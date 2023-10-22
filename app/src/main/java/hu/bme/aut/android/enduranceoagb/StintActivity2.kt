package hu.bme.aut.android.enduranceoagb

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.databinding.ActivityStint2Binding
import hu.bme.aut.android.enduranceoagb.ui.stint.SectionsPagerAdapterStint

class StintActivity2 : FragmentActivity() {

    private lateinit var binding: ActivityStint2Binding

    private lateinit var dbRef: DatabaseReference

    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
    }

    private var raceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStint2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)

        val sectionsPagerAdapterStint = SectionsPagerAdapterStint(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapterStint
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                if (!p0.result.child("AllStint").child("numberOfStint").child("1")
                        .child("hasStintDone").value.toString().toBoolean()
                ) {
                    binding.technicalProblem.isVisible = false
                }
                if (p0.result.child("Info").child("hasRaceDone").value.toString().toBoolean()) {
                    binding.technicalProblem.isVisible = false
                }
            }
        }

        binding.technicalProblem.setOnClickListener {
            dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

            dbRef.get().addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    if (p0.result.child("AllStint").child("numberOfStint").child("1").child("hasStintDone").value.toString().toBoolean()) {
                        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                            this,
                            R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                        )

                        val inflater = this.layoutInflater
                        val dialogView: View =
                            inflater.inflate(
                                hu.bme.aut.android.enduranceoagb.R.layout.technical_problem_fragment,
                                null
                            )
                        dialogBuilder.setView(dialogView)
                        dialogBuilder.setTitle(hu.bme.aut.android.enduranceoagb.R.string.technicalProblem)


                        val items: MutableList<Stint>? = mutableListOf()

                        val allStint = p0.result.child("AllStint").child("numberOfStint").children
                        for (element in allStint) {
                            if (!element.child("hasStintDone").value.toString().toBoolean()) {
                                val numberOfStint = ((element.child("numberOfStint").value.toString().toInt()) - 1).toString()
                                val list = p0.result.child("Stints").child("Etap: $numberOfStint").child("Info").children
                                for (each in list) {
                                    if (each.child("teamName").value.toString() != "null") {
                                        val addStint = Stint(
                                            each.child("teamName").value.toString(),
                                            each.child("teamNumber").value.toString().toInt(),
                                            each.child("driverName").value.toString(),
                                            each.child("numberStint").value.toString().toInt(),
                                            each.child("shortTeamName").value.toString(),
                                            each.child("plusWeight").value.toString().toDouble(),
                                            each.child("info").value.toString(),
                                            each.child("previousInfo").value.toString(),
                                            each.child("hasStintDone").value.toString().toBoolean(),
                                            each.child("prevAvgWeight").value.toString().toDoubleOrNull(),
                                            each.child("driverWeight").value.toString().toDoubleOrNull(),
                                            each.child("kartNumber").value.toString().toInt(),
                                            each.child("expectedKartNumber").value.toString().toInt()
                                        )
                                        items?.add(addStint)
                                    }
                                }
                                break
                            }
                        }

                        val sortedItems = items?.sortedBy { it.teamNumber}

                        val teamKart =
                            dialogView.findViewById<Spinner>(hu.bme.aut.android.enduranceoagb.R.id.spTechnicalProblem)

                        val onlyItems: MutableList<String>? = mutableListOf()

                        if (sortedItems != null) {
                            for (e in sortedItems) {
                                val string = "Gokart: " + e.kartNumber + " - " + e.shortTeamName
                                onlyItems?.add(string)
                            }
                        }

                        teamKart.adapter = ArrayAdapter(
                            this,
                            R.layout.simple_spinner_dropdown_item,
                            onlyItems!!.toMutableList()
                        )

                        dialogBuilder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok) { _, _ ->
                            val builder: AlertDialog.Builder = AlertDialog.Builder(
                                this,
                                R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                            )

                            val inflater2 = this.layoutInflater
                            val dialogView2: View =
                                inflater2.inflate(
                                    hu.bme.aut.android.enduranceoagb.R.layout.parking_kart_fragment,
                                    null
                                )
                            builder.setView(dialogView2)
                            builder.setTitle(hu.bme.aut.android.enduranceoagb.R.string.technicalProblemDialog)

                            builder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok) { _, _ ->
                                val parkingKart =
                                    dialogView2.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart)


                                dbRef.get().addOnCompleteListener { p1 ->
                                    if (p1.isSuccessful) {
                                        val allStint2 = p1.result.child("AllStint").child("numberOfStint").children

                                        for (element in allStint2) {
                                            if (!element.child("hasStintDone").value.toString().toBoolean()) {
                                                if (parkingKart.text.isNotEmpty()) {
                                                    val newKart = parkingKart.text.toString()
                                                    var numberOfStint = (element.child("numberOfStint").value.toString().toInt() - 1).toString()
                                                    val id = p0.result.child("Id").value.toString().toInt()
                                                    if (numberOfStint.toInt() in 1..9) {
                                                        numberOfStint = "0$numberOfStint"
                                                        val getKart = p0.result.child("Cserék").child("Etap: $numberOfStint").child("3 - Gépszámok").child("${teamKart.selectedItemId+1}").value.toString()
                                                        val pushKart = "$getKart és $newKart"
                                                        dbRef.child("Cserék").child("Etap: $numberOfStint").child("3 - Gépszámok").child("${teamKart.selectedItemId+1}").setValue(pushKart)
                                                        for (el in 1..id) {
                                                            val stintExcel = p0.result.child("Excel").child(el.toString()).child("stintNumber").value.toString()
                                                            val teamNumberExcel = p0.result.child("Excel").child(el.toString()).child("teamNumber").value.toString()
                                                            val numberStint = element.child("numberOfStint").value.toString().toInt() - 1
                                                            val stintFull = "$numberStint. etap"
                                                            val teamReal = "${teamKart.selectedItemId+1}. csapat"
                                                            if (teamNumberExcel == teamReal && stintExcel == stintFull) {
                                                                dbRef.child("Excel").child(el.toString()).child("kartNumber").setValue(pushKart)
                                                            }
                                                        }
                                                    }
                                                    else {
                                                        val numberOfStintNormal = (element.child("numberOfStint").value.toString().toInt() - 1).toString()
                                                        val getKart = p0.result.child("Cserék").child("Etap: $numberOfStintNormal").child("3 - Gépszámok").child("${teamKart.selectedItemId+1}").value.toString()
                                                        val pushKart = "$getKart és $newKart"
                                                        dbRef.child("Cserék").child("Etap: $numberOfStintNormal").child("3 - Gépszámok").child("${teamKart.selectedItemId+1}").setValue(pushKart)
                                                        for (el in 1..id) {
                                                            val stintExcel = p0.result.child("Excel").child(el.toString()).child("stintNumber").value.toString()
                                                            val teamNumberExcel = p0.result.child("Excel").child(el.toString()).child("teamNumber").value.toString()
                                                            val numberStint = element.child("numberOfStint").value.toString().toInt() - 1
                                                            val stintFull = "$numberStint. etap"
                                                            val teamReal = "${teamKart.selectedItemId+1}. csapat"
                                                            if (teamNumberExcel == teamReal && stintExcel == stintFull) {
                                                                dbRef.child("Excel").child(el.toString()).child("kartNumber").setValue(pushKart)
                                                            }
                                                        }
                                                    }
                                                    val numberOfStintReal = (element.child("numberOfStint").value.toString().toInt() - 1).toString()
                                                    dbRef.child("Stints").child("Etap: $numberOfStintReal").child("Info").child("$numberOfStintReal-${teamKart.selectedItemId+1}").child("kartNumber").setValue(newKart)
                                                }
                                                break
                                            }
                                        }
                                    }

                                }



                            }

                            builder.setNegativeButton(
                                hu.bme.aut.android.enduranceoagb.R.string.button_megse,
                                null
                            )
                            val dialog = builder.create()
                            dialog.show()
                        }
                        dialogBuilder.setNegativeButton(
                            hu.bme.aut.android.enduranceoagb.R.string.button_megse,
                            null
                        )
                        val alertDialog = dialogBuilder.create()
                        alertDialog.show()
                    }
                }
            }
        }
    }

    fun getMyData(): String? {
        return raceId
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        val myIntent = Intent(this@StintActivity2, RaceActivity::class.java)
        myIntent.putExtra("extra.race_name", raceId)

        startActivity(myIntent)
    }
}