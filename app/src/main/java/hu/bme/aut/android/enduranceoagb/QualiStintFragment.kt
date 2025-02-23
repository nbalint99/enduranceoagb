package hu.bme.aut.android.enduranceoagb

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.DetailsStintWatchActivity
import hu.bme.aut.android.enduranceoagb.DetailsTeamCheckActivity
import hu.bme.aut.android.enduranceoagb.DriverActivity
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.TeamActivity
import hu.bme.aut.android.enduranceoagb.adapter.DetailsStintAdapter
import hu.bme.aut.android.enduranceoagb.adapter.DetailsStintFragmentAdapter
import hu.bme.aut.android.enduranceoagb.adapter.QualiStintAdapter
import hu.bme.aut.android.enduranceoagb.adapter.ResultAdapter
import hu.bme.aut.android.enduranceoagb.data.BoxTime
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityBoxtimeBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityDetailsstintBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityDetailsstintfragmentBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityQualiBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityQualistintBinding
import hu.bme.aut.android.enduranceoagb.databinding.DetailsstintfragmentListBinding
import hu.bme.aut.android.enduranceoagb.fragments.NewQualiStintFragment
import hu.bme.aut.android.enduranceoagb.fragments.NewStintFragment


class QualiStintFragment : AppCompatActivity(), QualiStintAdapter.QualiStintItemClickListener,
    NewQualiStintFragment.NewQualiStintListener {
    private lateinit var binding: ActivityQualistintBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: QualiStintAdapter

    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
    }

    private var raceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQualistintBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)

        initRecyclerView()
    }

    private fun initRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 2)

        binding.rvDetailsQuali.layoutManager = layoutManager

        adapter = QualiStintAdapter(this)

        binding.rvDetailsQuali.adapter = adapter

        binding.rvDetailsQuali.setItemViewCacheSize(16)

        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val itemsTeams : MutableList<Teams>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.child("Teams").children) {
                    val addTeam = Teams(
                        element.child("Info").child("nameTeam").value.toString(),
                        element.child("Info").child("people").value.toString().toInt(),
                        element.child("Info").child("teamNumber").value.toString().toIntOrNull(),
                        element.child("Info").child("avgWeight").value.toString()
                            .toDoubleOrNull(),
                        element.child("Info").child("hasDriversDone").value.toString()
                            .toInt(),
                        element.child("Info").child("startKartNumber").value.toString().toInt(),
                        element.child("Info").child("hasQualiDone").value.toString().toBoolean(),
                        element.child("Info").child("stintsDone").value.toString().toIntOrNull(),
                        element.child("Info").child("gp2").value.toString().toBooleanStrictOrNull(),
                        element.child("Info").child("points").value.toString().toIntOrNull(),
                        element.child("Info").child("shortTeamName").value.toString(),
                        element.child("Info").child("group").value.toString().toIntOrNull(),
                        element.child("Info").child("hasQualiResultDone").value.toString().toBooleanStrictOrNull(),
                        element.child("Info").child("qualiName1").value.toString(),
                        element.child("Info").child("qualiWeight1").value.toString().toDoubleOrNull(),
                        element.child("Info").child("qualiName2").value.toString(),
                        element.child("Info").child("qualiWeight2").value.toString().toDoubleOrNull(),
                        element.child("Info").child("qualiTotalWeight").value.toString().toDoubleOrNull()
                    )
                    itemsTeams?.add(addTeam)
                }

                runOnUiThread {
                    adapter.teams(itemsTeams!!.toMutableList())
                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.refresh, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@QualiStintFragment, TeamActivity::class.java)
        showDetailsIntent.putExtra(TeamActivity.EXTRA_RACE_NAME, raceId)
        startActivity(showDetailsIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.menu_home) {
            restartActivity()
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onNewStintListener(stint: Int, teamName: String, shortTeamName: String?) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val fragment = NewQualiStintFragment.newInstance(
                    stint.toString(),
                    teamName,
                    shortTeamName
                )
                fragment.show(this.supportFragmentManager, "NewQualiStintFragment")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStintCreated(
        teamName: String,
        driver: String,
        stintNumber: Int,
        shortTeamName: String?,
        weight: Double,
        info: String?,
        kartNumber: Int,
        raceIdpass: String?
    ) {

        val raceId = raceIdpass.toString()
        val stintId = stintNumber

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val driverWeightReal = p0.result.child("Teams").child(teamName).child("Drivers").child(driver).child("weight").value.toString().toDoubleOrNull()
                var totalWeight = driverWeightReal?.plus(weight)
                if (stintId == 1) {
                    dbRef.child("Teams").child(teamName).child("Info").child("qualiName1").setValue(driver)
                    dbRef.child("Teams").child(teamName).child("Info").child("qualiWeight1").setValue(weight)
                    dbRef.child("Teams").child(teamName).child("Info").child("qualiTotalWeight").setValue(totalWeight)
                }
                else {
                    val qualiTotalWeight = p0.result.child("Teams").child(teamName).child("Info").child("qualiTotalWeight").value.toString().toDoubleOrNull()
                    val totalTotalWeight = totalWeight?.plus(qualiTotalWeight!!)
                    dbRef.child("Teams").child(teamName).child("Info").child("qualiName2").setValue(driver)
                    dbRef.child("Teams").child(teamName).child("Info").child("qualiWeight2").setValue(weight)
                    dbRef.child("Teams").child(teamName).child("Info").child("qualiTotalWeight").setValue(totalTotalWeight)
                }
            }
        }
        restartActivity()
    }

    private fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

    override fun onStintNotCreated() {
        val snack = Snackbar.make(binding.root, hu.bme.aut.android.enduranceoagb.R.string.notAddDriver, Snackbar.LENGTH_LONG)
        snack.show()
    }

    override fun raceId(): String? {
        return raceId
    }
}