package hu.bme.aut.android.enduranceoagb

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.adapter.DetailsTeamCheckAdapter
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityDetailsteamcheckBinding
import java.sql.Driver
import kotlin.concurrent.thread
import kotlin.math.roundToInt

class DetailsTeamCheckActivity : AppCompatActivity(), DetailsTeamCheckAdapter.DetailsTeamCheckItemClickListener{
    private lateinit var binding: ActivityDetailsteamcheckBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: DetailsTeamCheckAdapter

    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
        const val EXTRA_NAMETEAM = "extra.nameteam"
        const val EXTRA_TEAM_NUMBER = "extra.team_number"
        const val EXTRA_GP2 = "extra.gp2"
    }

    private var raceId: String? = null
    private var teamName: String? = null
    private var teamId: String? = null
    private var gp2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsteamcheckBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)
        teamName = intent.getStringExtra(EXTRA_NAMETEAM)
        teamId = intent.getStringExtra(EXTRA_TEAM_NUMBER)
        gp2 = intent.getStringExtra(EXTRA_GP2)

        if (gp2 == "true") {
            binding.tvNameTeamDetailsTeamCheckActivity.text = "Csapatnév: $teamName (GP2)"
        }
        else {
            binding.tvNameTeamDetailsTeamCheckActivity.text = "Csapatnév: $teamName"
        }

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val totalWeight = p0.result.child("Teams").child(teamName.toString()).child("Info").child("avgWeight").value.toString().toDouble()
                val stintNumber = p0.result.child("Teams").child(teamName.toString()).child("Info").child("stintsDone").value.toString().toDouble()

                val avg = totalWeight / stintNumber

                if (avg < 90.0) {
                    binding.tvWeightDetailsTeamCheckActivity.setTextColor(Color.RED)
                    val animationZoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
                    binding.tvWeightDetailsTeamCheckActivity.startAnimation(animationZoomIn)

                }
                binding.tvWeightDetailsTeamCheckActivity.text = "Átlag súly: ${((avg * 100.0).roundToInt() / 100.0)} kg"
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
            val myIntent = Intent(this@DetailsTeamCheckActivity, MainActivity::class.java)

            startActivity(myIntent)
            return true
        }
        if (id == R.id.menu_etc) {
            val myIntent = Intent(this@DetailsTeamCheckActivity, RaceActivity::class.java)
            myIntent.putExtra("extra.race_name", raceId)

            startActivity(myIntent)
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    private fun initRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        binding.rvMainDetailsTeamCheck.setLayoutManager(layoutManager)

        adapter = DetailsTeamCheckAdapter(this)

        binding.rvMainDetailsTeamCheck.adapter = adapter

        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val items : MutableList<Drivers>? = mutableListOf()
        val stint : MutableList<Stint>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val numberOfStint = p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                for (i in 1..numberOfStint) {
                    val change = "Etap: $i"
                    val pushTeamStint = "$i-$teamId"
                    if (p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("driverName").exists()) {
                        val driver = p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("driverName").value.toString()
                        val weight = p0.result.child("Teams").child(teamName.toString()).child("Drivers").child(driver).child("weight").value.toString()
                        val addDriver = Drivers(driver, weight.toDouble())
                        items?.add(addDriver)

                        val hasStintDone = p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("hasStintDone").value.toString().toBoolean()
                        val info = p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("info").value.toString()
                        val previousInfo = p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("previousInfo").value.toString()
                        val plusWeight = p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("plusWeight").value.toString().toDouble()
                        val kartNumber = p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("kartNumber").value.toString().toInt()
                        val expectedKartNumber = p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("expectedKartNumber").value.toString().toInt()
                        val shortTeamName = p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("shortTeamName").value.toString()
                        val prevAvgWeight = p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("prevAvgWeight").value.toString().toDoubleOrNull()
                        val driverWeight = p0.result.child("Stints").child(change).child("Info").child(pushTeamStint).child("driverWeight").value.toString().toDoubleOrNull()

                        val addStint = Stint(teamName.toString(), teamId.toString().toInt(), driver, i, shortTeamName, plusWeight, info, previousInfo, hasStintDone, prevAvgWeight, driverWeight, kartNumber, expectedKartNumber)
                        stint?.add(addStint)
                    }

                }

                if (items?.size == null || stint?.size == null) {
                    val snack = Snackbar.make(binding.root,R.string.noTeam, Snackbar.LENGTH_LONG)
                    snack.show()
                }
                else {
                    runOnUiThread {
                        adapter.update2(items.toMutableList())
                        adapter.updateStint2(stint.toMutableList())
                    }
                }

            }
        }
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 10.00
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}