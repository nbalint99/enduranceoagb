package hu.bme.aut.android.enduranceoagb

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.data.DoneStint
import hu.bme.aut.android.enduranceoagb.databinding.ActivityRaceBinding
import kotlin.concurrent.thread


class RaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRaceBinding

    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
    }

    private lateinit var dbRef: DatabaseReference

    private var raceId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRaceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        raceId = intent.getStringExtra(TeamActivity.EXTRA_RACE_NAME)

        binding.btnTeam.setOnClickListener {
            dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

            dbRef.get().addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val hasTeamsCreated =
                        p0.result.child("Info").child("hasTeamsCreated").value.toString()
                            .toBooleanStrictOrNull()
                    val hasFinalTeamsCreated =
                        p0.result.child("Info").child("hasFinalTeamsCreated").value.toString()
                            .toBooleanStrictOrNull()

                    if (hasTeamsCreated == true && hasFinalTeamsCreated == false) {
                        val showDetailsIntent = Intent()
                        showDetailsIntent.setClass(this@RaceActivity, TeamActivity::class.java)
                        showDetailsIntent.putExtra(TeamActivity.EXTRA_RACE_NAME, raceId)
                        showDetailsIntent.putExtra(TeamActivity.EXTRA_FINAL_CREATED, "false")
                        startActivity(showDetailsIntent)
                    }
                    else {
                        val showDetailsIntent = Intent()
                        showDetailsIntent.setClass(this@RaceActivity, TeamActivity::class.java)
                        showDetailsIntent.putExtra(TeamActivity.EXTRA_RACE_NAME, raceId)
                        showDetailsIntent.putExtra(TeamActivity.EXTRA_FINAL_CREATED, "true")
                        startActivity(showDetailsIntent)
                    }
                }
            }
        }


        binding.btnStint.setOnClickListener {
            val showDetailsIntent = Intent()
            dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

            dbRef.get().addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val numberOfTeams =
                        p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                    val teamsDone =
                        p0.result.child("Info").child("hasTeamsDone").value.toString().toInt()
                    val qualiDone =
                        p0.result.child("Info").child("hasQualiDone").value.toString().toInt()
                    if (numberOfTeams == teamsDone && numberOfTeams == qualiDone) {
                        val stintReady = p0.result.child("Info").child("hasStintReady").value.toString().toBoolean()
                        if (!stintReady){
                            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                            builder.setTitle("Figyelem!")
                            builder.setMessage("Biztos, hogy létre akarod hozni az etapokat? Innentől kezdve már nem módosíthatod a csapatokat és a versenyzőket!")

                            builder.setPositiveButton(R.string.button_ok) { _, _ ->
                                val builder2: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(
                                    this,
                                    android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                                )

                                val inflater2 = this.layoutInflater
                                val dialogView2: View =
                                    inflater2.inflate(
                                        hu.bme.aut.android.enduranceoagb.R.layout.change_time_dialog,
                                        null
                                    )
                                builder2.setView(dialogView2)
                                builder2.setTitle("Csereidő beállítása")

                                builder2.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok) { _, _ ->
                                    val changeTimeMin =
                                        dialogView2.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etChangeTimeMin)
                                    val changeTimeSec =
                                        dialogView2.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etChangeTimeSec)

                                    if (changeTimeMin.text.toString() != "" && changeTimeSec.text.toString() != ""
                                    ) {
                                        if (changeTimeSec.text.toString()
                                                .toInt() < 60
                                        ) {

                                                val totalChange = (changeTimeMin.text.toString()
                                                    .toInt()) * 60000 + (changeTimeSec.text.toString()
                                                    .toInt()) * 1000

                                                dbRef.child("Info").child("changeTime")
                                                    .setValue(totalChange)

                                                val stintNumber =
                                                    p0.result.child("Info")
                                                        .child("allStintNumber").value.toString()
                                                        .toInt()
                                                if (!p0.result.hasChild("AllStint")) {
                                                    val allTeamTogether =
                                                        p0.result.child("Info")
                                                            .child("allTeamTogether").value.toString()
                                                            .toBooleanStrictOrNull()
                                                    for (element in 1..stintNumber) {

                                                        val newItem = DoneStint(
                                                            element,
                                                            false,
                                                            false,
                                                            zeroToUp(numberOfTeams, element, allTeamTogether),
                                                            upToZero(numberOfTeams, element, allTeamTogether)
                                                        )
                                                        dbRef.child("AllStint")
                                                            .child("numberOfStint")
                                                            .child(element.toString())
                                                            .setValue(newItem)
                                                    }
                                                }
                                                for (ele in 1..numberOfTeams) {
                                                    if (!p0.result.child("Result")
                                                            .child(ele.toString())
                                                            .exists()
                                                    ) {
                                                        dbRef.child("Result").child(ele.toString())
                                                            .child("team")
                                                            .setValue("Nincs még eredmény!")
                                                    }
                                                }
                                                dbRef.child("Info").child("hasStintReady")
                                                    .setValue(true)

                                            showDetailsIntent.setClass(
                                                    this@RaceActivity,
                                                    StintActivity2::class.java
                                                )
                                                showDetailsIntent.putExtra(
                                                    StintActivity2.EXTRA_RACE_NAME,
                                                    raceId
                                                )
                                                startActivity(showDetailsIntent)

                                            val teams = p0.result.child("Teams").children
                                            val id = p0.result.child("Id").value.toString()
                                            var idNumber = 0
                                            for (i in teams) {
                                                val teamNumber = i.child("Info").child("teamNumber").value.toString()
                                                val shortTeamName = i.child("Info").child("shortTeamName").value.toString()
                                                when (id) {
                                                    "-1" -> {
                                                        idNumber = 0
                                                    }
                                                    else -> {
                                                        idNumber++
                                                    }
                                                }
                                                dbRef.child("Id").setValue(idNumber)
                                                val teamStintId = idNumber
                                                dbRef.child("Excel").child(teamStintId.toString()).child("stintNumber").setValue("Csapat")
                                                dbRef.child("Excel").child(teamStintId.toString()).child("teamNumber").setValue(
                                                    "$teamNumber. csapat"
                                                )
                                                dbRef.child("Excel").child(teamStintId.toString()).child("driver").setValue(shortTeamName)
                                                dbRef.child("Excel").child(teamStintId.toString()).child("plusWeight").setValue("-")
                                                dbRef.child("Excel").child(teamStintId.toString()).child("totalWeight").setValue("-")
                                                dbRef.child("Excel").child(teamStintId.toString()).child("kartNumber").setValue("-")

                                                idNumber++
                                                dbRef.child("Id").setValue(idNumber)
                                                val teamStintIdPlus = idNumber
                                                val startKartNumber = i.child("Info").child("startKartNumber").value.toString().toInt()
                                                dbRef.child("Excel").child(teamStintIdPlus.toString()).child("stintNumber").setValue("Etap 1")
                                                dbRef.child("Excel").child(teamStintIdPlus.toString()).child("teamNumber").setValue(
                                                    "$teamNumber. csapat"
                                                )
                                                dbRef.child("Excel").child(teamStintIdPlus.toString()).child("driver").setValue("-")
                                                dbRef.child("Excel").child(teamStintIdPlus.toString()).child("plusWeight").setValue("-")
                                                dbRef.child("Excel").child(teamStintIdPlus.toString()).child("totalWeight").setValue("-")
                                                dbRef.child("Excel").child(teamStintIdPlus.toString()).child("kartNumber").setValue(startKartNumber)

                                            }
                                            val teamsOther = p0.result.child("Teams").children
                                            for (e in teamsOther) {
                                                //val teamNumber = e.child("Info").child("teamNumber").value.toString()
                                                val shortTeamName = e.child("Info").child("shortTeamName").value.toString()
                                                val drivers = e.child("Drivers").children
                                                var driverId = 1
                                                for (i in drivers) {
                                                    val driverName = i.child("nameDriver").value.toString()
                                                    val weight = i.child("weight").value.toString()
                                                    val weightComma = weight.replace('.', ',')
                                                    when (id) {
                                                        "-1" -> {
                                                            idNumber = 0
                                                        }
                                                        else -> {
                                                            idNumber++
                                                        }
                                                    }
                                                    dbRef.child("Id").setValue(idNumber)
                                                    val teamStintId = idNumber
                                                    dbRef.child("Excel").child(teamStintId.toString()).child("stintNumber").setValue(
                                                        "$driverId. versenyző"
                                                    )
                                                    dbRef.child("Excel").child(teamStintId.toString()).child("teamNumber").setValue(
                                                        shortTeamName
                                                    )
                                                    dbRef.child("Excel").child(teamStintId.toString()).child("driver").setValue("-")
                                                    dbRef.child("Excel").child(teamStintId.toString()).child("plusWeight").setValue(weightComma)
                                                    dbRef.child("Excel").child(teamStintId.toString()).child("totalWeight").setValue(driverName)
                                                    dbRef.child("Excel").child(teamStintId.toString()).child("kartNumber").setValue("-")
                                                    driverId++

                                                }
                                            }
                                        }
                                        else {
                                            val snack = Snackbar.make(it, R.string.notValid, Snackbar.LENGTH_LONG)
                                            snack.show()
                                        }
                                    }
                                    else {
                                        val snack = Snackbar.make(it, R.string.notValid, Snackbar.LENGTH_LONG)
                                        snack.show()
                                    }
                                }
                                builder2.setNeutralButton(R.string.button_megse, null)
                                builder2.show()





                                /*val stintNumber =
                                    p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                                if (!p0.result.hasChild("AllStint")) {
                                    for (element in 1..stintNumber) {

                                        val newItem = DoneStint(element, false, false, zeroToUp(numberOfTeams, element), upToZero(numberOfTeams, element))
                                        dbRef.child("AllStint").child("numberOfStint")
                                            .child(element.toString())
                                            .setValue(newItem)
                                    }
                                }
                                for (ele in 1..stintNumber-1) {
                                    if (!p0.result.child("Result").child(ele.toString()).exists()) {
                                        dbRef.child("Result").child(ele.toString()).child("team")
                                            .setValue("Nincs még eredmény!")
                                    }
                                }
                                dbRef.child("Info").child("hasStintReady").setValue(true)

                                showDetailsIntent.setClass(this@RaceActivity, StintActivity2::class.java)
                                showDetailsIntent.putExtra(StintActivity2.EXTRA_RACE_NAME, raceId)
                                startActivity(showDetailsIntent)*/
                            }
                            builder.setNeutralButton(R.string.button_megse, null)
                            builder.show()
                        }
                        else if (stintReady) {
                            showDetailsIntent.setClass(this@RaceActivity, StintActivity2::class.java)
                            showDetailsIntent.putExtra(StintActivity2.EXTRA_RACE_NAME, raceId)
                            startActivity(showDetailsIntent)
                        }
                    }
                    else {
                        val snack = Snackbar.make(it, R.string.notTeamsDriversDone, Snackbar.LENGTH_LONG)
                        snack.show()
                    }
                }
            }

        }

        binding.btnEtc.setOnClickListener {

            val listItems = arrayOf("Ellenőrzés csapatonként", "Összesített ellenőrzés - jelenlegi állás szerint", "Összesített ellenőrzés - futam vége után", "Végeredmény")
            val mBuilder = AlertDialog.Builder(this@RaceActivity)
            mBuilder.setTitle("Válaszd ki, hogy melyik nézetet szeretnéd látni!")
            mBuilder.setSingleChoiceItems(listItems, -1) { _, _ ->}
            mBuilder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok){ dialog, _ ->
                val position = (dialog as AlertDialog).listView.checkedItemPosition
                dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

                dbRef.get().addOnCompleteListener { p0 ->
                    if (p0.isSuccessful) {
                        val firstStintDone = p0.result.child("AllStint").child("numberOfStint").child("1")
                            .child("hasStintDone").value.toString().toBoolean()
                        if (firstStintDone) {
                            if (position == 0) {
                                val showDetailsIntent = Intent()
                                showDetailsIntent.setClass(this@RaceActivity, TeamCheckActivity::class.java)
                                showDetailsIntent.putExtra(StintActivity2.EXTRA_RACE_NAME, raceId)
                                startActivity(showDetailsIntent)
                            }
                            else if (position == 1) {
                                val showDetailsIntent = Intent()
                                showDetailsIntent.setClass(this@RaceActivity, AllCheckActivity::class.java)
                                showDetailsIntent.putExtra(StintActivity2.EXTRA_RACE_NAME, raceId)
                                startActivity(showDetailsIntent)
                            }

                            else if (position == 2) {
                                dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

                                dbRef.get().addOnCompleteListener { p1 ->
                                    if (p1.isSuccessful) {
                                        val raceDone = p1.result.child("Info").child("hasRaceDone").value.toString().toBoolean()
                                        if (raceDone) {
                                            val showDetailsIntent = Intent()
                                            showDetailsIntent.setClass(this@RaceActivity, AllFinalCheckActivity::class.java)
                                            showDetailsIntent.putExtra(StintActivity2.EXTRA_RACE_NAME, raceId)
                                            startActivity(showDetailsIntent)
                                        }
                                        else {
                                            val snack = Snackbar.make(it,"Jelenleg ez nem választható ki, amíg vége nincs a versenynek!", Snackbar.LENGTH_LONG)
                                            snack.show()
                                        }
                                    }
                                }
                            }
                            else if (position == 3) {
                                dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

                                dbRef.get().addOnCompleteListener { p1 ->
                                    if (p1.isSuccessful) {
                                        val raceDone = p1.result.child("Info").child("hasRaceDone").value.toString().toBoolean()
                                        if (raceDone) {
                                            val showDetailsIntent = Intent()
                                            showDetailsIntent.setClass(this@RaceActivity, ResultActivity::class.java)
                                            showDetailsIntent.putExtra(StintActivity2.EXTRA_RACE_NAME, raceId)
                                            startActivity(showDetailsIntent)
                                        }
                                        else {
                                            val snack = Snackbar.make(it,"Jelenleg ez nem választható ki, amíg vége nincs a versenynek!", Snackbar.LENGTH_LONG)
                                            snack.show()
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            val snack = Snackbar.make(it,"Jelenleg ez nem választható ki, amíg el nem kezdődött a verseny!", Snackbar.LENGTH_LONG)
                            snack.show()
                        }

                    }
                }
            }

            mBuilder.setNeutralButton(R.string.button_megse) { dialog, _ ->
                dialog.cancel()
            }

            val mDialog = mBuilder.create()
            mDialog.show()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        val myIntent = Intent(this@RaceActivity, MainActivity::class.java)

        startActivity(myIntent)
    }

    private fun zeroToUp(numberOfTeams: Int, stintNumber: Int, allTeamTogether: Boolean?): String {
        if (allTeamTogether == true) {
            when (numberOfTeams) {
                5 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:28 - 0:32"
                        }

                        3 -> {
                            return "0:58 - 1:02"
                        }

                        4 -> {
                            return "1:28 - 1:32"
                        }

                        5 -> {
                            return "1:58 - 2:02"
                        }

                        6 -> {
                            return "2:28 - 2:32"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                6 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:23 - 0:28"
                        }

                        3 -> {
                            return "0:48 - 0:53"
                        }

                        4 -> {
                            return "1:14 - 1:19"
                        }

                        5 -> {
                            return "1:40 - 1:45"
                        }

                        6 -> {
                            return "2:06 - 2:11"
                        }

                        7 -> {
                            return "2:32 - 2:37"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                7 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:20 - 0:25"
                        }

                        3 -> {
                            return "0:43 - 0:48"
                        }

                        4 -> {
                            return "1:05 - 1:10"
                        }

                        5 -> {
                            return "1:28 - 1:33"
                        }

                        6 -> {
                            return "1:50 - 1:55"
                        }

                        7 -> {
                            return "2:12 - 2:17"
                        }

                        8 -> {
                            return "2:35 - 2:40"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                8 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:17 - 0:23"
                        }

                        3 -> {
                            return "0:37 - 0:43"
                        }

                        4 -> {
                            return "0:57 - 1:03"
                        }

                        5 -> {
                            return "1:17 - 1:23"
                        }

                        6 -> {
                            return "1:37 - 1:43"
                        }

                        7 -> {
                            return "1:57 - 2:03"
                        }

                        8 -> {
                            return "2:17 - 2:23"
                        }

                        9 -> {
                            return "2:37 - 2:43"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                9 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:15 - 0:22"
                        }

                        3 -> {
                            return "0:33 - 0:40"
                        }

                        4 -> {
                            return "0:51 - 0:58"
                        }

                        5 -> {
                            return "1:09 - 1:16"
                        }

                        6 -> {
                            return "1:27 - 1:34"
                        }

                        7 -> {
                            return "1:45 - 1:52"
                        }

                        8 -> {
                            return "2:03 - 2:10"
                        }

                        9 -> {
                            return "2:21 - 2:28"
                        }

                        10 -> {
                            return "2:39 - 2:46"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                10 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:13 - 0:20"
                        }

                        3 -> {
                            return "0:29 - 0:36"
                        }

                        4 -> {
                            return "0:46 - 0:53"
                        }

                        5 -> {
                            return "1:02 - 1:09"
                        }

                        6 -> {
                            return "1:18 - 1:25"
                        }

                        7 -> {
                            return "1:35 - 1:42"
                        }

                        8 -> {
                            return "1:51 - 1:58"
                        }

                        9 -> {
                            return "2:08 - 2:15"
                        }

                        10 -> {
                            return "2:24 - 2:31"
                        }

                        11 -> {
                            return "2:40 - 2:47"
                        }

                        else -> {
                            return ""
                        }
                    }
                }
                11 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:11 - 0:19"
                        }

                        3 -> {
                            return "0:26 - 0:34"
                        }

                        4 -> {
                            return "0:41 - 0:49"
                        }

                        5 -> {
                            return "0:56 - 1:04"
                        }

                        6 -> {
                            return "1:11 - 1:19"
                        }

                        7 -> {
                            return "1:26 - 1:34"
                        }

                        8 -> {
                            return "1:41 - 1:49"
                        }

                        9 -> {
                            return "1:56 - 2:04"
                        }

                        10 -> {
                            return "2:11 - 2:19"
                        }

                        11 -> {
                            return "2:26 - 2:34"
                        }

                        12 -> {
                            return "2:41 - 2:49"
                        }

                        else -> {
                            return ""
                        }
                    }
                }
                //TODO
                12 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:21 - 0:31"
                        }

                        3 -> {
                            return "0:47 - 0:57"
                        }

                        4 -> {
                            return "1:13 - 1:23"
                        }

                        5 -> {
                            return "1:39 - 1:49"
                        }

                        6 -> {
                            return "2:05 - 2:15"
                        }

                        7 -> {
                            return "2:30 - 2:40"
                        }

                        else -> {
                            return ""
                        }
                    }
                }
                //TODO
                13 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:17 - 0:28"
                        }

                        3 -> {
                            return "0:39 - 0:50"
                        }

                        4 -> {
                            return "1:02 - 1:13"
                        }

                        5 -> {
                            return "1:24 - 1:35"
                        }

                        6 -> {
                            return "1:47 - 1:58"
                        }

                        7 -> {
                            return "2:09 - 2:20"
                        }

                        8 -> {
                            return "2:32 - 2:43"
                        }

                        else -> {
                            return ""
                        }
                    }
                }
                //TODO
                14 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:17 - 0:28"
                        }

                        3 -> {
                            return "0:39 - 0:50"
                        }

                        4 -> {
                            return "1:02 - 1:13"
                        }

                        5 -> {
                            return "1:24 - 1:35"
                        }

                        6 -> {
                            return "1:47 - 1:58"
                        }

                        7 -> {
                            return "2:09 - 2:20"
                        }

                        8 -> {
                            return "2:32 - 2:43"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                else -> {
                    return ""
                }
            }
        }
        else {
            when (numberOfTeams) {
                5 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:28 - 0:32"
                        }

                        3 -> {
                            return "0:58 - 1:02"
                        }

                        4 -> {
                            return "1:28 - 1:32"
                        }

                        5 -> {
                            return "1:58 - 2:02"
                        }

                        6 -> {
                            return "2:28 - 2:32"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                6 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:23 - 0:28"
                        }

                        3 -> {
                            return "0:48 - 0:53"
                        }

                        4 -> {
                            return "1:14 - 1:19"
                        }

                        5 -> {
                            return "1:40 - 1:45"
                        }

                        6 -> {
                            return "2:06 - 2:11"
                        }

                        7 -> {
                            return "2:32 - 2:37"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                7 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:20 - 0:25"
                        }

                        3 -> {
                            return "0:43 - 0:48"
                        }

                        4 -> {
                            return "1:05 - 1:10"
                        }

                        5 -> {
                            return "1:28 - 1:33"
                        }

                        6 -> {
                            return "1:50 - 1:55"
                        }

                        7 -> {
                            return "2:12 - 2:17"
                        }

                        8 -> {
                            return "2:35 - 2:40"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                8 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:17 - 0:23"
                        }

                        3 -> {
                            return "0:37 - 0:43"
                        }

                        4 -> {
                            return "0:57 - 1:03"
                        }

                        5 -> {
                            return "1:17 - 1:23"
                        }

                        6 -> {
                            return "1:37 - 1:43"
                        }

                        7 -> {
                            return "1:57 - 2:03"
                        }

                        8 -> {
                            return "2:17 - 2:23"
                        }

                        9 -> {
                            return "2:37 - 2:43"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                9 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:15 - 0:22"
                        }

                        3 -> {
                            return "0:33 - 0:40"
                        }

                        4 -> {
                            return "0:51 - 0:58"
                        }

                        5 -> {
                            return "1:09 - 1:16"
                        }

                        6 -> {
                            return "1:27 - 1:34"
                        }

                        7 -> {
                            return "1:45 - 1:52"
                        }

                        8 -> {
                            return "2:03 - 2:10"
                        }

                        9 -> {
                            return "2:21 - 2:28"
                        }

                        10 -> {
                            return "2:39 - 2:46"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                10 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:25 - 0:35"
                        }

                        3 -> {
                            return "0:55 - 1:05"
                        }

                        4 -> {
                            return "1:25 - 1:35"
                        }

                        5 -> {
                            return "1:55 - 2:05"
                        }

                        6 -> {
                            return "2:25 - 2:35"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                11 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:21 - 0:31"
                        }

                        3 -> {
                            return "0:47 - 0:57"
                        }

                        4 -> {
                            return "1:13 - 1:23"
                        }

                        5 -> {
                            return "1:39 - 1:49"
                        }

                        6 -> {
                            return "2:05 - 2:15"
                        }

                        7 -> {
                            return "2:30 - 2:40"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                12 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:21 - 0:31"
                        }

                        3 -> {
                            return "0:47 - 0:57"
                        }

                        4 -> {
                            return "1:13 - 1:23"
                        }

                        5 -> {
                            return "1:39 - 1:49"
                        }

                        6 -> {
                            return "2:05 - 2:15"
                        }

                        7 -> {
                            return "2:30 - 2:40"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                13 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:17 - 0:28"
                        }

                        3 -> {
                            return "0:39 - 0:50"
                        }

                        4 -> {
                            return "1:02 - 1:13"
                        }

                        5 -> {
                            return "1:24 - 1:35"
                        }

                        6 -> {
                            return "1:47 - 1:58"
                        }

                        7 -> {
                            return "2:09 - 2:20"
                        }

                        8 -> {
                            return "2:32 - 2:43"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                14 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "0:17 - 0:28"
                        }

                        3 -> {
                            return "0:39 - 0:50"
                        }

                        4 -> {
                            return "1:02 - 1:13"
                        }

                        5 -> {
                            return "1:24 - 1:35"
                        }

                        6 -> {
                            return "1:47 - 1:58"
                        }

                        7 -> {
                            return "2:09 - 2:20"
                        }

                        8 -> {
                            return "2:32 - 2:43"
                        }

                        else -> {
                            return ""
                        }
                    }
                }

                else -> {
                    return ""
                }
            }
        }
    }

    private fun upToZero(numberOfTeams: Int, stintNumber: Int, allTeamTogether: Boolean?): String {
        if (allTeamTogether == true) {
            when (numberOfTeams) {
                5 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:32 - 2:28"
                        }
                        3 -> {
                            return "2:02 - 1:58"
                        }
                        4 -> {
                            return "1:32 - 1:28"
                        }
                        5 -> {
                            return "1:02 - 0:58"
                        }
                        6 -> {
                            return "0:32 - 0:28"
                        }
                        else -> {return ""}
                    }
                }
                6 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:37 - 2:32"
                        }
                        3 -> {
                            return "2:12 - 2:07"
                        }
                        4 -> {
                            return "1:46 - 1:41"
                        }
                        5 -> {
                            return "1:20 - 1:15"
                        }
                        6 -> {
                            return "0:54 - 0:49"
                        }
                        7 -> {
                            return "0:28 - 0:23"
                        }
                        else -> {return ""}
                    }
                }
                7 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:40 - 2:35"
                        }
                        3 -> {
                            return "2:17 - 2:12"
                        }
                        4 -> {
                            return "1:55 - 1:50"
                        }
                        5 -> {
                            return "1:32 - 1:27"
                        }
                        6 -> {
                            return "1:10 - 1:05"
                        }
                        7 -> {
                            return "0:48 - 0:43"
                        }
                        8 -> {
                            return "0:25 - 0:20"
                        }
                        else -> {return ""}
                    }
                }
                8 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:43 - 2:37"
                        }
                        3 -> {
                            return "2:23 - 2:17"
                        }
                        4 -> {
                            return "2:03 - 1:57"
                        }
                        5 -> {
                            return "1:43 - 1:37"
                        }
                        6 -> {
                            return "1:23 - 1:17"
                        }
                        7 -> {
                            return "1:03 - 0:57"
                        }
                        8 -> {
                            return "0:43 - 0:37"
                        }
                        9 -> {
                            return "0:23 - 0:17"
                        }
                        else -> {return ""}
                    }
                }
                9 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:45 - 2:38"
                        }
                        3 -> {
                            return "2:27 - 2:20"
                        }
                        4 -> {
                            return "2:09 - 2:02"
                        }
                        5 -> {
                            return "1:51 - 1:44"
                        }
                        6 -> {
                            return "1:33 - 1:26"
                        }
                        7 -> {
                            return "1:15 - 1:08"
                        }
                        8 -> {
                            return "0:57 - 0:50"
                        }
                        9 -> {
                            return "0:39 - 0:32"
                        }
                        10 -> {
                            return "0:21 - 0:14"
                        }
                        else -> {return ""}
                    }
                }
                10 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "2:47 - 2:40"
                        }

                        3 -> {
                            return "2:31 - 2:24"
                        }

                        4 -> {
                            return "2:14 - 2:07"
                        }

                        5 -> {
                            return "1:58 - 1:51"
                        }

                        6 -> {
                            return "1:42 - 1:35"
                        }

                        7 -> {
                            return "1:25 - 1:18"
                        }

                        8 -> {
                            return "1:09 - 1:02"
                        }

                        9 -> {
                            return "0:52 - 0:45"
                        }

                        10 -> {
                            return "0:36 - 0:29"
                        }

                        11 -> {
                            return "0:20 - 0:13"
                        }

                        else -> {
                            return ""
                        }
                    }
                }
                11 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:49 - 2:41"
                        }
                        3 -> {
                            return "2:34 - 2:26"
                        }
                        4 -> {
                            return "2:19 - 2:11"
                        }
                        5 -> {
                            return "2:04 - 1:56"
                        }
                        6 -> {
                            return "1:49 - 1:41"
                        }
                        7 -> {
                            return "1:34 - 1:26"
                        }
                        8 -> {
                            return "1:19 - 1:11"
                        }

                        9 -> {
                            return "1:04 - 0:56"
                        }

                        10 -> {
                            return "0:49 - 0:41"
                        }

                        11 -> {
                            return "0:34 - 0:26"
                        }

                        12 -> {
                            return "0:19 - 0:11"
                        }

                        else -> {
                            return ""
                        }
                    }
                }
                //TODO
                12 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:39 - 2:29"
                        }
                        3 -> {
                            return "2:13 - 2:03"
                        }
                        4 -> {
                            return "1:47 - 1:37"
                        }
                        5 -> {
                            return "1:21 - 1:11"
                        }
                        6 -> {
                            return "0:55 - 0:45"
                        }
                        7 -> {
                            return "0:30 - 0:20"
                        }
                        else -> {
                            return ""
                        }
                    }
                }
                //TODO
                13 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:43 - 2:32"
                        }
                        3 -> {
                            return "2:21 - 2:10"
                        }
                        4 -> {
                            return "1:58 - 1:47"
                        }
                        5 -> {
                            return "1:36 - 1:25"
                        }
                        6 -> {
                            return "1:13 - 1:02"
                        }
                        7 -> {
                            return "0:51 - 0:40"
                        }
                        8 -> {
                            return "0:28 - 0:17"
                        }
                        else -> {return ""}
                    }
                }
                //TODO
                14 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:43 - 2:32"
                        }
                        3 -> {
                            return "2:21 - 2:10"
                        }
                        4 -> {
                            return "1:58 - 1:47"
                        }
                        5 -> {
                            return "1:36 - 1:25"
                        }
                        6 -> {
                            return "1:13 - 1:02"
                        }
                        7 -> {
                            return "0:51 - 0:40"
                        }
                        8 -> {
                            return "0:28 - 0:17"
                        }
                        else -> {return ""}
                    }
                }
                else -> {return ""}
            }
        }
        else {
            when (numberOfTeams) {
                5 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:32 - 2:28"
                        }
                        3 -> {
                            return "2:02 - 1:58"
                        }
                        4 -> {
                            return "1:32 - 1:28"
                        }
                        5 -> {
                            return "1:02 - 0:58"
                        }
                        6 -> {
                            return "0:32 - 0:28"
                        }
                        else -> {return ""}
                    }
                }
                6 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:37 - 2:32"
                        }
                        3 -> {
                            return "2:12 - 2:07"
                        }
                        4 -> {
                            return "1:46 - 1:41"
                        }
                        5 -> {
                            return "1:20 - 1:15"
                        }
                        6 -> {
                            return "0:54 - 0:49"
                        }
                        7 -> {
                            return "0:28 - 0:23"
                        }
                        else -> {return ""}
                    }
                }
                7 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:40 - 2:35"
                        }
                        3 -> {
                            return "2:17 - 2:12"
                        }
                        4 -> {
                            return "1:55 - 1:50"
                        }
                        5 -> {
                            return "1:32 - 1:27"
                        }
                        6 -> {
                            return "1:10 - 1:05"
                        }
                        7 -> {
                            return "0:48 - 0:43"
                        }
                        8 -> {
                            return "0:25 - 0:20"
                        }
                        else -> {return ""}
                    }
                }
                8 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:43 - 2:37"
                        }
                        3 -> {
                            return "2:23 - 2:17"
                        }
                        4 -> {
                            return "2:03 - 1:57"
                        }
                        5 -> {
                            return "1:43 - 1:37"
                        }
                        6 -> {
                            return "1:23 - 1:17"
                        }
                        7 -> {
                            return "1:03 - 0:57"
                        }
                        8 -> {
                            return "0:43 - 0:37"
                        }
                        9 -> {
                            return "0:23 - 0:17"
                        }
                        else -> {return ""}
                    }
                }
                9 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:45 - 2:38"
                        }
                        3 -> {
                            return "2:27 - 2:20"
                        }
                        4 -> {
                            return "2:09 - 2:02"
                        }
                        5 -> {
                            return "1:51 - 1:44"
                        }
                        6 -> {
                            return "1:33 - 1:26"
                        }
                        7 -> {
                            return "1:15 - 1:08"
                        }
                        8 -> {
                            return "0:57 - 0:50"
                        }
                        9 -> {
                            return "0:39 - 0:32"
                        }
                        10 -> {
                            return "0:21 - 0:14"
                        }
                        else -> {return ""}
                    }
                }
                10 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }

                        2 -> {
                            return "2:35 - 2:25"
                        }

                        3 -> {
                            return "2:05 - 1:55"
                        }

                        4 -> {
                            return "1:35 - 1:25"
                        }

                        5 -> {
                            return "1:05 - 0:55"
                        }

                        6 -> {
                            return "0:35 - 0:25"
                        }

                        else -> {
                            return ""
                        }
                    }
                }
                11 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:39 - 2:29"
                        }
                        3 -> {
                            return "2:13 - 2:03"
                        }
                        4 -> {
                            return "1:47 - 1:37"
                        }
                        5 -> {
                            return "1:21 - 1:11"
                        }
                        6 -> {
                            return "0:55 - 0:45"
                        }
                        7 -> {
                            return "0:30 - 0:20"
                        }
                        else -> {
                            return ""
                        }
                    }
                }
                12 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:39 - 2:29"
                        }
                        3 -> {
                            return "2:13 - 2:03"
                        }
                        4 -> {
                            return "1:47 - 1:37"
                        }
                        5 -> {
                            return "1:21 - 1:11"
                        }
                        6 -> {
                            return "0:55 - 0:45"
                        }
                        7 -> {
                            return "0:30 - 0:20"
                        }
                        else -> {
                            return ""
                        }
                    }
                }
                13 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:43 - 2:32"
                        }
                        3 -> {
                            return "2:21 - 2:10"
                        }
                        4 -> {
                            return "1:58 - 1:47"
                        }
                        5 -> {
                            return "1:36 - 1:25"
                        }
                        6 -> {
                            return "1:13 - 1:02"
                        }
                        7 -> {
                            return "0:51 - 0:40"
                        }
                        8 -> {
                            return "0:28 - 0:17"
                        }
                        else -> {return ""}
                    }
                }
                14 -> {
                    when (stintNumber) {
                        1 -> {
                            return ""
                        }
                        2 -> {
                            return "2:43 - 2:32"
                        }
                        3 -> {
                            return "2:21 - 2:10"
                        }
                        4 -> {
                            return "1:58 - 1:47"
                        }
                        5 -> {
                            return "1:36 - 1:25"
                        }
                        6 -> {
                            return "1:13 - 1:02"
                        }
                        7 -> {
                            return "0:51 - 0:40"
                        }
                        8 -> {
                            return "0:28 - 0:17"
                        }
                        else -> {return ""}
                    }
                }
                else -> {return ""}
            }
        }
    }
}