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
import hu.bme.aut.android.enduranceoagb.adapter.DetailsStintAdapter
import hu.bme.aut.android.enduranceoagb.adapter.DetailsStintFragmentAdapter
import hu.bme.aut.android.enduranceoagb.data.BoxTime
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityBoxtimeBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityDetailsstintBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityDetailsstintfragmentBinding
import hu.bme.aut.android.enduranceoagb.databinding.DetailsstintfragmentListBinding
import hu.bme.aut.android.enduranceoagb.fragments.NewStintFragment


class DetailsStintActivity : Fragment(), DetailsStintAdapter.DetailsStintItemClickListener, NewStintFragment.NewStintListener {
    private lateinit var binding: ActivityDetailsstintBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: DetailsStintAdapter

    private val CHANNEL_ID = "channel_id_01"
    private val notificationId = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ActivityDetailsstintBinding.inflate(layoutInflater)

        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(this.context, 2)

        binding.rvMainDetailsStint.setLayoutManager(layoutManager)

        adapter = DetailsStintAdapter(this)

        binding.rvMainDetailsStint.adapter = adapter

        val bundle = arguments
        val message = bundle?.getString("mText")

        val handler = Handler()

        if (message == null) {
            handler.postDelayed(Runnable { layoutManager.smoothScrollToPosition(binding.rvMainDetailsStint, null, 1) }, 100)
        }
        else {
            handler.postDelayed(Runnable { layoutManager.smoothScrollToPosition(binding.rvMainDetailsStint, null, message.toInt()) }, 100)
        }

        binding.rvMainDetailsStint.setItemViewCacheSize(16)

        createNotificationChannel()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        //loadBackgroundBoxItems()
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()


        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val itemsTeams : MutableList<Teams>? = mutableListOf()

        val numberOfStint : MutableList<Int>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                //var change = "Etap: $stintId"
                if (!p0.result.child("AllStint").child("numberOfStint").child(stintId.toString())
                        .child("hasDetailsStintReady").value.toString().toBoolean()
                ) {
                    for (element in p0.result.child("Teams").children) {
                        val addTeam = Teams(
                            element.child("Info").child("nameTeam").value.toString(),
                            element.child("Info").child("people").value.toString().toInt(),
                            element.child("Info").child("teamNumber").value.toString().toInt(),
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
                            element.child("Info").child("group").value.toString().toIntOrNull()

                        )

                        itemsTeams?.add(addTeam)
                        if (stintId.toString().toInt() in 1..9) {
                            val change = "Etap: 0$stintId"
                            dbRef.child("Cserék").child(change).child("1 - Versenyzők")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null2")
                            dbRef.child("Cserék").child(change).child("4 - Plusz súlyok")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null3")
                            dbRef.child("Cserék").child(change).child("2 - Összsúly")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null4")
                            dbRef.child("Cserék").child(change).child("3 - Gépszámok")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null4")
                        }
                        else {
                            val changeOrigi = "Etap: $stintId"
                            dbRef.child("Cserék").child(changeOrigi).child("1 - Versenyzők")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null2")
                            dbRef.child("Cserék").child(changeOrigi).child("4 - Plusz súlyok")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null3")
                            dbRef.child("Cserék").child(changeOrigi).child("2 - Összsúly")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null4")
                            dbRef.child("Cserék").child(changeOrigi).child("3 - Gépszámok")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null4")
                        }


                        val stint = Stint(element.child("Info").child("nameTeam").value.toString(),
                            element.child("Info").child("teamNumber").value.toString().toInt(),
                            null, stintId.toString().toInt(), element.child("Info").child("shortTeamName").value.toString(),null, null, null, false, null)

                        val teamStint = stintId + "-" + element.child("Info").child("teamNumber").value.toString().toInt()

                        val changePush = "Etap: $stintId"

                        dbRef.child("Stints").child(changePush).child("Info").child(teamStint).setValue(stint)
                    }
                    dbRef.child("AllStint").child("numberOfStint").child(stintId.toString())
                        .child("hasDetailsStintReady").setValue(true)
                    val allTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                    var secondGroupFirst = p0.result.child("Info").child("secondGroup").value.toString().toIntOrNull()
                    if (secondGroupFirst == null) {
                        secondGroupFirst = 0
                    }
                    val firstGroupLast = allTeams - secondGroupFirst
                    //val allTeamTogether = p0.result.child("Info").child("allTeamTogether").value.toString().toBooleanStrictOrNull()
                    val firstMore = p0.result.child("Info").child("firstMore").value.toString().toBooleanStrictOrNull()
                    val secondMore = p0.result.child("Info").child("secondMore").value.toString().toBooleanStrictOrNull()
                    val equalGroup = p0.result.child("Info").child("equalGroup").value.toString().toBooleanStrictOrNull()
                    if (stintId.toInt() > 1) {
                        if (allTeams < 12) {
                            val prevStint = stintId.toInt() - 1
                            val prevParkKart = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$allTeams").child("kartNumber").value.toString().toInt()
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
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box11")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box12")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box21")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box22")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart)
                        }
                        else if (firstMore == true) {
                            val prevStint = stintId.toInt() - 1
                            val prevParkKart11 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$allTeams").child("kartNumber").value.toString().toInt()
                            val prevParkKart12 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-box11").child("kartNumber").value.toString().toInt()
                            val prevParkKart21 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$firstGroupLast").child("kartNumber").value.toString().toInt()
                            val prevParkKart22 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$firstGroupLast").child("kartNumber").value.toString().toInt()
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
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box11")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart11)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box12")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart12)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box21")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart21)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box22")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart22)
                        }
                        else if (secondMore == true) {
                            val prevStint = stintId.toInt() - 1
                            val prevParkKart11 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$allTeams").child("kartNumber").value.toString().toInt()
                            val prevParkKart12 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$allTeams").child("kartNumber").value.toString().toInt()
                            val prevParkKart21 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$firstGroupLast").child("kartNumber").value.toString().toInt()
                            val prevParkKart22 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-box21").child("kartNumber").value.toString().toInt()
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
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box11")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart11)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box12")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart12)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box21")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart21)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box22")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart22)
                        }
                        else if (equalGroup == true) {
                            val prevStint = stintId.toInt() - 1
                            val prevParkKart11 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$allTeams").child("kartNumber").value.toString().toInt()
                            val prevParkKart12 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$allTeams").child("kartNumber").value.toString().toInt()
                            val prevParkKart21 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$firstGroupLast").child("kartNumber").value.toString().toInt()
                            val prevParkKart22 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$firstGroupLast").child("kartNumber").value.toString().toInt()
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
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box11")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart11)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box12")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart12)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box21")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart21)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box22")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart22)
                        }
                        else {
                            val prevStint = stintId.toInt() - 1
                            val prevParkKart11 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$allTeams").child("kartNumber").value.toString().toInt()
                            val prevParkKart12 = p0.result.child("Stints").child("Etap: $prevStint").child("Info").child("$prevStint-$allTeams").child("kartNumber").value.toString().toInt()
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
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box11")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart11)
                            idNumber++

                            dbRef.child("Id").setValue(idNumber)
                            dbRef.child("Excel").child(idNumber.toString()).child("stintNumber").setValue("${stintId.toInt()}. etap")
                            dbRef.child("Excel").child(idNumber.toString()).child("teamNumber").setValue("box12")
                            dbRef.child("Excel").child(idNumber.toString()).child("driver").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("plusWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("totalWeight").setValue("-")
                            dbRef.child("Excel").child(idNumber.toString()).child("kartNumber").setValue(prevParkKart12)
                        }
                    }
                }
                else {
                    for (element in p0.result.child("Teams").children) {
                        val addTeam = Teams(
                            element.child("Info").child("nameTeam").value.toString(),
                            element.child("Info").child("people").value.toString().toInt(),
                            element.child("Info").child("teamNumber").value.toString().toInt(),
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
                            element.child("Info").child("group").value.toString().toIntOrNull()

                        )
                        itemsTeams?.add(addTeam)
                    }
                }

                loadBackgroundItems()

                val allStint = p0.result.child("Info").child("allStintNumber").value.toString().toInt()

                numberOfStint?.add(allStint)
                requireActivity().runOnUiThread {
                    adapter.teams(itemsTeams!!.toMutableList())
                    adapter.stints(numberOfStint!!.toMutableList())
                }

            }
        }
    }

    private fun loadBackgroundItems() {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val items : MutableList<Stint>? = mutableListOf()

        val itemsDrivers : MutableList<Drivers>? = mutableListOf()

        val change = "Etap: $stintId"

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {

                for (elem in p0.result.child("Drivers").children) {

                    val nameDriver = elem.child("nameDriver").value.toString()
                    val weight = elem.child("weight").value.toString().toDouble()

                    val addDriver = Drivers(nameDriver, weight)

                    itemsDrivers?.add(addDriver)
                }

                val numberOfTeam =
                    p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                for (iterator in 1..numberOfTeam) {
                    val stintTeam = stintId.toString() + "-" + iterator.toString()
                    val nameDriver = p0.result.child("Stints").child(change).child("Info")
                        .child(stintTeam).child("driverName").value.toString()
                    val plus = p0.result.child("Stints").child(change).child("Info")
                        .child(stintTeam).child("plusWeight").value.toString().toDoubleOrNull()

                    for (element in p0.result.child("Teams").children) {
                        val nameTeam = element.child("Info").child("nameTeam").value.toString()
                        val teamNumber =
                            element.child("Info").child("teamNumber").value.toString().toInt()
                        //val groupNumber =
                        //    element.child("Info").child("group").value.toString().toIntOrNull()
                        val shortTeamName = element.child("Info").child("shortTeamName").value.toString()
                        val driverWeight = p0.result.child("Teams").child(nameTeam).child("Drivers").child(nameDriver).child("weight").value.toString().toDoubleOrNull()
                        if (teamNumber == iterator) {
                            if (stintId.toString().toInt() == 1) {
                                val hasStintDone =
                                    p0.result.child("Stints").child(change).child("Info").children
                                for (ready in hasStintDone) {
                                    val hasStintD =
                                        ready.child("hasStintDone").value.toString().toBoolean()
                                    val teamNum = ready.child("teamNumber").value.toString().toIntOrNull()
                                    val stintNum =
                                        ready.child("numberStint").value.toString().toIntOrNull()
                                    val info = ready.child("info").value.toString()
                                    val kartNumber =
                                        ready.child("kartNumber").value.toString().toIntOrNull()
                                    val expectedKartNumber =
                                        p0.result.child("Teams").child(nameTeam).child("Info").child("startKartNumber").value.toString().toIntOrNull()
                                    val prevAvgWeight =
                                        ready.child("prevAvgWeight").value.toString().toDoubleOrNull()

                                    if (teamNum == teamNumber && stintNum == stintId.toString()
                                            .toInt()
                                    ) {
                                        val addStint = Stint(
                                            nameTeam,
                                            teamNumber,
                                            nameDriver,
                                            stintId.toString().toInt(),
                                            shortTeamName,
                                            plus,
                                            info,
                                            null,
                                            hasStintD, prevAvgWeight, driverWeight, kartNumber, expectedKartNumber, null, null, null
                                        )
                                        items?.add(addStint)
                                    }
                                }
                            } else if (stintId.toString().toInt() > 1) {
                                val hasStintDone =
                                    p0.result.child("Stints").child(change).child("Info").children
                                for (ready in hasStintDone) {
                                    val hasStintD =
                                        ready.child("hasStintDone").value.toString().toBoolean()
                                    val teamNum = ready.child("teamNumber").value.toString().toIntOrNull()
                                    val stintNum =
                                        ready.child("numberStint").value.toString().toIntOrNull()
                                    val info = ready.child("info").value.toString()
                                    val changePrevStint = "Etap: " + (stintId.toString().toInt()-1).toString()
                                    val changePrev = (stintId.toString().toInt()-1).toString() + "-" + teamNum
                                    val prevInfo = p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("info").value.toString()
                                    val kartNumber =
                                        ready.child("kartNumber").value.toString().toIntOrNull()
                                    val prevKartNumber =
                                        p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("kartNumber").value.toString().toIntOrNull()
                                    val prevDriverName =
                                        p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("driverName").value.toString()
                                    val prevPlusWeight =
                                        p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("plusWeight").value.toString().toDoubleOrNull()
                                    val prevAvgWeight = ready.child("prevAvgWeight").value.toString().toDoubleOrNull()

                                    val secondGroupFirst = p0.result.child("Info").child("secondGroup").value.toString().toIntOrNull()

                                    if (teamNumber > 1 && teamNumber != secondGroupFirst) {
                                        if (teamNum.toString().toIntOrNull() != null) {
                                            val changePrevTeam = (stintId.toString()
                                                .toInt() - 1).toString() + "-" + (teamNum.toString()
                                                .toInt() - 1)
                                            val expectedKartNumber =
                                                p0.result.child("Stints").child(changePrevStint)
                                                    .child("Info").child(changePrevTeam)
                                                    .child("kartNumber").value.toString()
                                                    .toIntOrNull()

                                            if (teamNum == teamNumber && stintNum == stintId.toString()
                                                    .toInt()
                                            ) {
                                                val addStint = Stint(
                                                    nameTeam,
                                                    teamNumber,
                                                    nameDriver,
                                                    stintId.toString().toInt(),
                                                    shortTeamName,
                                                    plus,
                                                    info,
                                                    prevInfo,
                                                    hasStintD, prevAvgWeight, driverWeight,
                                                    kartNumber, expectedKartNumber, prevDriverName, prevPlusWeight, prevKartNumber
                                                )
                                                items?.add(addStint)
                                            }
                                        } else if (teamNumber == 1) {
                                            /*val changePrevTeam = (stintId.toString()
                                                .toInt() - 1).toString() + "-box"
                                            val expectedKartNumber =
                                                p0.result.child("Stints").child(changePrevStint)
                                                    .child("Info").child(changePrevTeam)
                                                    .child("kartNumber").value.toString()
                                                    .toIntOrNull()

                                            if (teamNum == teamNumber && stintNum == stintId.toString()
                                                    .toInt()
                                            ) {
                                                val addStint = Stint(
                                                    nameTeam,
                                                    teamNumber,
                                                    nameDriver,
                                                    stintId.toString().toInt(),
                                                    shortTeamName,
                                                    plus,
                                                    info,
                                                    prevInfo,
                                                    hasStintD, prevAvgWeight, driverWeight,
                                                    kartNumber, expectedKartNumber, prevDriverName, prevPlusWeight, prevKartNumber
                                                )
                                                items?.add(addStint)
                                            }*/

                                            println("HIBA!")
                                        }

                                    } else if (teamNumber == 1) {
                                        val changePrevTeam = (stintId.toString()
                                            .toInt() - 1).toString() + "-" + "box12"
                                        val expectedKartNumber =
                                            p0.result.child("Stints").child(changePrevStint)
                                                .child("Info").child(changePrevTeam)
                                                .child("kartNumber").value.toString()
                                                .toIntOrNull()


                                        if (teamNum == teamNumber && stintNum == stintId.toString()
                                                .toInt()
                                        ) {
                                            val addStint = Stint(
                                                nameTeam,
                                                teamNumber,
                                                nameDriver,
                                                stintId.toString().toInt(),
                                                shortTeamName,
                                                plus,
                                                info,
                                                prevInfo,
                                                hasStintD, prevAvgWeight, driverWeight,
                                                kartNumber, expectedKartNumber, prevDriverName, prevPlusWeight, prevKartNumber
                                            )
                                            items?.add(addStint)
                                        }
                                    }
                                    else if (teamNumber == secondGroupFirst) {
                                        val changePrevTeam = (stintId.toString()
                                            .toInt() - 1).toString() + "-" + "box22"
                                        val expectedKartNumber =
                                            p0.result.child("Stints").child(changePrevStint)
                                                .child("Info").child(changePrevTeam)
                                                .child("kartNumber").value.toString()
                                                .toIntOrNull()


                                        if (teamNum == teamNumber && stintNum == stintId.toString()
                                                .toInt()
                                        ) {
                                            val addStint = Stint(
                                                nameTeam,
                                                teamNumber,
                                                nameDriver,
                                                stintId.toString().toInt(),
                                                shortTeamName,
                                                plus,
                                                info,
                                                prevInfo,
                                                hasStintD, prevAvgWeight, driverWeight,
                                                kartNumber, expectedKartNumber, prevDriverName, prevPlusWeight, prevKartNumber
                                            )
                                            items?.add(addStint)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                requireActivity().runOnUiThread {
                    adapter.update2(items!!.toMutableList())
                    adapter.drivers(itemsDrivers!!.toMutableList())
                }
            }
        }
    }

    override fun onNewStintListener(position: Int, teamNumber: Int, teamName: String, stintDone: Boolean, driverName: String?, plusWeight: Double?, shortTeamName: String?, driverWeight: Double?, prevTotalWeight: Double?) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val change = "Etap: ${stintId.toString().toInt()+1}"
                val teamStint = "${stintId.toString().toInt()+1}-$teamNumber"
                //val secondGroupFirst = p0.result.child("Info").child("secondGroup").value.toString().toIntOrNull()

                val numberOfTeams =
                    p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                val secondGroupFirstOri = p0.result.child("Info").child("secondGroup").value.toString().toIntOrNull()
                if (secondGroupFirstOri != 1) {
                    //?.plus(1)
                    //A:7
                    //B:6
                    //C:7 (all: 12)
                    val firstGroupAll = secondGroupFirstOri?.minus(1)
                    //A: x = 7 - 1 = 6
                    //B: x = 6 - 1 = 5
                    //C: x = 7 - 1 = 6
                    val secondGroupLast = numberOfTeams - firstGroupAll!!
                    //A: x = 11 - 6 = 5
                    //B: x = 11 - 5 = 6
                    //C: x = 12 - 6 = 6
                    val secondGroupFirstx = secondGroupLast + 1
                    //A: x = 5 + 1 = 6
                    //B: X = 6 + 1 = 7
                    //C: x = 6 + 1 = 7
                    if (p0.result.child("Stints").child(change).child("Info").child(teamStint)
                            .child("driverName").exists()
                    ) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Figyelem!")
                        builder.setMessage("Ezt az etapot már nem tudod módosítani!")

                        builder.setPositiveButton(
                            hu.bme.aut.android.enduranceoagb.R.string.button_ok,
                            null
                        )
                        builder.show()
                    } else if (stintDone) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Figyelem!")
                        builder.setMessage("Ezt az etapot egyszer már létrehoztad. Biztos, hogy módosítani szeretnéd?")

                        builder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.yes) { dialog, which ->
                            val fragment = NewStintFragment.newInstance(
                                position.toString(),
                                stintId.toString(),
                                teamName,
                                teamNumber.toString(),
                                stintDone.toString(),
                                driverName,
                                plusWeight.toString(),
                                shortTeamName,
                                driverWeight.toString(),
                                prevTotalWeight.toString(),
                                secondGroupFirstx.toString(),
                                "false"
                            )
                            fragment.show(
                                requireActivity().supportFragmentManager,
                                "NewStintFragment"
                            )
                        }
                        builder.setNeutralButton(
                            hu.bme.aut.android.enduranceoagb.R.string.button_megse,
                            null
                        )
                        builder.show()
                    } else {
                        val fragment = NewStintFragment.newInstance(
                            position.toString(),
                            stintId.toString(),
                            teamName,
                            teamNumber.toString(),
                            stintDone.toString(),
                            driverName,
                            plusWeight.toString(),
                            shortTeamName,
                            driverWeight.toString(),
                            prevTotalWeight.toString(),
                            secondGroupFirstx.toString(),
                            "false"
                        )
                        fragment.show(requireActivity().supportFragmentManager, "NewStintFragment")
                    }
                }
                else {
                    if (p0.result.child("Stints").child(change).child("Info").child(teamStint)
                            .child("driverName").exists()
                    ) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Figyelem!")
                        builder.setMessage("Ezt az etapot már nem tudod módosítani!")

                        builder.setPositiveButton(
                            hu.bme.aut.android.enduranceoagb.R.string.button_ok,
                            null
                        )
                        builder.show()
                    } else if (stintDone) {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Figyelem!")
                        builder.setMessage("Ezt az etapot egyszer már létrehoztad. Biztos, hogy módosítani szeretnéd?")

                        builder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.yes) { dialog, which ->
                            val fragment = NewStintFragment.newInstance(
                                position.toString(),
                                stintId.toString(),
                                teamName,
                                teamNumber.toString(),
                                stintDone.toString(),
                                driverName,
                                plusWeight.toString(),
                                shortTeamName,
                                driverWeight.toString(),
                                prevTotalWeight.toString(),
                                numberOfTeams.toString(),
                                "true"
                            )
                            fragment.show(
                                requireActivity().supportFragmentManager,
                                "NewStintFragment"
                            )
                        }
                        builder.setNeutralButton(
                            hu.bme.aut.android.enduranceoagb.R.string.button_megse,
                            null
                        )
                        builder.show()
                    } else {
                        val fragment = NewStintFragment.newInstance(
                            position.toString(),
                            stintId.toString(),
                            teamName,
                            teamNumber.toString(),
                            stintDone.toString(),
                            driverName,
                            plusWeight.toString(),
                            shortTeamName,
                            driverWeight.toString(),
                            prevTotalWeight.toString(),
                            numberOfTeams.toString(),
                            "true"
                        )
                        fragment.show(requireActivity().supportFragmentManager, "NewStintFragment")
                    }
                }
            }


        }
    }

    override fun onTeamListener(position: String?, number: String?, gp2: Boolean?) {
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this.requireContext(), DetailsTeamCheckActivity::class.java)
        showDetailsIntent.putExtra(DriverActivity.EXTRA_RACE_NAME, raceId())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_NAMETEAM, position.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_TEAM_NUMBER, number.toString())
        showDetailsIntent.putExtra(DriverActivity.EXTRA_GP2, gp2.toString())
        startActivity(showDetailsIntent)
    }

    override fun dataChanged(position: Int, initTime: Double) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                dbRef.child("BoxTime").child(stintId).child((position+1).toString()).child("initialTime").setValue(initTime)
            }
        }
    }

    override fun dataChangedBool(position: Int) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                dbRef.child("BoxTime").child(stintId).child((position+1).toString()).child("hasDone").setValue(true)
            }
        }
    }

    override fun onNewBoxListener(
        position: Int,
        teamNumber: Int,
        stintDone: Boolean,
        nameTeam: String
    ) {
        /*val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val change = "Etap: ${stintId.toString().toInt()+1}"
                val teamStint = "${stintId.toString().toInt()+1}-$teamNumber"
                if (p0.result.child("Stints").child(change).child("Info").child(teamStint).child("driverName").exists()) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Ezt az etapot már nem tudod módosítani!")

                    builder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok, null)
                    builder.show()
                }
                else if (stintDone) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Ezt az etapot egyszer már létrehoztad. Biztos, hogy módosítani szeretnéd?")

                    builder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.yes) { dialog, which ->
                        val fragment = NewBoxFragment.newInstance(raceId, position.toString(), stintId.toString(), nameTeam, teamNumber.toString(), stintDone.toString(), activity.toString())
                        fragment.show(requireActivity().supportFragmentManager, "NewBoxFragment")
                    }
                    builder.setNeutralButton(hu.bme.aut.android.enduranceoagb.R.string.button_megse, null)
                    builder.show()
                }
                else {
                    val fragment = NewBoxFragment.newInstance(
                        raceId, position.toString(), stintId.toString(), nameTeam, teamNumber.toString(), stintDone.toString(), activity.toString()
                    )
                    fragment.show(requireActivity().supportFragmentManager, "NewBoxFragment")
                }
            }


        }*/
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStintCreated(
        teamName: String,
        teamNumber: Int,
        driver: String,
        driverWeight: Double?,
        totalWeight: Double?,
        stintNumber: Int,
        shortTeamName: String?,
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

        //val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId = raceIdpass.toString()
        val stintId = stintIdpass.toString()
        //val activityStint = activity as DetailsStintWatchActivity?

        val items : MutableList<Stint>? = mutableListOf()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val driverWeightReal = p0.result.child("Teams").child(teamName).child("Drivers").child(driver).child("weight").value.toString().toDoubleOrNull()
                //var change = "Etap: $stintId"
                //val allStintNumberAll = p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                if (stintId.toString().toInt() == 1) {
                    //val firstTotalWeight = driverWeightReal?.plus(weight)
                    val stint = Stint(
                        teamName,
                        teamNumber,
                        driver,
                        stintId.toString().toInt(),
                        shortTeamName,
                        weight,
                        info,
                        null,
                        true, null, driverWeightReal,
                        kartNumber,
                        expectedKartNumber
                    )
                    val teamStint = "$stintId-$teamNumber"
                    val change = "Etap: $stintId"
                    dbRef.child("Stints").child(change).child("Info").child(teamStint)
                        .setValue(stint)
                    val oriWeight =
                        p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                            .child("weight").value.toString().toDouble()
                    val sum = oriWeight + weight
                    dbRef.child("Teams").child(teamName).child("Info").child("avgWeight")
                        .setValue(sum)
                    dbRef.child("Teams").child(teamName).child("Info").child("stintsDone")
                        .setValue(stintNumber)


                    stintDoneCheck(raceIdpass, stintIdpass)

                    val numberOfTeams =
                        p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                    val secondGroupFirst =
                        p0.result.child("Info").child("secondGroup").value.toString().toIntOrNull()
                    val firstGroupLast = numberOfTeams - (secondGroupFirst?.minus(1)!!)
                    //val allTeamTogether = p0.result.child("Info").child("allTeamTogether").value.toString().toBooleanStrictOrNull()
                    val firstMore = p0.result.child("Info").child("firstMore").value.toString().toBooleanStrictOrNull()
                    val secondMore = p0.result.child("Info").child("secondMore").value.toString().toBooleanStrictOrNull()
                    val equalGroup = p0.result.child("Info").child("equalGroup").value.toString().toBooleanStrictOrNull()

                    val allStintNumber =
                        p0.result.child("Info").child("allStintNumber").value.toString().toInt()

                    if ((stintId.toString().toInt() + 1) <= allStintNumber) {
                        //println("ide????1?")
                        if (firstMore == true) {
                            if (numberOfTeams < 12) {
                                if (teamNumber == numberOfTeams) {
                                    var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                    val teamStintNext11 = "${stintId.toString().toInt() + 1}-box11"
                                    val teamStintNext12 = "${stintId.toString().toInt() + 1}-box12"
                                    val teamStintNext21 = "${stintId.toString().toInt() + 1}-box21"
                                    val teamStintNext22 = "${stintId.toString().toInt() + 1}-box22"
                                    dbRef.child("Stints").child(changeNext).child("Info")
                                        .child(teamStintNext11).child("kartNumber").setValue(kartNumber)
                                    dbRef.child("Stints").child(changeNext).child("Info")
                                        .child(teamStintNext12).child("kartNumber").setValue(kartNumber)
                                    dbRef.child("Stints").child(changeNext).child("Info")
                                        .child(teamStintNext21).child("kartNumber").setValue(kartNumber)
                                    dbRef.child("Stints").child(changeNext).child("Info")
                                        .child(teamStintNext22).child("kartNumber").setValue(kartNumber)
                                }
                            }
                            else {
                                if (teamNumber == numberOfTeams) {
                                    var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                    val teamStintNext11 = "${stintId.toString().toInt() + 1}-box11"
                                    val teamStintNext12 = "${stintId.toString().toInt() + 1}-box12"
                                    dbRef.child("Stints").child(changeNext).child("Info")
                                        .child(teamStintNext11).child("kartNumber").setValue(kartNumber)
                                    dbRef.child("Stints").child(changeNext).child("Info")
                                        .child(teamStintNext12).child("kartNumber").setValue(kartNumber)
                                }
                                if (teamNumber == firstGroupLast) {
                                    var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                    var changeNext2 = "Etap: ${stintId.toString().toInt() + 2}"
                                    val teamStintNext21 = "${stintId.toString().toInt() + 1}-box21"
                                    val teamStintNext22 = "${stintId.toString().toInt() + 1}-box22"
                                    val teamStintNext221 = "${stintId.toString().toInt() + 2}-box22"
                                    dbRef.child("Stints").child(changeNext).child("Info")
                                        .child(teamStintNext21).child("kartNumber").setValue(kartNumber)
                                    val prevBoxKart = p0.result.child("Stints").child(change).child("Info")
                                        .child(teamStintNext21).child("kartNumber").value.toString().toIntOrNull()
                                    dbRef.child("Stints").child(changeNext).child("Info")
                                        .child(teamStintNext22).child("kartNumber").setValue(prevBoxKart)
                                    dbRef.child("Stints").child(changeNext2).child("Info")
                                        .child(teamStintNext221).child("kartNumber").setValue(kartNumber)
                                }
                            }
                        }
                        else if (secondMore == true) {
                            //println("ide2?")
                            if (teamNumber == numberOfTeams) {
                                //println("ide3?")
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                var changeNext2 = "Etap: ${stintId.toString().toInt() + 2}"
                                val teamStintNext11 = "${stintId.toString().toInt() + 1}-box11"
                                val teamStintNext122 = "${stintId.toString().toInt() + 1}-box12"
                                val teamStintNext12 = "${stintId.toString().toInt() + 2}-box12"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext11).child("kartNumber").setValue(kartNumber)
                                val prevBoxKart = p0.result.child("Stints").child(change).child("Info")
                                    .child(teamStintNext11).child("kartNumber").value.toString().toIntOrNull()
                                dbRef.child("Stints").child(changeNext2).child("Info")
                                    .child(teamStintNext12).child("kartNumber").setValue(kartNumber)
                            }
                            if (teamNumber == secondGroupFirst) {
                                //println("ide4?")
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                val teamStintNext21 = "${stintId.toString().toInt() + 1}-box21"
                                val teamStintNext22 = "${stintId.toString().toInt() + 1}-box22"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext21).child("kartNumber").setValue(kartNumber)
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext22).child("kartNumber").setValue(kartNumber)
                            }
                        }
                        else if (equalGroup == true) {
                            if (teamNumber == firstGroupLast) {
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                val teamStintNext21 = "${stintId.toString().toInt() + 1}-box21"
                                val teamStintNext22 = "${stintId.toString().toInt() + 1}-box22"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext21).child("kartNumber").setValue(kartNumber)
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext22).child("kartNumber").setValue(kartNumber)
                            }
                            if (teamNumber == numberOfTeams) {
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                val teamStintNext11 = "${stintId.toString().toInt() + 1}-box11"
                                val teamStintNext12 = "${stintId.toString().toInt() + 1}-box12"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext11).child("kartNumber").setValue(kartNumber)
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext12).child("kartNumber").setValue(kartNumber)
                            }
                        }
                        else {
                            //println("ide1111?")
                            if (teamNumber == numberOfTeams) {
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                val teamStintNext11 = "${stintId.toString().toInt() + 1}-box11"
                                val teamStintNext12 = "${stintId.toString().toInt() + 1}-box12"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext11).child("kartNumber").setValue(kartNumber)
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext12).child("kartNumber").setValue(kartNumber)
                            }
                        }
                    }

                    /*requireActivity().runOnUiThread {
                        adapter.addItemStints(stint)
                    }*/
                }
                else if (stintId.toString().toInt() > 1) {
                    val teamStint = "$stintId-$teamNumber"
                    val change = "Etap: $stintId"
                    val changePrevStint = "Etap: " + (stintId.toString().toInt() - 1).toString()
                    val changePrev = (stintId.toString().toInt() - 1).toString() + "-" + teamNumber
                    val prevInfo = p0.result.child("Stints").child(changePrevStint).child("Info")
                        .child(changePrev).child("info").value.toString()
                    var prevAvgWeight = p0.result.child("Stints").child(change).child("Info")
                        .child(teamStint).child("prevAvgWeight").value.toString().toDoubleOrNull()
                    if (prevAvgWeight == null || prevAvgWeight.toString() == "null") {
                        prevAvgWeight =
                            p0.result.child("Teams").child(teamName).child("Info").child("avgWeight").value.toString().toDoubleOrNull()
                    }
                    val nextStint = stintId.toInt()+1
                    val isNextStintReady = p0.result.child("AllStint").child("numberOfStint").child(nextStint.toString()).child("hasDetailsStintReady").value.toString()
                    if (isNextStintReady == "true") {
                        val nextAvgWeight = p0.result.child("Stints").child(nextStint.toString()).child("Info")
                            .child(teamStint).child("prevAvgWeight").value.toString().toDoubleOrNull()
                        val prevDriverWeight = p0.result.child("Teams").child(teamName).child("Drivers").child(driverName.toString()).child("weight").value.toString().toDoubleOrNull()
                        val prevWeight = plusWeightDriver?.toDoubleOrNull()
                            ?.let { prevDriverWeight?.plus(it) }
                        val newWeight = prevWeight?.let { nextAvgWeight?.minus(it) }
                        val newDriverWeight = driverWeightReal?.plus(weight)
                        val newTotalWeight = newWeight?.let { newDriverWeight?.plus(it) }
                        dbRef.child("Stints").child(nextStint.toString()).child("Info").child(teamStint).child("prevAvgWeight")
                            .setValue(newTotalWeight)
                    }

                    val stint = Stint(
                        teamName,
                        teamNumber,
                        driver,
                        stintId.toString().toInt(),
                        shortTeamName,
                        weight,
                        info,
                        prevInfo,
                        true, prevAvgWeight, driverWeightReal,
                        kartNumber,
                        expectedKartNumber
                    )

                    if (stintDonePrev == "false") {
                        val change = "Etap: $stintId"
                        dbRef.child("Stints").child(change).child("Info").child(teamStint)
                            .setValue(stint)
                        val getWeight = p0.result.child("Teams").child(teamName).child("Info")
                            .child("avgWeight").value.toString().toDouble()
                        val oriWeight =
                            p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                                .child("weight").value.toString().toDouble()
                        val sum = oriWeight + weight + getWeight
                        dbRef.child("Teams").child(teamName).child("Info").child("avgWeight")
                            .setValue(sum)
                        dbRef.child("Teams").child(teamName).child("Info").child("stintsDone")
                            .setValue(stintNumber)

                        stintDoneCheck(raceIdpass, stintIdpass)
                    } else if (stintDonePrev == "true") {
                        val change = "Etap: $stintId"
                        val getWeight = p0.result.child("Teams").child(teamName).child("Info")
                            .child("avgWeight").value.toString().toDouble()

                        val prevDriverWeight =
                            p0.result.child("Teams").child(teamName).child("Drivers")
                                .child(driverName.toString()).child("weight").value.toString()
                                .toDouble()
                        val prevPlus =
                            p0.result.child("Stints").child(change).child("Info").child(teamStint)
                                .child("plusWeight").value.toString().toDouble()
                        val prevAll = prevDriverWeight + prevPlus
                        val allWeight = getWeight - prevAll
                        val oriWeight =
                            p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                                .child("weight").value.toString().toDouble()
                        val sum = oriWeight + weight + allWeight

                        dbRef.child("Teams").child(teamName).child("Info").child("avgWeight")
                            .setValue(sum)
                        dbRef.child("Stints").child(change).child("Info").child(teamStint)
                            .setValue(stint)
                        dbRef.child("Teams").child(teamName).child("Info").child("stintsDone")
                            .setValue(stintNumber)
                    }

                    val numberOfTeams =
                        p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                    val secondGroupFirst = p0.result.child("Info").child("secondGroup").value.toString().toIntOrNull()
                    val firstGroupLast = secondGroupFirst
                    val firstGroupLastReal = secondGroupFirst?.minus(1)
                    val firstMore = p0.result.child("Info").child("firstMore").value.toString().toBooleanStrictOrNull()
                    val secondMore = p0.result.child("Info").child("secondMore").value.toString().toBooleanStrictOrNull()
                    val equalGroup = p0.result.child("Info").child("equalGroup").value.toString().toBooleanStrictOrNull()

                    val allStintNumber =
                        p0.result.child("Info").child("allStintNumber").value.toString().toInt()

                    if ((stintId.toString().toInt() + 1) <= allStintNumber) {
                        if (firstMore == true) {
                            if (teamNumber == numberOfTeams) {
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                val teamStintNext11 = "${stintId.toString().toInt() + 1}-box11"
                                val teamStintNext12 = "${stintId.toString().toInt() + 1}-box12"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext11).child("kartNumber").setValue(kartNumber)
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext12).child("kartNumber").setValue(kartNumber)
                            }
                            if (teamNumber == secondGroupFirst) {
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                var changeNext2 = "Etap: ${stintId.toString().toInt() + 2}"
                                val teamStintNext21 = "${stintId.toString().toInt() + 1}-box21"
                                val teamStintNext211 = "${stintId.toString().toInt()}-box21"
                                val teamStintNext22 = "${stintId.toString().toInt() + 1}-box22"
                                val teamStintNext221 = "${stintId.toString().toInt() + 2}-box22"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext21).child("kartNumber").setValue(kartNumber)
                                val prevBoxKart = p0.result.child("Stints").child(change).child("Info")
                                    .child(teamStintNext211).child("kartNumber").value.toString().toIntOrNull()
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext22).child("kartNumber").setValue(prevBoxKart)
                                dbRef.child("Stints").child(changeNext2).child("Info")
                                    .child(teamStintNext221).child("kartNumber").setValue(kartNumber)
                            }
                        }
                        else if (secondMore == true) {
                            if (teamNumber == numberOfTeams) {
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                var changeNext2 = "Etap: ${stintId.toString().toInt() + 2}"
                                val teamStintNext11 = "${stintId.toString().toInt() + 1}-box11"
                                val teamStintNext111 = "${stintId.toString().toInt()}-box11"
                                val teamStintNext12 = "${stintId.toString().toInt() + 1}-box12"
                                val teamStintNext121 = "${stintId.toString().toInt() + 2}-box12"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext11).child("kartNumber").setValue(kartNumber)
                                val prevBoxKart = p0.result.child("Stints").child(change).child("Info")
                                    .child(teamStintNext111).child("kartNumber").value.toString().toIntOrNull()
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext12).child("kartNumber").setValue(prevBoxKart)
                                dbRef.child("Stints").child(changeNext2).child("Info")
                                    .child(teamStintNext121).child("kartNumber").setValue(kartNumber)
                            }
                            if (teamNumber == secondGroupFirst) {
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                val teamStintNext21 = "${stintId.toString().toInt() + 1}-box21"
                                val teamStintNext22 = "${stintId.toString().toInt() + 1}-box22"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext21).child("kartNumber").setValue(kartNumber)
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext22).child("kartNumber").setValue(kartNumber)
                            }
                        }
                        else if (equalGroup == true) {
                            if (teamNumber == firstGroupLastReal) {
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                val teamStintNext21 = "${stintId.toString().toInt() + 1}-box21"
                                val teamStintNext22 = "${stintId.toString().toInt() + 1}-box22"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext21).child("kartNumber").setValue(kartNumber)
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext22).child("kartNumber").setValue(kartNumber)
                            }
                            if (teamNumber == numberOfTeams) {
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                val teamStintNext11 = "${stintId.toString().toInt() + 1}-box11"
                                val teamStintNext12 = "${stintId.toString().toInt() + 1}-box12"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext11).child("kartNumber").setValue(kartNumber)
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext12).child("kartNumber").setValue(kartNumber)
                            }
                        }
                        else {
                            if (teamNumber == numberOfTeams) {
                                var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                                val teamStintNext11 = "${stintId.toString().toInt() + 1}-box11"
                                val teamStintNext12 = "${stintId.toString().toInt() + 1}-box12"
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext11).child("kartNumber").setValue(kartNumber)
                                dbRef.child("Stints").child(changeNext).child("Info")
                                    .child(teamStintNext12).child("kartNumber").setValue(kartNumber)
                            }
                        }
                    }

                    /*requireActivity().runOnUiThread {
                        adapter.addItemStints(stint)
                    }*/

                }

                if (stintId.toString().toInt() in 1..9) {
                    val getStintDone = p0.result.child("Stints").child("Etap: $stintId").child("Info").child("$stintId-$teamNumber").child("hasStintDone").value.toString().toBoolean()
                    if (getStintDone) {
                        val getId = p0.result.child("Id").value.toString()
                        for (i in 1..getId.toInt()) {
                            val getStintNumber = p0.result.child("Excel").child(i.toString()).child("stintNumber").value.toString()
                            val getTeamNumber = p0.result.child("Excel").child(i.toString()).child("teamNumber").value.toString()
                            val stintIdString = "$stintId. etap"
                            val teamNumberString = "$teamNumber. csapat"
                            if (getStintNumber == stintIdString && getTeamNumber == teamNumberString) {
                                dbRef.child("Excel").child(i.toString()).child("driver").setValue(driver)
                                if (weight == 0.0) {
                                    dbRef.child("Excel").child(i.toString()).child("plusWeight").setValue("-")
                                } else {
                                    val weightString = weight.toString()
                                    val output = weightString.replace('.', ',')
                                    dbRef.child("Excel").child(i.toString()).child("plusWeight").setValue(output)
                                }
                                val originalWeight =
                                    p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                                        .child("weight").value.toString().toDouble()
                                val sumWeight = originalWeight + weight
                                val sumWeightStr = sumWeight.toString()
                                val outputSumWeight = sumWeightStr.replace('.', ',')
                                dbRef.child("Excel").child(i.toString()).child("totalWeight").setValue(outputSumWeight)
                                dbRef.child("Excel").child(i.toString()).child("kartNumber").setValue(kartNumber)
                                if (driver != driverName) {
                                    val prevDriverStints = p0.result.child("Teams")
                                        .child(teamName).child("Drivers").child(driverName.toString()).child("stints").value.toString().toInt()
                                    dbRef.child("Teams").child(teamName).child("Drivers").child(driverName.toString()).child("stints").setValue(prevDriverStints - 1)
                                    val driverStints = p0.result.child("Teams")
                                        .child(teamName).child("Drivers").child(driver).child("stints").value.toString().toInt()
                                    dbRef.child("Teams").child(teamName).child("Drivers").child(driver).child("stints").setValue(driverStints + 1)
                                }
                                break
                            }
                        }
                    }
                    else {
                        val change = "Etap: 0$stintId"
                        val id = p0.result.child("Id").value.toString()
                        var idNumber: Int
                        if (id == "-1") {
                            idNumber = 0
                        } else {
                            idNumber = id.toInt()
                            idNumber++
                        }
                        dbRef.child("Id").setValue(idNumber)
                        val teamStint = idNumber
                        val stintPass = "$stintId. etap"
                        val teamPass = "$teamNumber. csapat"
                        dbRef.child("Excel").child(teamStint.toString()).child("stintNumber")
                            .setValue(stintPass)
                        dbRef.child("Excel").child(teamStint.toString()).child("teamNumber")
                            .setValue(teamPass)
                        dbRef.child("Cserék").child(change).child("1 - Versenyzők")
                            .child(teamNumber.toString()).setValue(driver)
                        dbRef.child("Excel").child(teamStint.toString()).child("driver")
                            .setValue(driver)
                        if (weight == 0.0) {
                            dbRef.child("Cserék").child(change).child("4 - Plusz súlyok")
                                .child(teamNumber.toString()).setValue("-")
                            dbRef.child("Excel").child(teamStint.toString()).child("plusWeight")
                                .setValue("-")
                        } else {
                            val weightString = weight.toString()
                            val output = weightString.replace('.', ',')
                            dbRef.child("Cserék").child(change).child("4 - Plusz súlyok")
                                .child(teamNumber.toString()).setValue(output)
                            dbRef.child("Excel").child(teamStint.toString()).child("plusWeight")
                                .setValue(output)
                        }
                        val originalWeight =
                            p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                                .child("weight").value.toString().toDouble()
                        val sumWeight = originalWeight + weight
                        val sumWeightStr = sumWeight.toString()
                        val outputSumWeight = sumWeightStr.replace('.', ',')
                        dbRef.child("Cserék").child(change).child("2 - Összsúly")
                            .child(teamNumber.toString()).setValue(outputSumWeight)
                        dbRef.child("Cserék").child(change).child("3 - Gépszámok")
                            .child(teamNumber.toString()).setValue(kartNumber)

                        dbRef.child("Excel").child(teamStint.toString()).child("totalWeight")
                            .setValue(outputSumWeight)
                        dbRef.child("Excel").child(teamStint.toString()).child("kartNumber")
                            .setValue(kartNumber)
                        val driverStints = p0.result.child("Teams")
                            .child(teamName).child("Drivers").child(driver).child("stints").value.toString().toInt()
                        dbRef.child("Teams").child(teamName).child("Drivers").child(driver).child("stints").setValue(driverStints + 1)

                    }
                }
                else {
                    val getStintDone = p0.result.child("Stints").child("Etap: $stintId").child("Info").child("$stintId-$teamNumber").child("hasStintDone").value.toString().toBoolean()
                    if (getStintDone) {
                        val getId = p0.result.child("Id").value.toString()
                        for (i in 1..getId.toInt()) {
                            val getStintNumber = p0.result.child("Excel").child(i.toString()).child("stintNumber").value.toString()
                            val getTeamNumber = p0.result.child("Excel").child(i.toString()).child("teamNumber").value.toString()
                            val stintIdString = "$stintId. etap"
                            val teamNumberString = "$teamNumber. csapat"
                            if (getStintNumber == stintIdString && getTeamNumber == teamNumberString) {
                                dbRef.child("Excel").child(i.toString()).child("driver").setValue(driver)
                                if (weight == 0.0) {
                                    dbRef.child("Excel").child(i.toString()).child("plusWeight").setValue("-")
                                } else {
                                    val weightString = weight.toString()
                                    val output = weightString.replace('.', ',')
                                    dbRef.child("Excel").child(i.toString()).child("plusWeight").setValue(output)
                                }
                                val originalWeight =
                                    p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                                        .child("weight").value.toString().toDouble()
                                val sumWeight = originalWeight + weight
                                val sumWeightStr = sumWeight.toString()
                                val outputSumWeight = sumWeightStr.replace('.', ',')
                                dbRef.child("Excel").child(i.toString()).child("totalWeight").setValue(outputSumWeight)
                                dbRef.child("Excel").child(i.toString()).child("kartNumber").setValue(kartNumber)
                                if (driver != driverName) {
                                    val prevDriverStints = p0.result.child("Teams")
                                        .child(teamName).child("Drivers").child(driverName.toString()).child("stints").value.toString().toInt()
                                    dbRef.child("Teams").child(teamName).child("Drivers").child(driverName.toString()).child("stints").setValue(prevDriverStints - 1)
                                    val driverStints = p0.result.child("Teams")
                                        .child(teamName).child("Drivers").child(driver).child("stints").value.toString().toInt()
                                    dbRef.child("Teams").child(teamName).child("Drivers").child(driver).child("stints").setValue(driverStints + 1)
                                }
                                break
                            }
                        }
                    }
                    else {
                        val changeUp = "Etap: $stintId"
                        val id = p0.result.child("Id").value.toString()
                        var idNumber: Int
                        if (id == "-1") {
                            idNumber = 0
                        } else {
                            idNumber = id.toInt()
                            idNumber++
                        }
                        dbRef.child("Id").setValue(idNumber)
                        val teamStint = idNumber
                        val stintPass = "$stintId. etap"
                        val teamPass = "$teamNumber. csapat"
                        dbRef.child("Excel").child(teamStint.toString()).child("stintNumber")
                            .setValue(stintPass)
                        dbRef.child("Excel").child(teamStint.toString()).child("teamNumber")
                            .setValue(teamPass)
                        dbRef.child("Cserék").child(changeUp).child("1 - Versenyzők")
                            .child(teamNumber.toString()).setValue(driver)
                        dbRef.child("Excel").child(teamStint.toString()).child("driver")
                            .setValue(driver)
                        if (weight == 0.0) {
                            dbRef.child("Cserék").child(changeUp).child("4 - Plusz súlyok")
                                .child(teamNumber.toString()).setValue("-")
                            dbRef.child("Excel").child(teamStint.toString()).child("plusWeight")
                                .setValue("-")
                        } else {
                            val weightString = weight.toString()
                            val output = weightString.replace('.', ',')
                            dbRef.child("Cserék").child(changeUp).child("4 - Plusz súlyok")
                                .child(teamNumber.toString()).setValue(output)
                            dbRef.child("Excel").child(teamStint.toString()).child("plusWeight")
                                .setValue(output)
                        }
                        val originalWeight =
                            p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                                .child("weight").value.toString().toDouble()
                        val sumWeight = originalWeight + weight
                        val sumWeightStr = sumWeight.toString()
                        val outputSumWeight = sumWeightStr.replace('.', ',')
                        dbRef.child("Cserék").child(changeUp).child("2 - Összsúly")
                            .child(teamNumber.toString()).setValue(outputSumWeight)
                        dbRef.child("Cserék").child(changeUp).child("3 - Gépszámok")
                            .child(teamNumber.toString()).setValue(kartNumber)

                        dbRef.child("Excel").child(teamStint.toString()).child("totalWeight")
                            .setValue(outputSumWeight)
                        dbRef.child("Excel").child(teamStint.toString()).child("kartNumber")
                            .setValue(kartNumber)

                        val driverStints = p0.result.child("Teams")
                            .child(teamName).child("Drivers").child(driver).child("stints").value.toString().toInt()
                        dbRef.child("Teams").child(teamName).child("Drivers").child(driver).child("stints").setValue(driverStints + 1)

                    }
                }

            }
        }
    }

    private fun stintDoneCheck(raceIdpass: String?, stintIdpass: String?) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId = raceIdpass.toString()
        val stintId = stintIdpass.toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val change = "Etap: $stintId"

                val numberOfTeams =
                    p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                var done = 0
                for (i in 1..numberOfTeams) {
                    val stint = "$stintId-$i"
                    val hasStintDone =
                        p0.result.child("Stints").child(change).child("Info").child(stint)
                            .child("hasStintDone").value.toString().toBoolean()
                    if (hasStintDone) {
                        done += 1
                    } else if (!hasStintDone) {
                        //Ez nem
                    }
                }
                if (done == numberOfTeams) {
                    dbRef.child("AllStint").child("numberOfStint").child(stintId.toString())
                        .child("hasStintDone").setValue(true)
                    val numberOfStints =
                        p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                    if (stintId.toString().toInt() == numberOfStints) {
                        dbRef.child("Info").child("hasRaceDone").setValue(true)
                    }

                    val petrolDone = p0.result.child("Info").child("petrolDone").value.toString().toBoolean()
                    if(!petrolDone) {
                        val allStint = p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                        var stintPetrol = 0
                        for (element in 1..allStint) {
                            val hasStintDone = p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("hasStintDone").value.toString().toBoolean()

                            if (hasStintDone) {
                                stintPetrol += 1
                            }
                        }
                        if ((allStint.toDouble() / 2.0) == (stintPetrol.toDouble() + 1.0) || (allStint.toDouble() / 2.0) == (stintPetrol + 0.5)) {
                            sendNotification()
                            dbRef.child("Info").child("petrolDone").setValue(true)
                        }
                    }

                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Endurance OAGB"
            val descriptonText = "A következő cserénél tankolni kell!"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptonText
            }
            val notificationManager: NotificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification() {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(hu.bme.aut.android.enduranceoagb.R.mipmap.endu_logo_round)
            .setContentTitle("Endurance OAGB")
            .setContentText("A következő cserénél tankolni kell!")
            .setAutoCancel(true)
            .setSound(soundUri)
        with(NotificationManagerCompat.from(requireContext())) {
            notify(notificationId, notificationBuilder.build())
        }
    }

    override fun onStintNotCreated() {
        val snack = Snackbar.make(binding.root, hu.bme.aut.android.enduranceoagb.R.string.notAddDriver, Snackbar.LENGTH_LONG)
        snack.show()
    }

    /*override fun onBoxCreated(
        raceIdBox: String,
        teamName: String,
        teamNumber: Int,
        time: Double,
        stint: Int,
        activity: String
    ) {
        val items : MutableList<BoxTime>? = mutableListOf()

        val nextStint = stint + 1

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceIdBox)

        //dbRef.child("BoxTime").child(stint.toString()).child(teamNumber.toString()).child("hasDone").setValue(true)
        dbRef.child("BoxTime").child(stint.toString()).child(teamNumber.toString()).child("actualTime").setValue(time)
        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val initTime = p0.result.child("BoxTime").child(stint.toString()).child(teamNumber.toString()).child("initialTime").value.toString().toDouble()
                val difference = initTime - time
                val defaultTime = p0.result.child("Info").child("changeTime").value.toString().toDouble()
                if (difference > 0) {
                    val penalty = 10000.0 + (difference * 2)
                    dbRef.child("BoxTime").child(stint.toString()).child(teamNumber.toString()).child("penaltyTime").setValue(penalty)
                    val nextTime = defaultTime + penalty
                    dbRef.child("BoxTime").child(stint.toString()).child(teamNumber.toString()).child("nextTime").setValue(nextTime)


                    dbRef.child("BoxTime").child(nextStint.toString()).child(teamNumber.toString()).child("initialTime").setValue(nextTime)
                    dbRef.child("BoxTime").child(nextStint.toString()).child(teamNumber.toString()).child("prevPenaltyTime").setValue(penalty)
                }
                else {
                    dbRef.child("BoxTime").child(stint.toString()).child(teamNumber.toString()).child("penaltyTime").setValue(0.0)
                    dbRef.child("BoxTime").child(stint.toString()).child(teamNumber.toString()).child("nextTime").setValue(defaultTime)

                    dbRef.child("BoxTime").child(nextStint.toString()).child(teamNumber.toString()).child("initialTime").setValue(defaultTime)
                    dbRef.child("BoxTime").child(nextStint.toString()).child(teamNumber.toString()).child("prevPenaltyTime").setValue(0.0)
                }
                dbRef.child("BoxTime").child(stint.toString()).child(teamNumber.toString()).child("hasDone").setValue(true)

                dbRef.child("BoxTime").child(nextStint.toString()).child(teamNumber.toString()).child("hasDone").setValue(false)
                dbRef.child("BoxTime").child(nextStint.toString()).child(teamNumber.toString()).child("teamNumber").setValue(teamNumber)
            }
        }
        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val numberOfTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                if (!p0.result.child("BoxTime").child(stint.toString()).exists()) {
                    val changeTime = p0.result.child("Info").child("changeTime").value.toString().toInt()
                    for (element in 1..numberOfTeams) {
                        val boxTime = BoxTime(element, changeTime.toDouble(), false)
                        dbRef.child("BoxTime").child(stint.toString()).child(element.toString()).setValue(boxTime)
                        items?.add(boxTime)
                    }
                }
                requireActivity().runOnUiThread {
                    adapter.update2Box(items!!)
                }
                context?.let {
                    val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
                    fragmentManager?.let {
                        val currentFragment = fragmentManager.findFragmentById(R.id.boxTimeData)
                        currentFragment?.let {
                            val fragmentTransaction = fragmentManager.beginTransaction()
                            fragmentTransaction.detach(it)
                            fragmentTransaction.attach(it)
                            fragmentTransaction.commit()
                        }
                    }
                }
                onStart()
            }
        }
    }

    override fun onBoxNotCreated() {
        val snack = Snackbar.make(ActivityBoxtimeBinding.inflate(layoutInflater).root, hu.bme.aut.android.enduranceoagb.R.string.notAddDriver, Snackbar.LENGTH_LONG)
        snack.show()
    }*/

    override fun raceId(): String? {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        return activity?.getMyData().toString()
    }
}