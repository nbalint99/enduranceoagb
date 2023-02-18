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
import hu.bme.aut.android.enduranceoagb.adapter.AllCheckAdapter
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityAllcheckBinding
import kotlin.concurrent.thread

class AllCheckActivity : AppCompatActivity(), AllCheckAdapter.AllCheckItemClickListener {
    private lateinit var binding: ActivityAllcheckBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: AllCheckAdapter

    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
    }

    private var raceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllcheckBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val trackName = p0.result.child("Info").child("location").value.toString()
                binding.tvTrackName.text = trackName
            }
        }

        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.menu_home) {
            val myIntent = Intent(this@AllCheckActivity, MainActivity::class.java)

            startActivity(myIntent)
            return true
        }
        if (id == R.id.menu_etc) {
            val myIntent = Intent(this@AllCheckActivity, RaceActivity::class.java)
            myIntent.putExtra("extra.race_name", raceId)

            startActivity(myIntent)
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    private fun initRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        binding.rvMainAllCheck.setLayoutManager(layoutManager)

        adapter = AllCheckAdapter(this)

        binding.rvMainAllCheck.adapter = adapter

        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val items: MutableList<Teams>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val list = p0.result.child("Teams").children
                for (element in list){
                    val teamNumber = element.child("Info").child("teamNumber").value.toString().toInt()
                    val avgWeight = element.child("Info").child("avgWeight").value.toString().toDouble()
                    val hasDriversDone = element.child("Info").child("hasDriversDone").value.toString().toInt()
                    val nameTeam = element.child("Info").child("nameTeam").value.toString()
                    val people = element.child("Info").child("people").value.toString().toInt()
                    val startKartNumber = element.child("Info").child("startKartNumber").value.toString().toIntOrNull()
                    val hasQualiDone = element.child("Info").child("hasDriversDone").value.toString().toBoolean()
                    val stintsDone = element.child("Info").child("stintsDone").value.toString().toIntOrNull()
                    val gp2 = element.child("Info").child("gp2").value.toString().toBooleanStrictOrNull()

                    val addTeam = Teams(nameTeam, people, teamNumber, avgWeight, hasDriversDone, startKartNumber, hasQualiDone, stintsDone, gp2)
                    items?.add(addTeam)
                }
                val sortedItems = items?.sortedBy { it.teamNumber }

                if (items?.size == 0 || items?.size == null) {
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@AllCheckActivity, RaceActivity::class.java)
        showDetailsIntent.putExtra(RaceActivity.EXTRA_RACE_NAME, raceId)
        startActivity(showDetailsIntent)
    }

    override fun onItemClick(nameTeam: String?, teamNumber: String?, gp2: Boolean?) {
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@AllCheckActivity, DetailsTeamCheckActivity::class.java)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_RACE_NAME, raceId)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_NAMETEAM, nameTeam.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_TEAM_NUMBER, teamNumber.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_GP2, gp2.toString())
        startActivity(showDetailsIntent)
    }

}