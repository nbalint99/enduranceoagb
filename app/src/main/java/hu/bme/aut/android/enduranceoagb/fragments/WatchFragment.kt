package hu.bme.aut.android.enduranceoagb.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import hu.bme.aut.android.enduranceoagb.DetailsStintWatchActivity
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.adapter.WatchAdapter2
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.data.Watch
import hu.bme.aut.android.enduranceoagb.databinding.ActivityWatchBinding

class WatchFragment : Fragment(), WatchAdapter2.Watch2ItemClickListener{

    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRef2: DatabaseReference

    private lateinit var adapter: WatchAdapter2

    private val CHANNEL_ID = "channel_id_01"
    private val notificationId = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ActivityWatchBinding.inflate(layoutInflater)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)

        binding.rvWatch.setLayoutManager(layoutManager)

        adapter = WatchAdapter2(this)

        binding.rvWatch.adapter = adapter

        binding.rvWatch.setItemViewCacheSize(16)

        createNotificationChannel2()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loadItemsInBackground()

    }

    private fun loadItemsInBackground() {
        getData()
    }

    private fun getData() {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val items : MutableList<Watch>? = mutableListOf()
        val itemsTeams : MutableList<Teams>? = mutableListOf()
        val groups : MutableList<Int>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val numberOfTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                val changeTime = p0.result.child("Info").child("changeTime").value.toString().toDouble()
                if (!p0.result.child("Watch").child(stintId).exists()) {
                    for (element in 1..numberOfTeams) {
                        val watch = Watch(element, changeTime, false, changeTime)
                        dbRef.child("Watch").child(stintId).child(element.toString()).setValue(watch)
                        items?.add(watch)
                    }
                }
                else {
                    for (element in 1..numberOfTeams) {
                        val watchGet = Watch(p0.result.child("Watch").child(stintId).child(element.toString()).child("teamNumber").value.toString().toInt(),
                            p0.result.child("Watch").child(stintId).child(element.toString()).child("time").value.toString().toDouble(), p0.result.child("Watch").child(stintId).child(element.toString()).child("hasDone").value.toString().toBoolean(),
                            p0.result.child("Watch").child(stintId).child(element.toString()).child("initialTime").value.toString().toDouble())
                        items?.add(watchGet)
                    }
                }

                val teams = p0.result.child("Teams").children
                for (el in teams) {
                    val teamsGet = Teams(el.child("Info").child("nameTeam").value.toString(), el.child("Info").child("people").value.toString().toInt(),
                                        el.child("Info").child("teamNumber").value.toString().toInt(), el.child("Info").child("avgWeight").value.toString().toDouble(),
                                        el.child("Info").child("hasDriversDone").value.toString().toInt(), el.child("Info").child("startKartNumber").value.toString().toInt(),
                                        el.child("Info").child("hasQualiDone").value.toString().toBoolean(), el.child("Info").child("stintsDone").value.toString().toIntOrNull(), el.child("Info").child("gp2").value.toString().toBooleanStrictOrNull(),
                        el.child("Info").child("points").value.toString().toIntOrNull(), el.child("Info").child("shortTeamName").value.toString(), el.child("Info").child("group").value.toString().toIntOrNull())
                    itemsTeams?.add(teamsGet)
                }

                val secondGroupFirstOri = p0.result.child("Info").child("secondGroup").value.toString().toIntOrNull()
                if (numberOfTeams < 10) {
                    adapter.update2(items!!)
                    adapter.update2Teams(itemsTeams!!)
                }
                else {
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
                    groups?.add(secondGroupFirstx)

                    adapter.update2(items!!)
                    adapter.update2Teams(itemsTeams!!)
                    adapter.update2Group(groups!!)
                }

            }
        }
    }

    override fun dataChanged(position: Int, initTime: Double) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                dbRef.child("Watch").child(stintId).child((position+1).toString()).child("initialTime").setValue(initTime)
            }
        }

        dbRef2 = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("/")

        dbRef2.get().addOnCompleteListener { p1 ->
            if (p1.isSuccessful) {
                val team = position + 1
                dbRef2.child("endTime").child(team.toString()).child("duration").setValue(initTime)
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
                dbRef.child("Watch").child(stintId).child((position+1).toString()).child("hasDone").setValue(true)
            }
        }
    }

    override fun startTimer(position: Int) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val numberOfTeams =
                    p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                val secondGroupFirstOri = p0.result.child("Info").child("secondGroup").value.toString().toIntOrNull()
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

                if (numberOfTeams < 10) {
                    //do nothing
                }
                else {
                    if (position + 1 == secondGroupLast) {
                        sendNotification2()
                    }
                }

            }
        }
    }

    override fun serverTime(position: Int, time: Double, unixTime: Long, counting: Boolean) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("/")

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val team = position + 1

                dbRef.child("endTime").child(team.toString()).child("serverTime").setValue(unixTime)
                dbRef.child("endTime").child(team.toString()).child("duration").setValue(time)
                dbRef.child("endTime").child(team.toString()).child("position").setValue(team)
                dbRef.child("endTime").child(team.toString()).child("counting").setValue(counting)
            }
        }
    }


    override fun dataChangedBoolFalse(position: Int) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val changeTime = p0.result.child("Info").child("changeTime").value.toString().toDouble()
                dbRef.child("Watch").child(stintId).child((position+1).toString()).child("hasDone").setValue(false)
                dbRef.child("Watch").child(stintId).child((position+1).toString()).child("initialTime").setValue(changeTime)
            }
        }
    }

    private fun createNotificationChannel2() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Endurance OAGB"
            val descriptonText = "Jön a melegítős gokart a boxba!"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptonText
            }
            val notificationManager: NotificationManager = requireActivity().getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification2() {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.endu)
            .setContentTitle("Endurance OAGB")
            .setContentText("Jön a melegítős gokart a boxba!")
            .setAutoCancel(true)
            .setSound(soundUri)
        with(NotificationManagerCompat.from(requireContext())) {
            notify(notificationId, notificationBuilder.build())
        }
    }
}

