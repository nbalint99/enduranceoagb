package hu.bme.aut.android.enduranceoagb

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import hu.bme.aut.android.enduranceoagb.adapter.TeamAdapter
import hu.bme.aut.android.enduranceoagb.data.AllTeams
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityTeamBinding
import hu.bme.aut.android.enduranceoagb.fragments.QualiFragment
import hu.bme.aut.android.enduranceoagb.fragments.QualificationFragment
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

class TeamActivity : AppCompatActivity(), TeamAdapter.TeamItemClickListener, QualiFragment.QualiListener {
    private lateinit var binding : ActivityTeamBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRef2: DatabaseReference

    private lateinit var adapter: TeamAdapter

    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
    }

    private var raceId: String? = null

    private val c = Calendar.getInstance()

    private val year = c.get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)

        initRecyclerView()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val teamsDone = p0.result.child("Info").child("hasTeamsDone").value.toString().toInt()
                val numberOfTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                val groupDone = p0.result.child("Info").child("hasGroupDone").value.toString().toBooleanStrictOrNull()
                binding.divideButton.isVisible = teamsDone == numberOfTeams && groupDone == false
                binding.btnQuali.isVisible = groupDone == true
            }
        }

        binding.btnQuali.setOnClickListener {
            dbRef.get().addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val numberOfTeams =
                        p0.result.child("Info").child("numberOfTeams").value.toString().toInt()

                    for (ele in 1..numberOfTeams) {
                        if (!p0.result.child("Quali")
                                .child(ele.toString())
                                .exists()
                        ) {
                            dbRef.child("Quali").child(ele.toString())
                                .child("team")
                                .setValue("Nincs még eredmény!")
                        }
                    }
                    val showDetailsIntent = Intent()
                    showDetailsIntent.setClass(this@TeamActivity, QualiActivity::class.java)
                    showDetailsIntent.putExtra(TeamActivity.EXTRA_RACE_NAME, raceId)
                    startActivity(showDetailsIntent)
                }
            }
        }



        binding.divideButton.setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Figyelem!")
            builder.setMessage("Biztos, hogy meg akarod csinálni a csoportbontást?")

            builder.setPositiveButton(R.string.button_ok) { _, _ ->

                dbRef =
                    FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference("Races").child(raceId.toString())

                val items: MutableList<Teams> = mutableListOf()

                dbRef.get().addOnCompleteListener { p0 ->
                    if (p0.isSuccessful) {
                        dbRef2 =
                            FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference(year.toString())

                        dbRef2.get().addOnCompleteListener { p1 ->
                            if (p1.isSuccessful) {
                                for (element in p0.result.child("Teams").children) {
                                    val addTeam = Teams(
                                        element.child("Info").child("nameTeam").value.toString(),
                                        element.child("Info").child("people").value.toString()
                                            .toInt(),
                                        element.child("Info").child("teamNumber").value.toString()
                                            .toIntOrNull(),
                                        element.child("Info").child("avgWeight").value.toString()
                                            .toDoubleOrNull(),
                                        element.child("Info")
                                            .child("hasDriversDone").value.toString().toInt(),
                                        element.child("Info")
                                            .child("startKartNumber").value.toString()
                                            .toIntOrNull(),
                                        element.child("Info").child("hasQualiDone").value.toString()
                                            .toBoolean(),
                                        element.child("Info").child("stintsDone").value.toString()
                                            .toIntOrNull(),
                                        element.child("Info").child("gp2").value.toString()
                                            .toBooleanStrictOrNull(),
                                        element.child("Info").child("points").value.toString()
                                            .toIntOrNull(),
                                        element.child("Info")
                                            .child("shortTeamName").value.toString(),
                                        element.child("Info").child("group").value.toString()
                                            .toIntOrNull()
                                    )
                                    items.add(addTeam)
                                }

                                val sortedItems = items.sortedWith(compareByDescending{ it.points })
                                val numberOfTeams =
                                    p0.result.child("Info").child("numberOfTeams").value.toString()
                                        .toInt()
                                var setSecondGroup = false
                                if (numberOfTeams >= 10) {
                                    val divideGroup = ceil(numberOfTeams.toDouble() / 2.0)
                                    var firstTeam = 1
                                    var group1 = 0
                                    var group2 = 0
                                    for (e in sortedItems) {
                                        if (firstTeam < divideGroup.toInt()) {
                                            dbRef.child("Teams").child(e.nameTeam).child("Info")
                                                .child("group").setValue(1)
                                            sortedItems[firstTeam-1].group = 1
                                            group1++
                                            firstTeam++
                                        } else if (firstTeam > divideGroup.toInt()) {
                                            dbRef.child("Teams").child(e.nameTeam).child("Info")
                                                .child("group").setValue(2)
                                            if (!setSecondGroup) {
                                                dbRef.child("Info").child("secondGroup").setValue(firstTeam)
                                                setSecondGroup = true
                                            }
                                            sortedItems[firstTeam-1].group = 2
                                            group2++
                                            firstTeam++
                                        } else if (firstTeam == divideGroup.toInt()) {
                                            if (e.gp2 == true) {
                                                dbRef.child("Teams").child(e.nameTeam)
                                                    .child("Info").child("group")
                                                    .setValue(2)
                                                if (!setSecondGroup) {
                                                    dbRef.child("Info").child("secondGroup").setValue(firstTeam)
                                                    setSecondGroup = true
                                                }
                                                sortedItems[firstTeam-1].group = 2
                                                group2++
                                                firstTeam++
                                            } else if (e.gp2 == false) {
                                                dbRef.child("Teams").child(e.nameTeam)
                                                    .child("Info").child("group")
                                                    .setValue(1)
                                                sortedItems[firstTeam-1].group = 1
                                                group1++
                                                firstTeam++
                                            }
                                        }
                                    }
                                    if (group1 > group2) {
                                        dbRef.child("Info").child("firstMore").setValue(true)
                                        dbRef.child("Info").child("secondMore").setValue(false)
                                        dbRef.child("Info").child("equalGroup").setValue(false)
                                    }
                                    else if (group1 < group2) {
                                        dbRef.child("Info").child("firstMore").setValue(false)
                                        dbRef.child("Info").child("secondMore").setValue(true)
                                        dbRef.child("Info").child("equalGroup").setValue(false)
                                    }
                                    else if (group1 == group2) {
                                        dbRef.child("Info").child("firstMore").setValue(false)
                                        dbRef.child("Info").child("secondMore").setValue(false)
                                        dbRef.child("Info").child("equalGroup").setValue(true)
                                    }
                                }
                                else {
                                    dbRef.child("Info").child("secondGroup").setValue(1)
                                }
                                dbRef.child("Info").child("hasGroupDone").setValue(true)

                                runOnUiThread {
                                    adapter.update2(sortedItems.toMutableList())
                                }

                                recreate()
                            }
                        }

                    }
                }
            }
            builder.setNeutralButton(R.string.button_megse, null)
            builder.show()
        }

        binding.fab.setOnClickListener {
            dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

            val items : MutableList<Teams> = mutableListOf()

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
                        items.add(addTeam)
                    }

                    val numberOfTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                    val missingTeams = numberOfTeams - items.size

                    if (missingTeams != 0) {
                        runOnUiThread {
                            dbRef2 =
                                FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                                    .getReference(year.toString())

                            val items2: MutableList<AllTeams> = mutableListOf()

                            dbRef2.get().addOnCompleteListener { p0 ->
                                if (p0.isSuccessful) {
                                    for (element in p0.result.child("Teams").children) {
                                        val addTeam2 = AllTeams(
                                            element.child("nameTeam").value.toString(),
                                            element.child("people").value.toString()
                                                .toIntOrNull(),
                                            element.child("joker").value.toString()
                                                .toIntOrNull(),
                                            element.child("hasJokerRaced").value.toString()
                                                .toBooleanStrictOrNull(),
                                            element.child("points").value.toString().toIntOrNull(),
                                            element.child("oldPoints").value.toString().toIntOrNull(),
                                            element.child("gp2Points").value.toString().toIntOrNull(),
                                            element.child("oldGp2Points").value.toString().toIntOrNull(),
                                            element.child("gp2").value.toString()
                                                .toBooleanStrictOrNull(),
                                            element.child("racesTeam").value.toString()
                                                .toInt(),
                                            element.child("totalPoints").value.toString().toIntOrNull(),
                                            element.child("totalGp2Points").value.toString().toIntOrNull(),
                                            element.child("one").value.toString().toIntOrNull(),
                                            element.child("two").value.toString().toIntOrNull(),
                                            element.child("three").value.toString().toIntOrNull(),
                                            element.child("four").value.toString().toIntOrNull(),
                                            element.child("five").value.toString().toIntOrNull(),
                                            element.child("six").value.toString().toIntOrNull(),
                                            element.child("seven").value.toString().toIntOrNull(),
                                            element.child("eight").value.toString().toIntOrNull(),
                                            element.child("nine").value.toString().toIntOrNull(),
                                            element.child("ten").value.toString().toIntOrNull(),
                                            element.child("eleven").value.toString().toIntOrNull(),
                                            element.child("twelve").value.toString().toIntOrNull(),
                                            element.child("thirteen").value.toString().toIntOrNull(),
                                            element.child("fourteen").value.toString().toIntOrNull(),
                                            element.child("fifteen").value.toString().toIntOrNull(),
                                            element.child("oneGp2").value.toString().toIntOrNull(),
                                            element.child("twoGp2").value.toString().toIntOrNull(),
                                            element.child("threeGp2").value.toString().toIntOrNull(),
                                            element.child("fourGp2").value.toString().toIntOrNull(),
                                            element.child("fiveGp2").value.toString().toIntOrNull(),
                                            element.child("sixGp2").value.toString().toIntOrNull(),
                                            element.child("sevenGp2").value.toString().toIntOrNull(),
                                            element.child("eightGp2").value.toString().toIntOrNull(),
                                            element.child("nineGp2").value.toString().toIntOrNull(),
                                            element.child("tenGp2").value.toString().toIntOrNull(),
                                            element.child("elevenGp2").value.toString().toIntOrNull(),
                                            element.child("twelveGp2").value.toString().toIntOrNull(),
                                            element.child("thirteenGp2").value.toString().toIntOrNull(),
                                            element.child("fourteenGp2").value.toString().toIntOrNull(),
                                            element.child("fifteenGp2").value.toString().toIntOrNull()
                                        )
                                        items2.add(addTeam2)
                                    }

                                    val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                                        this,
                                        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                                    )

                                    val inflater = this.layoutInflater
                                    val dialogView: View =
                                        inflater.inflate(R.layout.new_team_fragment, null)
                                    dialogBuilder.setView(dialogView)
                                    dialogBuilder.setTitle(R.string.newTeam)

                                    val teamName = dialogView.findViewById<Spinner>(R.id.spTeamName)
                                    val people =
                                        dialogView.findViewById<Spinner>(R.id.spPeopleNumber)

                                    val teamNameList: MutableList<String> = mutableListOf()
                                    val peopleList: MutableList<String> = mutableListOf()

                                    val selectOne = "-- Válassz egyet! --"
                                    teamNameList.add(0, selectOne)
                                    peopleList.add(0, selectOne)

                                    for (element in items2) {
                                        val addTeam = element.nameTeam
                                        var isExisted = false
                                        for (i in items) {
                                            val existed = i.nameTeam
                                            if (addTeam == existed) {
                                                isExisted = true
                                            }
                                        }
                                        if (!isExisted) {
                                            teamNameList.add(addTeam)
                                        }
                                    }

                                    for (element in 1..3) {
                                        peopleList.add(element.toString())
                                    }

                                    teamName.adapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        teamNameList
                                    )


                                    people.adapter = ArrayAdapter(
                                        this,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        peopleList
                                    )


                                    dialogBuilder.setPositiveButton(R.string.button_ok) { _, _ ->
                                        val selectedTeam: String = teamName.selectedItem.toString()
                                        val selectedPeople: String = people.selectedItem.toString()

                                        var gp2Selected: Boolean? = null
                                        var peopleInTeam: Int? = null
                                        for (element in items2) {
                                            if (element.nameTeam == selectedTeam) {
                                                gp2Selected = element.gp2
                                                peopleInTeam = element.people
                                            }
                                        }
                                        if (selectedTeam != selectOne && selectedPeople != selectOne && selectedPeople.toInt() <= peopleInTeam.toString().toInt()
                                        ) {
                                            if (gp2Selected != null) {
                                                onTeamCreated(
                                                    selectedTeam,
                                                    selectedPeople.toInt(),
                                                    gp2Selected
                                                )
                                            }
                                            if (missingTeams > 1) {
                                                val snack = Snackbar.make(
                                                    binding.root,
                                                    "Még ${missingTeams - 1} csapatot fel kell venned!",
                                                    Snackbar.LENGTH_LONG
                                                )
                                                snack.show()
                                            }

                                        }
                                        else {
                                            AlertDialog.Builder(this)
                                                .setTitle(R.string.warning)
                                                .setMessage(R.string.notValid)
                                                .setPositiveButton(R.string.button_ok, null)
                                                .setNegativeButton("", null)
                                                .show()
                                        }
                                    }
                                    dialogBuilder.setNegativeButton(R.string.button_megse, null)
                                    val alertDialog = dialogBuilder.create()
                                    alertDialog.show()
                                }
                            }
                        }
                    }
                    else {
                        val snack = Snackbar.make(binding.root,R.string.doneAddTeam, Snackbar.LENGTH_LONG)
                        snack.show()
                    }

                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@TeamActivity, RaceActivity::class.java)
        showDetailsIntent.putExtra(RaceActivity.EXTRA_RACE_NAME, raceId)
        startActivity(showDetailsIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.menu_home) {
            val myIntent = Intent(this@TeamActivity, MainActivity::class.java)

            startActivity(myIntent)
            return true
        }
        if (id == R.id.menu_etc) {
            val myIntent = Intent(this@TeamActivity, RaceActivity::class.java)
            myIntent.putExtra("extra.race_name", raceId)

            startActivity(myIntent)
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    private fun initRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        binding.rvMainTeam.layoutManager = layoutManager

        adapter = TeamAdapter(this)

        binding.rvMainTeam.adapter = adapter

        loadItemsInBackground()

    }

    private fun loadItemsInBackground() {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val items : MutableList<Teams> = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                dbRef2 =
                    FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference(year.toString())

                dbRef2.get().addOnCompleteListener { p1 ->
                    if (p1.isSuccessful) {
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
                            items.add(addTeam)
                        }

                        val sortedItems = items.sortedWith(compareBy<Teams> { it.teamNumber }.thenByDescending { it.points })
                        runOnUiThread {
                            adapter.update2(sortedItems.toMutableList())
                        }
                        val numberOfTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                        val missingTeams = numberOfTeams - items.size
                        if (missingTeams != 0) {
                            val snack = Snackbar.make(binding.root,"Még $missingTeams csapatot fel kell venned!", Snackbar.LENGTH_LONG)
                            snack.show()
                        }
                    }
                }

            }
        }
    }

    override fun onTeamCreated(nameTeam: String, people: Int, gp2: Boolean) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef2 =
            FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference(year.toString())
        //dbRef2.child("Teams").child(nameTeam).child("racesTeam").setValue(ServerValue.increment(1))
        dbRef2.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val points = p0.result.child("Teams").child(nameTeam).child("points").value.toString().toIntOrNull()
                val newItem = Teams(nameTeam, people, null, null, 0, null, false, null, gp2, points, null,0)
                dbRef.child("Teams").child(nameTeam).child("Info").setValue(newItem)

                runOnUiThread {
                    adapter.addItem(newItem)
                }
            }
        }
    }

    override fun onTeamSelected(nameTeam: String?, teamNumber: String?, people: Int?, startKartNumber: Int?, gp2: Boolean?, group: Int?) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val builder = AlertDialog.Builder(this)
                val initDone = p0.result.child("Info").child("hasStintReady").value.toString().toBoolean()
                if (initDone) {
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Mostmár nem módosíthatod a csapatokat, mivel már elkezdődött a verseny!")
                    builder.setPositiveButton(R.string.button_ok, null)
                    builder.show()
                }
                val groupDone = p0.result.child("Info").child("hasGroupDone").value.toString().toBoolean()
                if (!groupDone) {
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Még nem adhatsz meg csapatszámot, illetve gép számot, amíg nem csináltad meg a csoportosztást!")
                    builder.setPositiveButton(R.string.button_ok, null)
                    builder.show()
                }
                else {
                    val fragment = QualiFragment.newInstance(nameTeam.toString(), teamNumber.toString(), people.toString(), startKartNumber.toString(), gp2.toString(), group)
                    fragment.show(supportFragmentManager, "QualiFragment")
                }
            }
        }
    }

    override fun onItemClick(nameTeam: String?, teamNumber: String?, gp2: Boolean?) {
        dbRef2 =
            FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference(year.toString()).child("Teams").child(nameTeam.toString())

        val items: MutableList<Drivers> = mutableListOf()

        val driversList: MutableList<String> = mutableListOf()

        dbRef2.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.child("Drivers").children) {
                    val addDriver = Drivers(
                        element.child("nameDriver").value.toString(),
                        element.child("weight").value.toString().toDoubleOrNull(),
                        element.child("races").value.toString().toIntOrNull(),
                        element.child("joker").value.toString().toBooleanStrictOrNull())

                    items.add(addDriver)
                }
                for (i in items) {
                    val driverName = i.nameDriver
                    driversList.add(driverName)
                }



            val showDetailsIntent = Intent()
            showDetailsIntent.setClass(this@TeamActivity, DriverActivity::class.java)
            showDetailsIntent.putExtra(DriverActivity.EXTRA_RACE_NAME, raceId)
            showDetailsIntent.putExtra(DriverActivity.EXTRA_TEAM_NUMBER, teamNumber.toString())
            showDetailsIntent.putExtra(DriverActivity.EXTRA_NAMETEAM, nameTeam.toString())
            showDetailsIntent.putExtra(DriverActivity.EXTRA_GP2, gp2.toString())
            showDetailsIntent.putExtra(DriverActivity.EXTRA_TEAM_MEMBERS, driversList.joinToString(","))
            startActivity(showDetailsIntent)
            }
        }
    }

    override fun onItemLongClick(team: Teams?): Boolean {
        val builder = AlertDialog.Builder(this)

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val initDone = p0.result.child("Info").child("hasStintReady").value.toString().toBoolean()
                if (initDone) {
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Mostmár nem törölheted a csapatokat, mivel már elkezdődött a verseny!")
                    builder.setPositiveButton(R.string.button_ok, null)
                    builder.show()

                } else if (!initDone) {
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Biztos, hogy törölni szeretnéd ezt a csapatot?")

                    builder.setPositiveButton(R.string.button_ok) { dialog, which ->
                        adapter.deleteItem(team!!)

                        dbRef.get().addOnCompleteListener { p0 ->
                            if (p0.isSuccessful) {
                                val list = p0.result.child("Teams").children
                                for (element in list) {
                                    if (element.child("Info").child("nameTeam").value.toString() == team.nameTeam) {
                                        dbRef2 =
                                            FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                                                .getReference(year.toString())
                                        if (p0.result.child("Teams").child(team.nameTeam).child("Info")
                                                .child("hasDriversDone").value.toString().toInt() == p0.result.child("Teams").child(team.nameTeam).child("Info")
                                                .child("people").value.toString().toInt()
                                        ) {
                                            val teamsDone = p0.result.child("Info")
                                                .child("hasTeamsDone").value.toString().toInt()
                                            dbRef.child("Info").child("hasTeamsDone")
                                                .setValue(teamsDone - 1)
                                        }
                                        val listItems = element.child("Drivers").children
                                        for (i in listItems) {
                                            val listDrivers = p0.result.child("Drivers").children
                                            for (elem in listDrivers) {
                                                if (elem.child("nameDriver").value.toString() == i.child("nameDriver").value.toString()) {
                                                    val deleteDriver = elem.child("nameDriver").value.toString()
                                                    val key = elem.ref.key.toString()
                                                    dbRef.child("Drivers").child(key).removeValue()
                                                    dbRef2.child("Teams").child(team.nameTeam).child("Drivers").child(deleteDriver).child("races").setValue(ServerValue.increment(-1))
                                                }
                                            }
                                        }
                                        if (p0.result.child("Teams").child(team.nameTeam).child("Info").child("hasQualiDone").value.toString().toBoolean()) {
                                            dbRef.child("Teams").child(team.nameTeam).child("Info").child("hasQualiDone").setValue(false)
                                            val qualiDone = p0.result.child("Info")
                                                .child("hasQualiDone").value.toString().toInt()
                                            dbRef.child("Info").child("hasQualiDone").setValue(qualiDone - 1)
                                        }

                                        dbRef.child("Teams").child(team.nameTeam).removeValue()
                                        //dbRef2.child("Teams").child(team.nameTeam).child("racesTeam").setValue(ServerValue.increment(-1))
                                    }
                                }
                            }
                        }
                    }
                    builder.setNeutralButton(R.string.button_megse, null)
                    builder.show()
                }
            }
        }
        return true
    }

    override fun onQualiCreated(teamName: String, teamNumber: Int?, kartNumber: Int?, group: Int?) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val teamNumbers: MutableList<Int>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val list = p0.result.child("Teams").children
                val list2 = p0.result.child("Teams").children
                val list3 = p0.result.child("Teams").children
                for (each in list2) {
                    if (each.child("Info").child("teamNumber").value.toString().toIntOrNull() != null) {
                        teamNumbers?.add(each.child("Info").child("teamNumber").value.toString().toInt())
                    }
                }
                var group1 = 0
                var group2 = 0
                for (each in list3) {
                    if (each.child("Info").child("group").value.toString().toIntOrNull() == 1) {
                        group1++
                    }
                    if (each.child("Info").child("group").value.toString().toIntOrNull() == 2) {
                        group2++
                    }
                }
                val numberOfTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toDouble()
                var divideGroup = numberOfTeams / 2.0
                var groupEqual = false
                if (group1 < group2) {
                    ceil(divideGroup)
                }
                else if (group1 > group2) {
                    floor(divideGroup)
                }
                else if (group1 == group2) {
                    groupEqual = true
                    divideGroup += 0.5
                }
                for (element in list) {
                    if (element.child("Info").child("nameTeam").value.toString() == teamName) {

                        if (!element.child("Info").child("teamNumber").exists() && !element.child("Info").child("startKartNumber").exists()) {
                            if (teamNumber != null && kartNumber != null) {
                                if ((!groupEqual && ((group!!.toInt() == 1 && teamNumber <= divideGroup) || (group!!.toInt() == 2 && teamNumber > divideGroup))) ||
                                    (groupEqual && ((group!!.toInt() == 1 && teamNumber.toDouble() < divideGroup) || (group!!.toInt() == 2 && teamNumber.toDouble() > divideGroup)))) {
                                    val snack = Snackbar.make(binding.root,R.string.wrongGroup, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                                else {
                                    if (teamNumbers != null) {
                                        if (teamNumbers.contains(teamNumber)) {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.alreadyContains,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        } else {
                                            val numberOfTeams = p0.result.child("Info")
                                                .child("numberOfTeams").value.toString().toInt()
                                            if (teamNumber in 1..numberOfTeams && kartNumber in 1..20) {
                                                teamNumbers.add(teamNumber)
                                                val qualiDone = p0.result.child("Info")
                                                    .child("hasQualiDone").value.toString().toInt()
                                                dbRef.child("Info").child("hasQualiDone")
                                                    .setValue(qualiDone + 1)
                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("hasQualiDone").setValue(true)

                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("teamNumber").setValue(teamNumber)
                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("startKartNumber").setValue(kartNumber)
                                                finish()
                                                startActivity(intent)
                                            } else {
                                                val snack = Snackbar.make(
                                                    binding.root,
                                                    R.string.giveNormalNumber,
                                                    Snackbar.LENGTH_LONG
                                                )
                                                snack.show()
                                            }
                                        }
                                    } else {
                                        val numberOfTeams = p0.result.child("Info")
                                            .child("numberOfTeams").value.toString().toInt()
                                        if (teamNumber in 1..numberOfTeams && kartNumber in 1..20) {
                                            teamNumbers?.add(teamNumber)
                                            val qualiDone = p0.result.child("Info")
                                                .child("hasQualiDone").value.toString().toInt()
                                            dbRef.child("Info").child("hasQualiDone")
                                                .setValue(qualiDone + 1)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("hasQualiDone").setValue(true)

                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("teamNumber").setValue(teamNumber)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("startKartNumber").setValue(kartNumber)
                                            finish()
                                            startActivity(intent)
                                        } else {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.giveNormalNumber,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        }
                                    }
                                }
                            }
                            else if (teamNumber != null && kartNumber == null) {
                                if ((!groupEqual && ((group!!.toInt() == 1 && teamNumber <= divideGroup) || (group!!.toInt() == 2 && teamNumber > divideGroup))) ||
                                    (groupEqual && ((group!!.toInt() == 1 && teamNumber.toDouble() < divideGroup) || (group!!.toInt() == 2 && teamNumber.toDouble() > divideGroup)))) {
                                    val snack = Snackbar.make(binding.root,R.string.wrongGroup, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                                else {
                                    if (teamNumbers != null) {
                                        if (teamNumbers.contains(teamNumber)) {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.alreadyContains,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        } else {
                                            val numberOfTeams = p0.result.child("Info")
                                                .child("numberOfTeams").value.toString().toInt()
                                            if (teamNumber in 1..numberOfTeams) {
                                                teamNumbers.add(teamNumber)

                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("teamNumber").setValue(teamNumber)
                                                finish()
                                                startActivity(intent)
                                            } else {
                                                val snack = Snackbar.make(
                                                    binding.root,
                                                    R.string.giveNormalNumber,
                                                    Snackbar.LENGTH_LONG
                                                )
                                                snack.show()
                                            }
                                        }
                                    } else {
                                        val numberOfTeams = p0.result.child("Info")
                                            .child("numberOfTeams").value.toString().toInt()
                                        if (teamNumber in 1..numberOfTeams && kartNumber in 1..20) {
                                            teamNumbers?.add(teamNumber)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("teamNumber").setValue(teamNumber)
                                            finish()
                                            startActivity(intent)
                                        } else {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.giveNormalNumber,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        }
                                    }
                                }
                            }
                            else if (teamNumber == null && kartNumber != null) {
                                if (kartNumber in 1..20) {
                                    dbRef.child("Teams").child(teamName).child("Info").child("startKartNumber").setValue(kartNumber)
                                    finish()
                                    startActivity(intent)
                                }
                                else {
                                    val snack = Snackbar.make(binding.root,R.string.giveNormalNumber, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                            }
                            else if (teamNumber == null && kartNumber == null) {
                                val snack = Snackbar.make(binding.root,R.string.notGiveAnything, Snackbar.LENGTH_LONG)
                                snack.show()
                            }
                        }

                        else if (element.child("Info").child("teamNumber").exists() && element.child("Info").child("startKartNumber").exists()) {
                            if (teamNumber != null && kartNumber != null) {
                                val teamNumberPrev = element.child("Info").child("teamNumber").value.toString().toInt()
                                if ((!groupEqual && ((group!!.toInt() == 1 && teamNumber <= divideGroup) || (group!!.toInt() == 2 && teamNumber > divideGroup))) ||
                                    (groupEqual && ((group!!.toInt() == 1 && teamNumber.toDouble() < divideGroup) || (group!!.toInt() == 2 && teamNumber.toDouble() > divideGroup)))) {
                                    val snack = Snackbar.make(binding.root,R.string.wrongGroup, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                                else {
                                    if (teamNumbers!!.contains(teamNumber)) {
                                        if (teamNumber != teamNumberPrev) {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.alreadyContains,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        } else {
                                            if (kartNumber in 1..20) {
                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("startKartNumber").setValue(kartNumber)
                                                finish()
                                                startActivity(intent)
                                            } else {
                                                val snack = Snackbar.make(
                                                    binding.root,
                                                    R.string.giveNormalKart,
                                                    Snackbar.LENGTH_LONG
                                                )
                                                snack.show()
                                            }
                                        }
                                    } else {
                                        val numberOfTeams = p0.result.child("Info")
                                            .child("numberOfTeams").value.toString().toInt()
                                        if (teamNumber in 1..numberOfTeams && kartNumber in 1..20) {
                                            teamNumbers.remove(teamNumberPrev)
                                            teamNumbers.add(teamNumber)

                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("teamNumber").setValue(teamNumber)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("startKartNumber").setValue(kartNumber)
                                            finish()
                                            startActivity(intent)
                                        } else {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.giveNormalNumber,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        }
                                    }
                                }
                            }
                            else if (teamNumber != null && kartNumber == null) {
                                val teamNumberPrev = element.child("Info").child("teamNumber").value.toString().toInt()
                                if ((!groupEqual && ((group!!.toInt() == 1 && teamNumber <= divideGroup) || (group!!.toInt() == 2 && teamNumber > divideGroup))) ||
                                    (groupEqual && ((group!!.toInt() == 1 && teamNumber.toDouble() < divideGroup) || (group!!.toInt() == 2 && teamNumber.toDouble() > divideGroup)))) {
                                    val snack = Snackbar.make(binding.root,R.string.wrongGroup, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                                else {
                                    if (teamNumbers!!.contains(teamNumber)) {
                                        if (teamNumber != teamNumberPrev) {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.alreadyContains,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        }
                                    } else {
                                        val numberOfTeams = p0.result.child("Info")
                                            .child("numberOfTeams").value.toString().toInt()
                                        if (teamNumber in 1..numberOfTeams) {
                                            teamNumbers?.remove(teamNumberPrev)
                                            teamNumbers?.add(teamNumber)
                                            val qualiDone = p0.result.child("Info")
                                                .child("hasQualiDone").value.toString().toInt()
                                            dbRef.child("Info").child("hasQualiDone")
                                                .setValue(qualiDone - 1)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("hasQualiDone").setValue(false)

                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("teamNumber").setValue(teamNumber)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("startKartNumber").removeValue()
                                            finish()
                                            startActivity(intent)
                                        } else {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.giveNormalNumber,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        }
                                    }
                                }

                            }
                            else if (teamNumber == null && kartNumber != null) {
                                if (kartNumber in 1..20) {
                                    val qualiDone = p0.result.child("Info")
                                        .child("hasQualiDone").value.toString().toInt()
                                    dbRef.child("Info").child("hasQualiDone").setValue(qualiDone - 1)
                                    dbRef.child("Teams").child(teamName).child("Info").child("hasQualiDone").setValue(false)

                                    dbRef.child("Teams").child(teamName).child("Info").child("teamNumber").removeValue()
                                    dbRef.child("Teams").child(teamName).child("Info").child("startKartNumber").setValue(kartNumber)
                                    finish()
                                    startActivity(intent)
                                }
                                else {
                                    val snack = Snackbar.make(binding.root,R.string.giveNormalKart, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                            }
                            else if (teamNumber == null && kartNumber == null) {
                                val qualiDone = p0.result.child("Info")
                                    .child("hasQualiDone").value.toString().toInt()
                                dbRef.child("Info").child("hasQualiDone").setValue(qualiDone - 1)
                                dbRef.child("Teams").child(teamName).child("Info").child("hasQualiDone").setValue(false)

                                dbRef.child("Teams").child(teamName).child("Info").child("teamNumber").removeValue()
                                dbRef.child("Teams").child(teamName).child("Info").child("startKartNumber").removeValue()
                                finish()
                                startActivity(intent)
                            }
                        }

                        else if (element.child("Info").child("teamNumber").exists() && !element.child("Info").child("startKartNumber").exists()) {
                            if (teamNumber != null && kartNumber != null) {
                                val teamNumberPrev = element.child("Info").child("teamNumber").value.toString().toInt()
                                if ((!groupEqual && ((group!!.toInt() == 1 && teamNumber <= divideGroup) || (group!!.toInt() == 2 && teamNumber > divideGroup))) ||
                                    (groupEqual && ((group!!.toInt() == 1 && teamNumber.toDouble() < divideGroup) || (group!!.toInt() == 2 && teamNumber.toDouble() > divideGroup)))) {
                                    val snack = Snackbar.make(binding.root,R.string.wrongGroup, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                                else {
                                    if (teamNumbers!!.contains(teamNumber)) {
                                        if (teamNumber != teamNumberPrev) {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.alreadyContains,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        } else {
                                            if (kartNumber in 1..20) {
                                                val qualiDone = p0.result.child("Info")
                                                    .child("hasQualiDone").value.toString().toInt()
                                                dbRef.child("Info").child("hasQualiDone")
                                                    .setValue(qualiDone + 1)
                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("hasQualiDone").setValue(true)

                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("startKartNumber").setValue(kartNumber)
                                                finish()
                                                startActivity(intent)
                                            } else {
                                                val snack = Snackbar.make(
                                                    binding.root,
                                                    R.string.giveNormalKart,
                                                    Snackbar.LENGTH_LONG
                                                )
                                                snack.show()
                                            }
                                        }
                                    } else {
                                        val numberOfTeams = p0.result.child("Info")
                                            .child("numberOfTeams").value.toString().toInt()
                                        if (teamNumber in 1..numberOfTeams && kartNumber in 1..20) {
                                            teamNumbers.remove(teamNumberPrev)
                                            teamNumbers.add(teamNumber)

                                            val qualiDone = p0.result.child("Info")
                                                .child("hasQualiDone").value.toString().toInt()
                                            dbRef.child("Info").child("hasQualiDone")
                                                .setValue(qualiDone + 1)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("hasQualiDone").setValue(true)

                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("teamNumber").setValue(teamNumber)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("startKartNumber").setValue(kartNumber)
                                            finish()
                                            startActivity(intent)
                                        } else {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.giveNormalNumber,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        }
                                    }
                                }
                            }
                            else if (teamNumber != null && kartNumber == null) {
                                val teamNumberPrev = element.child("Info").child("teamNumber").value.toString().toInt()
                                if ((!groupEqual && ((group!!.toInt() == 1 && teamNumber <= divideGroup) || (group!!.toInt() == 2 && teamNumber > divideGroup))) ||
                                    (groupEqual && ((group!!.toInt() == 1 && teamNumber.toDouble() < divideGroup) || (group!!.toInt() == 2 && teamNumber.toDouble() > divideGroup)))) {
                                    val snack = Snackbar.make(binding.root,R.string.wrongGroup, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                                else {
                                    if (teamNumbers!!.contains(teamNumber)) {
                                        if (teamNumber != teamNumberPrev) {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.alreadyContains,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        }
                                    } else {
                                        val numberOfTeams = p0.result.child("Info")
                                            .child("numberOfTeams").value.toString().toInt()
                                        if (teamNumber in 1..numberOfTeams) {
                                            teamNumbers?.remove(teamNumberPrev)
                                            teamNumbers?.add(teamNumber)

                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("teamNumber").setValue(teamNumber)
                                            finish()
                                            startActivity(intent)
                                        } else {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.giveNormalNumber,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        }
                                    }
                                }
                            }
                            else if (teamNumber == null && kartNumber != null) {
                                if (kartNumber in 1..20) {
                                    dbRef.child("Teams").child(teamName).child("Info").child("teamNumber").removeValue()
                                    dbRef.child("Teams").child(teamName).child("Info").child("startKartNumber").setValue(kartNumber)
                                    finish()
                                    startActivity(intent)
                                }
                                else {
                                    val snack = Snackbar.make(binding.root,R.string.giveNormalKart, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                            }
                            else if (teamNumber == null && kartNumber == null) {
                                dbRef.child("Teams").child(teamName).child("Info").child("teamNumber").removeValue()
                                finish()
                                startActivity(intent)
                            }
                        }

                        else if (!element.child("Info").child("teamNumber").exists() && element.child("Info").child("startKartNumber").exists()) {
                            if (teamNumber != null && kartNumber != null) {
                                if ((!groupEqual && ((group!!.toInt() == 1 && teamNumber <= divideGroup) || (group!!.toInt() == 2 && teamNumber > divideGroup))) ||
                                    (groupEqual && ((group!!.toInt() == 1 && teamNumber.toDouble() < divideGroup) || (group!!.toInt() == 2 && teamNumber.toDouble() > divideGroup)))) {
                                    val snack = Snackbar.make(binding.root,R.string.wrongGroup, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                                else {
                                    if (teamNumbers != null) {
                                        if (teamNumbers.contains(teamNumber)) {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.alreadyContains,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        } else {
                                            val numberOfTeams = p0.result.child("Info")
                                                .child("numberOfTeams").value.toString().toInt()
                                            if (teamNumber in 1..numberOfTeams && kartNumber in 1..20) {
                                                teamNumbers.add(teamNumber)

                                                val qualiDone = p0.result.child("Info")
                                                    .child("hasQualiDone").value.toString().toInt()
                                                dbRef.child("Info").child("hasQualiDone")
                                                    .setValue(qualiDone + 1)
                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("hasQualiDone").setValue(true)

                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("teamNumber").setValue(teamNumber)
                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("startKartNumber").setValue(kartNumber)
                                                finish()
                                                startActivity(intent)
                                            } else {
                                                val snack = Snackbar.make(
                                                    binding.root,
                                                    R.string.giveNormalNumber,
                                                    Snackbar.LENGTH_LONG
                                                )
                                                snack.show()
                                            }
                                        }
                                    } else {
                                        val numberOfTeams = p0.result.child("Info")
                                            .child("numberOfTeams").value.toString().toInt()
                                        if (teamNumber in 1..numberOfTeams && kartNumber in 1..20) {
                                            teamNumbers?.add(teamNumber)

                                            val qualiDone = p0.result.child("Info")
                                                .child("hasQualiDone").value.toString().toInt()
                                            dbRef.child("Info").child("hasQualiDone")
                                                .setValue(qualiDone + 1)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("hasQualiDone").setValue(true)

                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("teamNumber").setValue(teamNumber)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("startKartNumber").setValue(kartNumber)
                                            finish()
                                            startActivity(intent)
                                        } else {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.giveNormalNumber,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        }
                                    }
                                }

                            }
                            else if (teamNumber != null && kartNumber == null) {
                                if ((!groupEqual && ((group!!.toInt() == 1 && teamNumber <= divideGroup) || (group!!.toInt() == 2 && teamNumber > divideGroup))) ||
                                    (groupEqual && ((group!!.toInt() == 1 && teamNumber.toDouble() < divideGroup) || (group!!.toInt() == 2 && teamNumber.toDouble() > divideGroup)))) {
                                    val snack = Snackbar.make(binding.root,R.string.wrongGroup, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                                else {
                                    if (teamNumbers != null) {
                                        if (teamNumbers.contains(teamNumber)) {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.alreadyContains,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        } else {
                                            val numberOfTeams = p0.result.child("Info")
                                                .child("numberOfTeams").value.toString().toInt()
                                            if (teamNumber in 1..numberOfTeams) {
                                                teamNumbers.add(teamNumber)

                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("teamNumber").setValue(teamNumber)
                                                dbRef.child("Teams").child(teamName).child("Info")
                                                    .child("startKartNumber").removeValue()
                                                finish()
                                                startActivity(intent)
                                            } else {
                                                val snack = Snackbar.make(
                                                    binding.root,
                                                    R.string.giveNormalNumber,
                                                    Snackbar.LENGTH_LONG
                                                )
                                                snack.show()
                                            }
                                        }
                                    } else {
                                        val numberOfTeams = p0.result.child("Info")
                                            .child("numberOfTeams").value.toString().toInt()
                                        if (teamNumber in 1..numberOfTeams) {
                                            teamNumbers?.add(teamNumber)

                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("teamNumber").setValue(teamNumber)
                                            dbRef.child("Teams").child(teamName).child("Info")
                                                .child("startKartNumber").removeValue()
                                            finish()
                                            startActivity(intent)
                                        } else {
                                            val snack = Snackbar.make(
                                                binding.root,
                                                R.string.giveNormalNumber,
                                                Snackbar.LENGTH_LONG
                                            )
                                            snack.show()
                                        }
                                    }
                                }

                            }
                            else if (teamNumber == null && kartNumber != null) {
                                if (kartNumber in 1..20) {
                                    dbRef.child("Teams").child(teamName).child("Info").child("startKartNumber").setValue(kartNumber)
                                    finish()
                                    startActivity(intent)
                                }
                                else {
                                    val snack = Snackbar.make(binding.root,R.string.giveNormalKart, Snackbar.LENGTH_LONG)
                                    snack.show()
                                }
                            }
                            else if (teamNumber == null && kartNumber == null) {
                                dbRef.child("Teams").child(teamName).child("Info").child("startKartNumber").removeValue()
                                finish()
                                startActivity(intent)
                            }

                        }

                    }
                }
            }
        }
    }

}