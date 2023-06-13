package hu.bme.aut.android.enduranceoagb

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.databinding.ActivityBoxtimeBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityDetailsstintwatch2Binding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityDetailsstintwatchBinding
import hu.bme.aut.android.enduranceoagb.fragments.*


class DetailsStintWatchActivity : FragmentActivity(), NewStintFragment.NewStintListener, NewBoxFragment.NewBoxListener {
    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
        const val EXTRA_STINT_NUMBER = "extra.stint_number"
    }

    private var raceId: String? = null
    private var stintId: String? = null

    private lateinit var binding: ActivityDetailsstintwatchBinding

    private lateinit var binding2: ActivityDetailsstintwatch2Binding

    private lateinit var dbRef: DatabaseReference

    private val fragmentOri = DetailsStintActivity()
    private val fragmentOri2 = BoxTimeFragment()
    private val fragmentOri3 = LiveTimingFragment()
    private val fragmentOri1 = DetailsStintFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)
        stintId = intent.getStringExtra(EXTRA_STINT_NUMBER)

        supportFragmentManager.beginTransaction().replace(hu.bme.aut.android.enduranceoagb.R.id.stintData, fragmentOri1, "1").commit()

        if (stintId.toString().toInt() == 1) {
            binding = ActivityDetailsstintwatchBinding.inflate(layoutInflater)
            setContentView(binding.root)
            binding.tvNumberOfStint.text = "$stintId. etap"


            binding.changeKart.setOnClickListener {
                dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

                dbRef.get().addOnCompleteListener { p0 ->
                    if (p0.isSuccessful) {
                        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                            this,
                            R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                        )

                        val inflater = this.layoutInflater
                        val dialogView: View =
                            inflater.inflate(
                                hu.bme.aut.android.enduranceoagb.R.layout.parking_kart_fragment,
                                null
                            )
                        dialogBuilder.setView(dialogView)
                        dialogBuilder.setTitle(hu.bme.aut.android.enduranceoagb.R.string.parkingKart)

                        val parkingKart =
                            dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart)

                        val kartNumberOri = p0.result.child("Stints").child("Etap: 1").child("Info").child("1-box")
                            .child("kartNumber").value.toString().toIntOrNull()
                        if (kartNumberOri != null || kartNumberOri.toString() != "null") {
                            parkingKart.setText(kartNumberOri.toString())
                        }

                        dialogBuilder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok) { _, _ ->
                            if (parkingKart.text.toString()
                                    .isNotEmpty()
                            ) {
                                dbRef.child("Stints").child("Etap: 1").child("Info").child("1-box")
                                    .child("kartNumber").setValue(parkingKart.text.toString())
                                dbRef.child("Cserék").child("Etap: 01").child("5 - Parkol").child("Parkol")
                                    .setValue(parkingKart.text.toString())
                                val id = p0.result.child("Id").value.toString()
                                var idNumber : Int
                                if (id == "-1") {
                                    idNumber = 0
                                }
                                else {
                                    idNumber = id.toInt()
                                    idNumber++
                                }
                                dbRef.child("Id").setValue(idNumber)
                                val teamStintId = idNumber
                                dbRef.child("Excel").child(teamStintId.toString()).child("stintNumber").setValue("1. etap")
                                dbRef.child("Excel").child(teamStintId.toString()).child("teamNumber").setValue("box")
                                dbRef.child("Excel").child(teamStintId.toString()).child("driver").setValue("-")
                                dbRef.child("Excel").child(teamStintId.toString()).child("plusWeight").setValue("-")
                                dbRef.child("Excel").child(teamStintId.toString()).child("totalWeight").setValue("-")
                                dbRef.child("Excel").child(teamStintId.toString()).child("kartNumber").setValue(parkingKart.text.toString())

                            } else {
                                AlertDialog.Builder(this)
                                    .setTitle(hu.bme.aut.android.enduranceoagb.R.string.warning)
                                    .setMessage(hu.bme.aut.android.enduranceoagb.R.string.validAll)
                                    .setPositiveButton(
                                        hu.bme.aut.android.enduranceoagb.R.string.button_ok,
                                        null
                                    )
                                    .setNegativeButton("", null)
                                    .show()
                            }
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

            binding.startingList.setOnClickListener {
                dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

                val items : ArrayList<String>? = arrayListOf()

                dbRef.get().addOnCompleteListener { p0 ->
                    if (p0.isSuccessful) {
                        val doneStint = p0.result.child("AllStint").child("numberOfStint").child("1").child("hasStintDone").value.toString().toBoolean()
                        if (doneStint) {
                            val numberOfTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                            val teams = p0.result.child("Teams").children
                            for (element in teams) {
                                val teamName = element.child("Info").child("nameTeam").value.toString()
                                val teamNumber = element.child("Info").child("teamNumber").value.toString().toInt()
                                val childTeam = "1-$teamNumber"
                                val kartNumber = p0.result.child("Stints").child("Etap: 1").child("Info").child(childTeam)
                                    .child("kartNumber").value.toString().toIntOrNull()
                                var init = 1
                                for (i in numberOfTeams downTo 1) {
                                    if (init == teamNumber) {
                                        if (i < 10) {
                                            var place = i.toString()
                                            place = "0$place"
                                            val string =
                                                "$place. hely - Gokart: $kartNumber - Csapat: $teamName"
                                            items?.add(string)
                                            break
                                        }
                                        else {
                                            val string =
                                                "$i. hely - Gokart: $kartNumber - Csapat: $teamName"
                                            items?.add(string)
                                            break
                                        }
                                    }
                                    init += 1
                                }
                            }
                            val sortedItems = items?.sorted()
                            val pushItems: MutableList<String>? = mutableListOf()
                            if (sortedItems != null) {
                                for (each in sortedItems.toMutableList()) {
                                    if (each.startsWith("0")) {
                                        val item = each.drop(1)
                                        pushItems?.add(item)
                                    }
                                    else {
                                        pushItems?.add(each)
                                    }
                                }
                            }

                            runOnUiThread {
                                val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                                    this,
                                    android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                                )
                                dialogBuilder.setTitle(hu.bme.aut.android.enduranceoagb.R.string.startingList)

                                dialogBuilder.setItems(pushItems?.toTypedArray(), null)

                                dialogBuilder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok, null)
                                val alertDialog = dialogBuilder.create()
                                alertDialog.show()
                            }
                        }
                        else {
                            val snack = Snackbar.make(binding.root,
                                hu.bme.aut.android.enduranceoagb.R.string.doneStint, Snackbar.LENGTH_LONG)
                            snack.show()
                        }

                    }

                }
            }
        }
        else {
            supportFragmentManager.beginTransaction().replace(hu.bme.aut.android.enduranceoagb.R.id.boxTimeData, fragmentOri3, "2").commit()

            binding2 = ActivityDetailsstintwatch2Binding.inflate(layoutInflater)
            setContentView(binding2.root)

            binding2.tvNumberOfStint.text = "$stintId. etap"
        }


    }



    fun getMyData(): String? {
        return raceId
    }

    fun getMyDataStint(): String? {
        return stintId
    }

    override fun onStintCreated(
        teamName: String,
        teamNumber: Int,
        driver: String,
        stintNumber: Int,
        weight: Double,
        info: String?,
        kartNumber: Int,
        expectedKartNumber: Int,
        driverName: String?,
        plusWeightDriver: String?,
        stintDonePrev: String,
        stintIdpass: String?,
        raceIdpass: String?
    ) {
        val fragment = DetailsStintActivity()
        fragment.onStintCreated(teamName, teamNumber, driver, stintNumber, weight, info, kartNumber, expectedKartNumber, driverName, plusWeightDriver, stintDonePrev, stintId, raceId)
        val mBundle = Bundle()
        mBundle.putString("mText",teamNumber.toString())
        fragment.arguments = mBundle
        supportFragmentManager.beginTransaction().replace(hu.bme.aut.android.enduranceoagb.R.id.stintData, fragment, "1").commit()
    }

    override fun onStintNotCreated() {
        val fragment = DetailsStintActivity()
        fragment.onStintNotCreated()
    }

    override fun onBoxCreated(raceIdBox: String, teamName: String, teamNumber: Int, time: Double, stint: Int, activity: String) {
        val fragment2 = BoxTimeFragment()
        fragment2.onBoxCreated(raceIdBox, teamName, teamNumber, time, stintId.toString().toInt(), activity = this.toString())
        val mBundle2 = Bundle()
        mBundle2.putString("mText",teamNumber.toString())
        fragment2.arguments = mBundle2
        supportFragmentManager.beginTransaction().replace(hu.bme.aut.android.enduranceoagb.R.id.boxTimeData, fragment2, "2").commit()
    }

    override fun onBoxNotCreated() {
        val snack = Snackbar.make(ActivityBoxtimeBinding.inflate(layoutInflater).root, hu.bme.aut.android.enduranceoagb.R.string.notAddDriver, Snackbar.LENGTH_LONG)
        snack.show()
    }

    override fun raceId(): String? {
        return raceId
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        val myIntent = Intent(this@DetailsStintWatchActivity, StintActivity2::class.java)
        myIntent.putExtra("extra.race_name", raceId)

        startActivity(myIntent)
    }


}
