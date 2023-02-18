package hu.bme.aut.android.enduranceoagb

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
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
            val showDetailsIntent = Intent()
            showDetailsIntent.setClass(this@RaceActivity, TeamActivity::class.java)
            showDetailsIntent.putExtra(TeamActivity.EXTRA_RACE_NAME, raceId)
            startActivity(showDetailsIntent)
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
                                val stintNumber =
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
                                startActivity(showDetailsIntent)
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

    private fun zeroToUp(numberOfTeams: Int, stintNumber: Int): String {
        when (numberOfTeams) {
            5 -> {
                when (stintNumber) {
                    1 -> {
                        return ""
                    }
                    2 -> {
                        return "0:27 - 0:32"
                    }
                    3 -> {
                        return "0:57 - 1:02"
                    }
                    4 -> {
                        return "1:27 - 1:32"
                    }
                    5 -> {
                        return "1:57 - 2:02"
                    }
                    6 -> {
                        return "2:27 - 2:32"
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
                        return "0:23 - 0:29"
                    }
                    3 -> {
                        return "0:48 - 0:54"
                    }
                    4 -> {
                        return "1:14 - 1:20"
                    }
                    5 -> {
                        return "1:39 - 1:45"
                    }
                    6 -> {
                        return "2:05 - 2:11"
                    }
                    7 -> {
                        return "2:30 - 2:36"
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
                        return "0:19 - 0:26"
                    }
                    3 -> {
                        return "0:41 - 0:48"
                    }
                    4 -> {
                        return "1:04 - 1:11"
                    }
                    5 -> {
                        return "1:26 - 1:33"
                    }
                    6 -> {
                        return "1:49 - 1:56"
                    }
                    7 -> {
                        return "2:11 - 2:18"
                    }
                    8 -> {
                        return "2:34 - 2:41"
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
                        return "0:16 - 0:24"
                    }
                    3 -> {
                        return "0:36 - 0:44"
                    }
                    4 -> {
                        return "0:56 - 1:04"
                    }
                    5 -> {
                        return "1:16 - 1:24"
                    }
                    6 -> {
                        return "1:36 - 1:44"
                    }
                    7 -> {
                        return "1:56 - 2:04"
                    }
                    8 -> {
                        return "2:16 - 2:24"
                    }
                    9 -> {
                        return "2:36 - 2:44"
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
                        return "0:16 - 0:24"
                    }
                    3 -> {
                        return "0:36 - 0:44"
                    }
                    4 -> {
                        return "0:56 - 1:04"
                    }
                    5 -> {
                        return "1:16 - 1:24"
                    }
                    6 -> {
                        return "1:36 - 1:44"
                    }
                    7 -> {
                        return "1:56 - 2:04"
                    }
                    8 -> {
                        return "2:16 - 2:24"
                    }
                    9 -> {
                        return "2:36 - 2:44"
                    }
                    10 -> {
                        return "2:56 - 3:04"
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
                        return "0:15 - 0:25"
                    }
                    3 -> {
                        return "0:35 - 0:45"
                    }
                    4 -> {
                        return "0:55 - 1:05"
                    }
                    5 -> {
                        return "1:15 - 1:25"
                    }
                    6 -> {
                        return "1:35 - 1:45"
                    }
                    7 -> {
                        return "1:55 - 2:05"
                    }
                    8 -> {
                        return "2:15 - 2:25"
                    }
                    9 -> {
                        return "2:35 - 2:45"
                    }
                    10 -> {
                        return "2:55 - 3:05"
                    }
                    11 -> {
                        return "3:15 - 3:25"
                    }
                    else -> {return ""}
                }
            }
            11 -> {
                when (stintNumber) {
                    1 -> {
                        return ""
                    }
                    2 -> {
                        return "0:15 - 0:25"
                    }
                    3 -> {
                        return "0:35 - 0:45"
                    }
                    4 -> {
                        return "0:55 - 1:05"
                    }
                    5 -> {
                        return "1:15 - 1:25"
                    }
                    6 -> {
                        return "1:35 - 1:45"
                    }
                    7 -> {
                        return "1:55 - 2:05"
                    }
                    8 -> {
                        return "2:15 - 2:25"
                    }
                    9 -> {
                        return "2:35 - 2:45"
                    }
                    10 -> {
                        return "2:55 - 3:05"
                    }
                    11 -> {
                        return "3:15 - 3:25"
                    }
                    12 -> {
                        return "3:35 - 3:45"
                    }
                    else -> {return ""}
                }
            }
            else -> {return ""}
        }
    }

    private fun upToZero(numberOfTeams: Int, stintNumber: Int): String {
        when (numberOfTeams) {
            5 -> {
                when (stintNumber) {
                    1 -> {
                        return ""
                    }
                    2 -> {
                        return "2:33 - 2:28"
                    }
                    3 -> {
                        return "2:03 - 1:58"
                    }
                    4 -> {
                        return "1:33 - 1:28"
                    }
                    5 -> {
                        return "1:03 - 0:58"
                    }
                    6 -> {
                        return "0:33 - 0:28"
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
                        return "2:37 - 2:31"
                    }
                    3 -> {
                        return "2:12 - 2:06"
                    }
                    4 -> {
                        return "1:46 - 1:40"
                    }
                    5 -> {
                        return "1:21 - 1:15"
                    }
                    6 -> {
                        return "0:55 - 0:49"
                    }
                    7 -> {
                        return "0:30 - 0:24"
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
                        return "2:41 - 2:34"
                    }
                    3 -> {
                        return "2:19 - 2:12"
                    }
                    4 -> {
                        return "1:56 - 1:49"
                    }
                    5 -> {
                        return "1:34 - 1:27"
                    }
                    6 -> {
                        return "1:11 - 1:04"
                    }
                    7 -> {
                        return "0:49 - 0:42"
                    }
                    8 -> {
                        return "0:26 - 0:19"
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
                        return "2:44 - 2:36"
                    }
                    3 -> {
                        return "2:24 - 2:16"
                    }
                    4 -> {
                        return "2:04 - 1:56"
                    }
                    5 -> {
                        return "1:44 - 1:36"
                    }
                    6 -> {
                        return "1:24 - 1:16"
                    }
                    7 -> {
                        return "1:04 - 0:56"
                    }
                    8 -> {
                        return "0:44 - 0:36"
                    }
                    9 -> {
                        return "0:24 - 0:16"
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
                        return "3:04 - 2:56"
                    }
                    3 -> {
                        return "2:44 - 2:36"
                    }
                    4 -> {
                        return "2:24 - 2:16"
                    }
                    5 -> {
                        return "2:04 - 1:56"
                    }
                    6 -> {
                        return "1:44 - 1:36"
                    }
                    7 -> {
                        return "1:24 - 1:16"
                    }
                    8 -> {
                        return "1:04 - 0:56"
                    }
                    9 -> {
                        return "0:44 - 0:36"
                    }
                    10 -> {
                        return "0:24 - 0:16"
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
                        return "3:25 - 3:15"
                    }
                    3 -> {
                        return "3:05 - 2:55"
                    }
                    4 -> {
                        return "2:45 - 2:35"
                    }
                    5 -> {
                        return "2:25 - 2:15"
                    }
                    6 -> {
                        return "2:05 - 1:55"
                    }
                    7 -> {
                        return "1:45 - 1:35"
                    }
                    8 -> {
                        return "1:25 - 1:15"
                    }
                    9 -> {
                        return "1:05 - 0:55"
                    }
                    10 -> {
                        return "0:45 - 0:35"
                    }
                    11 -> {
                        return "0:25 - 0:15"
                    }
                    else -> {return ""}
                }
            }
            11 -> {
                when (stintNumber) {
                    1 -> {
                        return ""
                    }
                    2 -> {
                        return "3:45 - 3:35"
                    }
                    3 -> {
                        return "3:25 - 3:15"
                    }
                    4 -> {
                        return "3:05 - 2:55"
                    }
                    5 -> {
                        return "2:45 - 2:35"
                    }
                    6 -> {
                        return "2:25 - 2:15"
                    }
                    7 -> {
                        return "2:05 - 1:55"
                    }
                    8 -> {
                        return "1:45 - 1:35"
                    }
                    9 -> {
                        return "1:25 - 1:15"
                    }
                    10 -> {
                        return "1:05 - 0:55"
                    }
                    11 -> {
                        return "0:45 - 0:35"
                    }
                    12 -> {
                        return "0:25 - 0:15"
                    }
                    else -> {return ""}
                }
            }
            else -> {return ""}
        }
    }
}