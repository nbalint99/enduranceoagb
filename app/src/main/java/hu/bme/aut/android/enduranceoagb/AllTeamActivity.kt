package hu.bme.aut.android.enduranceoagb

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.ColumnInfo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.adapter.AllTeamAdapter
import hu.bme.aut.android.enduranceoagb.data.AllTeams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityAllteamBinding
import java.util.*

class AllTeamActivity : AppCompatActivity(), AllTeamAdapter.AllTeamItemClickListener {
    private lateinit var binding : ActivityAllteamBinding

    private lateinit var dbRef: DatabaseReference

    private lateinit var adapter: AllTeamAdapter

    private val c = Calendar.getInstance()

    private val year = c.get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllteamBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initRecyclerView()

        binding.fab.setOnClickListener {
            dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference(
                year.toString())

            val items : MutableList<AllTeams> = mutableListOf()

            dbRef.get().addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    for (element in p0.result.child("Teams").children) {
                        val addTeam = AllTeams(
                            element.child("nameTeam").value.toString(),
                            element.child("people").value.toString().toIntOrNull(),
                            element.child("joker").value.toString().toIntOrNull(),
                            element.child("hasJokerRaced").value.toString().toBooleanStrictOrNull(),
                            element.child("points").value.toString().toIntOrNull(),
                            element.child("oldPoints").value.toString().toIntOrNull(),
                            element.child("gp2Points").value.toString().toIntOrNull(),
                            element.child("oldGp2Points").value.toString().toIntOrNull(),
                            element.child("gp2").value.toString().toBooleanStrictOrNull(),
                            element.child("racesTeam").value.toString().toInt(),
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
                        items.add(addTeam)
                    }

                    runOnUiThread {
                        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                            this,
                            android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                        )

                        val inflater = this.layoutInflater
                        val dialogView: View =
                            inflater.inflate(R.layout.new_allteam_fragment, null)
                        dialogBuilder.setView(dialogView)
                        dialogBuilder.setTitle(R.string.newTeam)

                        val teamName = dialogView.findViewById<EditText>(R.id.etAllTeamName)
                        val gp2 = dialogView.findViewById<CheckBox>(R.id.cbAllGP2)


                        dialogBuilder.setPositiveButton(R.string.button_ok) { _, _ ->
                            if (teamName.text.toString()
                                    .isNotEmpty()) {
                                onTeamCreated(
                                    teamName.text.toString(),
                                    0,
                                    gp2.isChecked.toString().toBoolean()
                                )

                            } else {
                                AlertDialog.Builder(this)
                                    .setTitle(R.string.warning)
                                    .setMessage(R.string.validNot)
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
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@AllTeamActivity, MainActivity::class.java)
        startActivity(showDetailsIntent)
    }

    private fun initRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        binding.rvAllMainTeam.layoutManager = layoutManager

        adapter = AllTeamAdapter(this)

        binding.rvAllMainTeam.adapter = adapter

        loadItemsInBackground()

    }

    private fun loadItemsInBackground() {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference(year.toString())

        val items : MutableList<AllTeams> = mutableListOf()

        val itemsTotal : MutableList<AllTeams> = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.child("Teams").children) {
                    val addTeam = AllTeams(
                        element.child("nameTeam").value.toString(),
                        element.child("people").value.toString().toIntOrNull(),
                        element.child("joker").value.toString().toIntOrNull(),
                        element.child("hasJokerRaced").value.toString().toBooleanStrictOrNull(),
                        element.child("points").value.toString().toIntOrNull(),
                        element.child("oldPoints").value.toString().toIntOrNull(),
                        element.child("gp2Points").value.toString().toIntOrNull(),
                        element.child("oldGp2Points").value.toString().toIntOrNull(),
                        element.child("gp2").value.toString().toBooleanStrictOrNull(),
                        element.child("racesTeam").value.toString().toInt(),
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

                    items.add(addTeam)
                }

                val sortedItems = items.sortedByDescending { it.points }
                for (i in sortedItems) {
                    if (i.racesTeam == 9) {
                        var minimumPoint = 100
                        var minimumPointGp2 = 100
                        for (el in p0.result.child("Teams").child(i.nameTeam).child("races").children) {
                            val actualPoint = el.child("points").value.toString().toInt()
                            if (actualPoint < minimumPoint) {
                                minimumPoint = actualPoint
                            }
                            if (i.gp2 == true) {
                                val actualPointGp2 = el.child("pointsGp2").value.toString().toInt()
                                if (actualPointGp2 < minimumPointGp2) {
                                    minimumPointGp2 = actualPointGp2
                                }
                            }
                        }
                        if (i.gp2 == true) {
                            i.totalGp2Points = i.gp2Points
                            val realGp2Points = i.totalGp2Points?.minus(minimumPointGp2)
                            i.gp2Points = realGp2Points
                        }
                        i.totalPoints = i.points
                        val realPoints = i.totalPoints?.minus(minimumPoint)
                        i.points = realPoints
                    }
                    itemsTotal.add(i)
                }

                val sortedTotalItems = itemsTotal.sortedWith(compareByDescending<AllTeams> { it.points }.thenByDescending { it.totalPoints }.thenByDescending { it.one }.thenByDescending { it.two }.thenByDescending { it.three }.thenByDescending { it.four }
                    .thenByDescending { it.five }.thenByDescending { it.six }.thenByDescending { it.seven }.thenByDescending { it.eight }.thenByDescending { it.nine }.thenByDescending { it.ten }.thenByDescending { it.eleven }.thenByDescending { it.twelve }
                    .thenByDescending { it.thirteen }.thenByDescending { it.fourteen }.thenByDescending { it.fifteen }.thenByDescending { it.gp2Points })

                runOnUiThread {
                    adapter.update2(sortedTotalItems.toMutableList())
                }
            }
        }
    }

    override fun onTeamCreated(nameTeam: String, people: Int?, gp2: Boolean) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference(year.toString())

        if (gp2) {
            val newItem = AllTeams(nameTeam, people, 0, hasJokerRaced = false, 0, 0, 0, 0, gp2 = gp2, 0, 0, 0,
                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                0,0,0,0,0,0,0,0,0,0)
            dbRef.child("Teams").child(nameTeam).setValue(newItem)
            runOnUiThread {
                adapter.addItem(newItem)
            }
        }
        else {
            val newItem = AllTeams(nameTeam, people, 0, hasJokerRaced = false, 0, 0, null, null, gp2 = gp2, 0, 0, null,
                0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,null,null,null,null,null,
                null,null,null,null,null,null,null,null,null,null)
            dbRef.child("Teams").child(nameTeam).setValue(newItem)
            runOnUiThread {
                adapter.addItem(newItem)
            }
        }
    }

    override fun onTeamSelected(
        nameTeam: String?,
        people: Int?,
        joker: Int?,
        hasJokerRaced: Boolean?,
        gp2: Boolean?
    ) {
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@AllTeamActivity, AllDriverActivity::class.java)
        showDetailsIntent.putExtra(AllDriverActivity.EXTRA_NAMETEAM, nameTeam.toString())
        showDetailsIntent.putExtra(AllDriverActivity.EXTRA_GP2, gp2.toString())
        startActivity(showDetailsIntent)
    }

    /*override fun onItemClick(nameTeam: String?, teamNumber: String?, gp2: Boolean?) {
        /*val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@TeamActivity, DriverActivity::class.java)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_RACE_NAME, raceId)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_TEAM_NUMBER, teamNumber.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_NAMETEAM, nameTeam.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_GP2, gp2.toString())
        startActivity(showDetailsIntent)*/
    }*/

    override fun onItemLongClick(team: AllTeams?): Boolean {
        val builder = AlertDialog.Builder(this)

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference(year.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val initDone = p0.result.child("Teams").child(team?.nameTeam.toString()).child("racesTeam").value.toString().toInt()
                if (initDone > 0) {
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Mostmár nem törölheted a csapatot, mivel már részt vett egy versenyen!")
                    builder.setPositiveButton(R.string.button_ok, null)
                    builder.show()

                } else if (initDone == 0) {
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Biztos, hogy törölni szeretnéd ezt a csapatot?")

                    builder.setPositiveButton(R.string.button_ok) { dialog, which ->
                        adapter.deleteItem(team!!)

                        dbRef.get().addOnCompleteListener { p0 ->
                            if (p0.isSuccessful) {
                                val list = p0.result.child("Teams").children
                                for (element in list) {
                                    if (element.child("nameTeam").value.toString() == team.nameTeam) {
                                        dbRef.child("Teams").child(team.nameTeam).removeValue()
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

}