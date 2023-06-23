package hu.bme.aut.android.enduranceoagb

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.adapter.TeamCheckAdapter
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityTeamcheckBinding

class TeamCheckActivity : AppCompatActivity(), TeamCheckAdapter.TeamCheckItemClickListener {
    private lateinit var binding: ActivityTeamcheckBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: TeamCheckAdapter

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
        binding = ActivityTeamcheckBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)
        teamId = intent.getStringExtra(EXTRA_TEAM_NUMBER)
        teamName = intent.getStringExtra(EXTRA_NAMETEAM)

        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.menu_home) {
            val myIntent = Intent(this@TeamCheckActivity, MainActivity::class.java)

            startActivity(myIntent)
            return true
        }
        if (id == R.id.menu_etc) {
            val myIntent = Intent(this@TeamCheckActivity, RaceActivity::class.java)
            myIntent.putExtra("extra.race_name", raceId)

            startActivity(myIntent)
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    private fun initRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        binding.rvMainTeamCheck.layoutManager = layoutManager

        adapter = TeamCheckAdapter(this)

        binding.rvMainTeamCheck.adapter = adapter

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
                        element.child("Info").child("shortTeamName").value.toString()
                    )

                    items?.add(addTeam)
                }
                val sortedItems = items?.sortedBy { it.teamNumber }

                if ((items?.size == 0 || items?.size == null)) {
                    val snack = Snackbar.make(binding.root,R.string.noTeam, Snackbar.LENGTH_LONG)
                    snack.show()
                }
                else {
                    runOnUiThread {
                        adapter.update2(sortedItems!!.toMutableList())
                    }
                }
            }
        }
    }

    /*override fun onBackPressed() {
        super.onBackPressed()
        finish()

        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@TeamCheckActivity, RaceActivity::class.java)
        showDetailsIntent.putExtra(RaceActivity.EXTRA_RACE_NAME, raceId)
        startActivity(showDetailsIntent)
    }*/

    override fun onTeamSelected(position: String?, number: String?, gp2: Boolean?) {
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@TeamCheckActivity, DetailsTeamCheckActivity::class.java)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_RACE_NAME, raceId)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_NAMETEAM, position.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_TEAM_NUMBER, number.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_GP2, gp2.toString())
        startActivity(showDetailsIntent)
    }

    override fun onItemClick(position: String?, number: String?, gp2: Boolean?) {
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@TeamCheckActivity, DetailsTeamCheckActivity::class.java)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_RACE_NAME, raceId)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_NAMETEAM, position.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_TEAM_NUMBER, number.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_GP2, gp2.toString())
        startActivity(showDetailsIntent)
    }

}