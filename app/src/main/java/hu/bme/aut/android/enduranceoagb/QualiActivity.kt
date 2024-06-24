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
import hu.bme.aut.android.enduranceoagb.data.Quali
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityQualiBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityResultBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityTeamcheckBinding
import hu.bme.aut.android.enduranceoagb.fragments.NewStintFragment
import hu.bme.aut.android.enduranceoagb.fragments.QualificationFragment
import hu.bme.aut.android.enduranceoagb.fragments.ResultFragment
import java.util.*

class QualiActivity : AppCompatActivity(), ResultAdapter.ResultItemClickListener, QualificationFragment.QualificationFragmentListener {
    private lateinit var binding: ActivityQualiBinding

    private lateinit var dbRef: DatabaseReference

    private lateinit var dbRef2: DatabaseReference

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
        binding = ActivityQualiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)
        teamId = intent.getStringExtra(EXTRA_TEAM_NUMBER)
        teamName = intent.getStringExtra(EXTRA_NAMETEAM)

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val hasQualiDone = p0.result.child("Info").child("hasQualiDone").value.toString().toIntOrNull()
                if (hasQualiDone != null) {
                    if (hasQualiDone > 0) {
                        binding.btnQualiEnd.visibility = View.INVISIBLE
                    }
                    else {
                        binding.btnQualiEnd.visibility = View.VISIBLE
                    }
                }
                else {
                    binding.btnQualiEnd.visibility = View.VISIBLE
                }
            }


        }

        binding.btnQualiEnd.setOnClickListener {
            qualiResult()
            binding.btnQualiEnd.visibility = View.INVISIBLE
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
            val myIntent = Intent(this@QualiActivity, MainActivity::class.java)

            startActivity(myIntent)
            return true
        }
        if (id == R.id.menu_etc) {
            val myIntent = Intent(this@QualiActivity, TeamActivity::class.java)
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
                for (element in p0.result.child("Quali").children) {
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

                if (done == 0) {
                    binding.btnQualiEnd.visibility = View.VISIBLE
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
        showDetailsIntent.setClass(this@QualiActivity, TeamActivity::class.java)
        showDetailsIntent.putExtra(RaceActivity.EXTRA_RACE_NAME, raceId)
        startActivity(showDetailsIntent)
    }

    override fun onTeamSelected(position: String?, number: String?, gp2: Boolean?) {
        //
    }

    override fun onItemClick(position: String?, number: String?, gp2: Boolean?, itemId: Int?) {
        dbRef =
            FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val fragment = QualificationFragment.newInstance(
                    itemId.toString()
                )
                fragment.show(supportFragmentManager, "QualificationFragment")
            }
        }
    }



    override fun onQualificationCreated(result: Int, team: String) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                dbRef.child("Quali").child(result.toString()).child("team")
                    .setValue(team)
                val teams = p0.result.child("Teams").children
                for (i in teams) {
                    val teamName = i.child("Info").child("shortTeamName").value.toString()
                    if (team == teamName) {
                        val longTeamName = i.child("Info").child("nameTeam").value.toString()
                        val gp2 = i.child("Info").child("gp2").value.toString().toBoolean()
                        val group = i.child("Info").child("group").value.toString().toInt()
                        dbRef.child("Quali").child(result.toString()).child("gp2").setValue(gp2)
                        dbRef.child("Quali").child(result.toString()).child("longTeamName").setValue(longTeamName)
                        dbRef.child("Quali").child(result.toString()).child("result").setValue(result)
                        dbRef.child("Quali").child(result.toString()).child("group").setValue(group)
                        break
                    }
                }
            }
        }
        loadItemsInBackground()
    }


    override fun raceId(): String? {
        return raceId
    }

    private fun qualiResult() {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val items : MutableList<Quali>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                //println("loadResult")
                val quali = p0.result.child("Quali").children
                for (element in quali) {
                    val addTeam = Quali(
                        element.child("team").value.toString(),
                        element.child("longTeamName").value.toString(),
                        element.child("gp2").value.toString().toBooleanStrict(),
                        element.child("result").value.toString().toInt(),
                        element.child("group").value.toString().toIntOrNull()
                    )

                    //println(addTeam)

                    items?.add(addTeam)
                }
                val sortedItems = items?.sortedBy { it.result }

                val secondGroupFirst = p0.result.child("Info").child("secondGroup").value.toString().toIntOrNull()
                val numberOfTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toInt()

                var group2 = 0
                if (secondGroupFirst != null) {
                    group2 = numberOfTeams-(secondGroupFirst-1)
                }

                var group1 = numberOfTeams
                //var group2 = numberOfTeams-(secondGroupFirst-1)

                //println(group1)
                //println(group2)

                if (sortedItems != null) {
                    //println("sorted not null")
                    for (el in sortedItems) {
                        val teams = p0.result.child("Teams").children
                        for (i in teams) {
                        //println(el)
                            //println(i)
                            val teamName = i.child("Info").child("shortTeamName").value.toString()
                            if (teamName == el.team) {
                                //println(teamName)
                                if (el.group == 1) {
                                    //println("group1")
                                    //println(group1)
                                    dbRef.child("Teams").child(el.longTeamName).child("Info").child("teamNumber").setValue(group1)
                                    dbRef.child("Teams").child(el.longTeamName).child("Info").child("hasQualiResultDone").setValue(true)
                                    dbRef.child("Info").child("hasQualiDone").setValue(ServerValue.increment(1))
                                    group1--
                                    break
                                }
                                else if (el.group == 2) {
                                    //println("group2")
                                    //println(group2)
                                    dbRef.child("Teams").child(el.longTeamName).child("Info").child("teamNumber").setValue(group2)
                                    dbRef.child("Teams").child(el.longTeamName).child("Info").child("hasQualiResultDone").setValue(true)
                                    dbRef.child("Info").child("hasQualiDone").setValue(ServerValue.increment(1))
                                    group2--
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}