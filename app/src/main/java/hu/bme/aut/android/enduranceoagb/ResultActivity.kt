package hu.bme.aut.android.enduranceoagb

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import hu.bme.aut.android.enduranceoagb.adapter.ResultAdapter
import hu.bme.aut.android.enduranceoagb.adapter.TeamCheckAdapter
import hu.bme.aut.android.enduranceoagb.data.DoneStint
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityResultBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityTeamcheckBinding
import hu.bme.aut.android.enduranceoagb.fragments.NewStintFragment
import hu.bme.aut.android.enduranceoagb.fragments.ResultFragment
import java.util.*

class ResultActivity : AppCompatActivity(), ResultAdapter.ResultItemClickListener, ResultFragment.ResultFragmentListener {
    private lateinit var binding: ActivityResultBinding

    private lateinit var dbRef: DatabaseReference

    private lateinit var dbRef2: DatabaseReference

    private lateinit var dbRef3: DatabaseReference

    private val c = Calendar.getInstance()

    private val year = c.get(Calendar.YEAR)

    private lateinit var adapter: ResultAdapter

    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
        const val EXTRA_TEAM_NUMBER = "extra.team_number"
        const val EXTRA_NAMETEAM= "extra.nameteam"
    }

    private var raceId: String? = null
    private var teamId: String? = null
    private var teamName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)
        teamId = intent.getStringExtra(EXTRA_TEAM_NUMBER)
        teamName = intent.getStringExtra(EXTRA_NAMETEAM)

        binding.podium.visibility = View.INVISIBLE

        binding.podium.setOnClickListener {
            publishFinalResults()
        }

        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.menu_home) {
            val myIntent = Intent(this@ResultActivity, MainActivity::class.java)

            startActivity(myIntent)
            return true
        }
        if (id == R.id.menu_etc) {
            val myIntent = Intent(this@ResultActivity, RaceActivity::class.java)
            myIntent.putExtra("extra.race_name", raceId)

            startActivity(myIntent)
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    private fun initRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        binding.rvResult.layoutManager = layoutManager

        adapter = ResultAdapter(this)

        binding.rvResult.adapter = adapter

        binding.rvResult.setItemViewCacheSize(16)

        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val items : MutableList<Teams>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.child("Teams").children) {
                    val addTeam = Teams(
                        element.child("Info").child("nameTeam").value.toString(),
                        element.child("Info").child("people").value.toString().toInt(),
                        element.child("Info").child("teamNumber").value.toString().toIntOrNull(),
                        element.child("Info").child("avgWeight").value.toString().toDoubleOrNull(),
                        element.child("Info").child("hasDriversDone").value.toString().toInt(),
                        element.child("Info").child("startKartNumber").value.toString().toIntOrNull(),
                        element.child("Info").child("hasQualiDone").value.toString().toBoolean(),
                        element.child("Info").child("stintsDone").value.toString().toIntOrNull(),
                        element.child("Info").child("gp2").value.toString().toBooleanStrictOrNull(),
                        element.child("Info").child("points").value.toString().toIntOrNull(),
                        element.child("Info").child("shortTeamName").value.toString(),
                        element.child("Info").child("group").value.toString().toIntOrNull()
                    )

                    items?.add(addTeam)
                }
                val sortedItems = items?.sortedBy { it.teamNumber }

                if ((items?.size == 0 || items?.size == null)) {
                    val snack = Snackbar.make(binding.root,R.string.noTeam, Snackbar.LENGTH_LONG)
                    snack.show()
                }
                else {
                    loadResults(sortedItems!!.toMutableList())
                }
            }
        }
    }

    private fun loadResults(sortedItems: MutableList<Teams>?) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val results : MutableList<String>? = mutableListOf()

        var done = 0

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.child("Result").children) {
                    var addString: String
                    if (element.child("gp2").value.toString().toBoolean()) {
                        val passString =
                            element.child("team").value.toString()
                        addString = "$passString (GP2)"
                    } else {
                        addString = element.child("team").value.toString()
                    }

                    if (addString != "Nincs még eredmény!") {
                        done++
                    }

                    results?.add(addString)
                }
            }
            runOnUiThread {
                if (done == results?.size) {
                    binding.podium.visibility = View.VISIBLE
                }
                adapter.update2(sortedItems!!.toMutableList())
                adapter.update3(results!!.toMutableList())
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@ResultActivity, RaceActivity::class.java)
        showDetailsIntent.putExtra(RaceActivity.EXTRA_RACE_NAME, raceId)
        startActivity(showDetailsIntent)
    }

    override fun onTeamSelected(position: String?, number: String?, gp2: Boolean?) {
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@ResultActivity, DetailsTeamCheckActivity::class.java)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_RACE_NAME, raceId)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_NAMETEAM, position.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_TEAM_NUMBER, number.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_GP2, gp2.toString())
        startActivity(showDetailsIntent)
    }

    override fun onItemClick(position: String?, number: String?, gp2: Boolean?, itemId: Int?) {
        dbRef =
            FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val fragment = ResultFragment.newInstance(
                    number.toString()
                )
                fragment.show(supportFragmentManager, "ResultFragment")
            }
        }
    }

    override fun onResultCreated(result: Int, team: String/*, gp2: Boolean?*/) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                dbRef.child("Result").child(result.toString()).child("team")
                    .setValue(team)
                val teams = p0.result.child("Teams").children
                for (i in teams) {
                    val teamName = i.child("Info").child("shortTeamName").value.toString()
                    if (team == teamName) {
                        val longTeamName = i.child("Info").child("nameTeam").value.toString()
                        val gp2 = i.child("Info").child("gp2").value.toString().toBoolean()
                        dbRef.child("Result").child(result.toString()).child("gp2").setValue(gp2)
                        dbRef.child("Result").child(result.toString()).child("longTeamName").setValue(longTeamName)
                        break
                    }
                }
            }
        }
        loadItemsInBackground()
    }

    private fun publishFinalResults() {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val hasResultsDone = p0.result.child("Info").child("hasResultsDone").value.toString().toBooleanStrictOrNull()
                val numberOfRace = p0.result.child("Info").child("numberOfRace").value.toString().toInt()
                if (hasResultsDone == false) {
                    var gp2Result = 1
                    val results = p0.result.child("Result").children
                    var result = 1
                    for (i in results) {
                        val nameTeam = i.child("longTeamName").value.toString()
                        val gp2 = i.child("gp2").value.toString().toBoolean()
                        dbRef2 =
                            FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference(year.toString())
                        dbRef2.get().addOnCompleteListener { p1 ->
                            if (p1.isSuccessful) {
                                val currentPoints = p1.result.child("Teams").child(nameTeam)
                                    .child("points").value.toString().toInt()
                                val newPoints = currentPoints + pointGiving(result)
                                dbRef2.child("Teams").child(nameTeam).child("points")
                                    .setValue(newPoints)
                                dbRef2.child("Teams").child(nameTeam).child("totalPoints")
                                    .setValue(newPoints)
                                dbRef2.child("Teams").child(nameTeam).child("oldPoints")
                                    .setValue(currentPoints)
                                dbRef2.child("Teams").child(nameTeam).child(resultFun(result)).setValue(ServerValue.increment(1))
                                dbRef2.child("Teams").child(nameTeam).child("races").child(numberOfRace.toString()).child("result").setValue(result)
                                dbRef2.child("Teams").child(nameTeam).child("races").child(numberOfRace.toString()).child("points").setValue(pointGiving(result))

                                val teams = p0.result.child("Teams").children
                                var gp2Valid = 0
                                for (j in teams) {
                                    val isGP2 = j.child("Info").child("gp2").value.toString().toBoolean()
                                    if (isGP2) {
                                        gp2Valid++
                                    }
                                    val thisTeam = j.child("Info").child("nameTeam").value.toString()
                                    if (thisTeam == nameTeam) {
                                        val drivers = p0.result.child("Teams").child(thisTeam).child("Drivers").children
                                        for (e in drivers) {
                                            val driver = e.child("nameDriver").value.toString()

                                            val driverRaced =
                                                p1.result.child("Teams").child(thisTeam.toString())
                                                    .child("Drivers").child(driver)
                                                    .child("races").value.toString()
                                            if (driverRaced == "null" || driverRaced == "") {
                                                dbRef2.child("Teams").child(thisTeam.toString())
                                                    .child("Drivers").child(driver)
                                                    .child("nameDriver").setValue(driver)
                                                dbRef2.child("Teams").child(thisTeam.toString())
                                                    .child("Drivers").child(driver).child("races")
                                                    .setValue(1)
                                                dbRef2.child("Teams").child(thisTeam.toString())
                                                    .child("Drivers").child(driver).child("joker")
                                                    .setValue(false)
                                            } else {
                                                dbRef2.child("Teams").child(thisTeam.toString())
                                                    .child("Drivers").child(driver)
                                                    .child("nameDriver").setValue(driver)
                                                dbRef2.child("Teams").child(thisTeam.toString())
                                                    .child("Drivers").child(driver).child("races")
                                                    .setValue(ServerValue.increment(1))
                                                dbRef2.child("Teams").child(thisTeam).child("joker")
                                                    .setValue(ServerValue.increment(1))
                                                dbRef2.child("Teams").child(thisTeam.toString())
                                                    .child("Drivers").child(driver).child("joker")
                                                    .setValue(false)
                                            }
                                        }
                                        val jokers =
                                            p1.result.child("Teams").child(thisTeam)
                                                .child("joker").value.toString()
                                                .toIntOrNull()
                                        if (jokers == 4) {
                                            for (element in p1.result.child("Teams")
                                                .child(thisTeam)
                                                .child("Drivers").children) {
                                                val raceNumberByDriver =
                                                    element.child("races").value.toString()
                                                        .toInt()
                                                if (raceNumberByDriver == 0 || raceNumberByDriver == 1) {
                                                    val driverName =
                                                        element.child("nameDriver").value.toString()
                                                    dbRef2.child("Teams").child(thisTeam)
                                                        .child("Drivers")
                                                        .child(driverName)
                                                        .child("joker").setValue(true)
                                                }
                                            }
                                        }
                                    }
                                }
                                if (gp2 && gp2Valid >= 3) {
                                    val currentPointsGp2 = p1.result.child("Teams").child(nameTeam)
                                        .child("gp2Points").value.toString().toInt()
                                    val newPointsGp2 = currentPointsGp2 + pointGiving(gp2Result)
                                    dbRef2.child("Teams").child(nameTeam).child("gp2Points")
                                        .setValue(newPointsGp2)
                                    dbRef2.child("Teams").child(nameTeam).child("totalGp2Points")
                                        .setValue(newPointsGp2)
                                    dbRef2.child("Teams").child(nameTeam).child("oldGp2Points")
                                        .setValue(currentPointsGp2)
                                    dbRef2.child("Teams").child(nameTeam).child(resultFunGp2(gp2Result)).setValue(ServerValue.increment(1))
                                    dbRef2.child("Teams").child(nameTeam).child("races").child(numberOfRace.toString()).child("resultGp2").setValue(gp2Result)
                                    dbRef2.child("Teams").child(nameTeam).child("races").child(numberOfRace.toString()).child("pointsGp2").setValue(pointGiving(gp2Result))
                                    gp2Result++
                                }
                                result++
                                dbRef2.child("Teams").child(nameTeam).child("racesTeam").setValue(ServerValue.increment(1))

                            }
                        }
                    }
                    dbRef3 =
                        FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                            .getReference("/")
                    dbRef3.get().addOnCompleteListener { p2 ->
                        if (p2.isSuccessful) {
                            dbRef3.child("endTime").child("info").child("title").setValue("Endurance OAGB - Boxutca")
                            dbRef3.child("endTime").child("raceTime").child("serverTime").removeValue()
                        }
                    }

                    dbRef.child("Info").child("hasResultsDone").setValue(true)
                    val showDetailsIntent = Intent()
                    showDetailsIntent.setClass(this@ResultActivity, PodiumActivity::class.java)
                    showDetailsIntent.putExtra(TeamActivity.EXTRA_RACE_NAME, raceId)
                    startActivity(showDetailsIntent)
                }
                if (hasResultsDone == true) {
                    val builder2: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(
                        this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                    )

                    builder2.setTitle("A végeredmény már létrejött! Szeretnéd, hogy a mostani állás szerint legyen újrapontozva a verseny?")
                    builder2.setMessage(" Ha ez egy korábbi verseny, akkor nyomj NEMET! Ha módosuljon a pontozás, akkor nyomj IGENT!")
                    builder2.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.yes) { _, _ ->
                        var gp2Result = 1
                        val results = p0.result.child("Result").children
                        var result = 1
                        for (i in results) {
                            val nameTeam = i.child("longTeamName").value.toString()
                            val gp2 = i.child("gp2").value.toString().toBoolean()
                            dbRef2 =
                                FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                                    .getReference(year.toString())
                            dbRef2.get().addOnCompleteListener { p1 ->
                                if (p1.isSuccessful) {
                                    val currentPoints = p1.result.child("Teams").child(nameTeam)
                                        .child("oldPoints").value.toString().toInt()
                                    val newPoints = currentPoints + pointGiving(result)
                                    dbRef2.child("Teams").child(nameTeam).child("points")
                                        .setValue(newPoints)
                                    dbRef2.child("Teams").child(nameTeam).child("totalPoints")
                                        .setValue(newPoints)
                                    dbRef2.child("Teams").child(nameTeam).child("oldPoints")
                                        .setValue(currentPoints)
                                    val oldResult = p1.result.child("Teams").child(nameTeam).child("races").child(numberOfRace.toString()).child("result").value.toString().toInt()
                                    dbRef2.child("Teams").child(nameTeam).child(resultFun(oldResult)).setValue(ServerValue.increment(-1))
                                    dbRef2.child("Teams").child(nameTeam).child(resultFun(result)).setValue(ServerValue.increment(1))
                                    dbRef2.child("Teams").child(nameTeam).child("races").child(numberOfRace.toString()).child("result").setValue(result)
                                    dbRef2.child("Teams").child(nameTeam).child("races").child(numberOfRace.toString()).child("points").setValue(pointGiving(result))

                                    val teams = p0.result.child("Teams").children
                                    var gp2Valid = 0
                                    for (j in teams) {
                                        val isGP2 = j.child("Info").child("gp2").value.toString()
                                            .toBoolean()
                                        if (isGP2) {
                                            gp2Valid++
                                        }
                                    }

                                    if (gp2 && gp2Valid >= 3) {
                                        val currentPointsGp2 = p1.result.child("Teams").child(nameTeam)
                                            .child("oldGp2Points").value.toString().toInt()
                                        val newPointsGp2 = currentPointsGp2 + pointGiving(gp2Result)
                                        dbRef2.child("Teams").child(nameTeam).child("gp2Points")
                                            .setValue(newPointsGp2)
                                        dbRef2.child("Teams").child(nameTeam).child("totalGp2Points")
                                            .setValue(newPointsGp2)
                                        dbRef2.child("Teams").child(nameTeam).child("oldGp2Points")
                                            .setValue(currentPointsGp2)
                                        val oldGp2Result = p1.result.child("Teams").child(nameTeam).child("races").child(numberOfRace.toString()).child("resultGp2").value.toString().toInt()
                                        dbRef2.child("Teams").child(nameTeam).child(resultFunGp2(oldGp2Result)).setValue(ServerValue.increment(-1))
                                        dbRef2.child("Teams").child(nameTeam).child(resultFunGp2(gp2Result)).setValue(ServerValue.increment(1))
                                        dbRef2.child("Teams").child(nameTeam).child("races").child(numberOfRace.toString()).child("resultGp2").setValue(gp2Result)
                                        dbRef2.child("Teams").child(nameTeam).child("races").child(numberOfRace.toString()).child("pointsGp2").setValue(pointGiving(gp2Result))
                                        gp2Result++
                                    }
                                    result++
                                }
                            }
                        }
                        val showDetailsIntent = Intent()
                        showDetailsIntent.setClass(this@ResultActivity, PodiumActivity::class.java)
                        showDetailsIntent.putExtra(TeamActivity.EXTRA_RACE_NAME, raceId)
                        startActivity(showDetailsIntent)
                    }
                    builder2.setNegativeButton(R.string.no) { _, _ ->
                        val showDetailsIntent = Intent()
                        showDetailsIntent.setClass(this@ResultActivity, PodiumActivity::class.java)
                        showDetailsIntent.putExtra(TeamActivity.EXTRA_RACE_NAME, raceId)
                        startActivity(showDetailsIntent)
                    }
                    builder2.setNeutralButton(R.string.button_megse, null)
                    builder2.show()
                }
            }
        }
    }

    private fun pointGiving(result: Int): Int {
        when (result) {
            1 -> {
                return 25
            }
            2 -> {
                return 18
            }
            3 -> {
                return 15
            }
            4 -> {
                return 12
            }
            5 -> {
                return 10
            }
            6 -> {
                return 8
            }
            7 -> {
                return 6
            }
            8 -> {
                return 4
            }
            9 -> {
                return 2
            }
            10 -> {
                return 1
            }
            else -> {return 0}
        }
    }

    private fun resultFun(result: Int): String {
        when (result) {
            1 -> {
                return "one"
            }
            2 -> {
                return "two"
            }
            3 -> {
                return "three"
            }
            4 -> {
                return "four"
            }
            5 -> {
                return "five"
            }
            6 -> {
                return "six"
            }
            7 -> {
                return "seven"
            }
            8 -> {
                return "eight"
            }
            9 -> {
                return "nine"
            }
            10 -> {
                return "ten"
            }
            11 -> {
                return "eleven"
            }
            12 -> {
                return "twelve"
            }
            13 -> {
                return "thirteen"
            }
            14 -> {
                return "fourteen"
            }
            15 -> {
                return "fifteen"
            }
            else -> {return "other"}
        }
    }

    private fun resultFunGp2(result: Int): String {
        when (result) {
            1 -> {
                return "oneGp2"
            }
            2 -> {
                return "twoGp2"
            }
            3 -> {
                return "threeGp2"
            }
            4 -> {
                return "fourGp2"
            }
            5 -> {
                return "fiveGp2"
            }
            6 -> {
                return "sixGp2"
            }
            7 -> {
                return "sevenGp2"
            }
            8 -> {
                return "eightGp2"
            }
            9 -> {
                return "nineGp2"
            }
            10 -> {
                return "tenGp2"
            }
            11 -> {
                return "elevenGp2"
            }
            12 -> {
                return "twelveGp2"
            }
            13 -> {
                return "thirteenGp2"
            }
            14 -> {
                return "fourteenGp2"
            }
            15 -> {
                return "fifteenGp2"
            }
            else -> {return "other"}
        }
    }


    override fun raceId(): String? {
        return raceId
    }

}