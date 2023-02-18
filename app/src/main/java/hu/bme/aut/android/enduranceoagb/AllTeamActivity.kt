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
                            element.child("gp2").value.toString().toBooleanStrictOrNull(),
                            element.child("racesTeam").value.toString().toInt()
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

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.child("Teams").children) {
                    val addTeam = AllTeams(
                        element.child("nameTeam").value.toString(),
                        element.child("people").value.toString().toIntOrNull(),
                        element.child("joker").value.toString().toIntOrNull(),
                        element.child("hasJokerRaced").value.toString().toBooleanStrictOrNull(),
                        element.child("gp2").value.toString().toBooleanStrictOrNull(),
                        element.child("racesTeam").value.toString().toInt()
                    )

                    items.add(addTeam)
                }

                //val sortedItems = items.sortedBy { it.teamNumber }
                runOnUiThread {
                    adapter.update2(items.toMutableList())
                }
            }
        }
    }

    override fun onTeamCreated(nameTeam: String, people: Int?, gp2: Boolean) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference(year.toString())

        val newItem = AllTeams(nameTeam, people, 0, hasJokerRaced = false, gp2 = gp2, 0)
        dbRef.child("Teams").child(nameTeam).setValue(newItem)
        runOnUiThread {
            adapter.addItem(newItem)
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