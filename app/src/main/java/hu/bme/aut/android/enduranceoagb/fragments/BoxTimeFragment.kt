package hu.bme.aut.android.enduranceoagb.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.DetailsStintWatchActivity
import hu.bme.aut.android.enduranceoagb.adapter.WatchAdapter2
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.data.Watch
import hu.bme.aut.android.enduranceoagb.databinding.ActivityWatchBinding

class BoxTimeFragment : Fragment(), WatchAdapter2.Watch2ItemClickListener{

    private lateinit var dbRef: DatabaseReference

    private lateinit var adapter: WatchAdapter2

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

        binding.rvWatch.setItemViewCacheSize(11)

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

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val numberOfTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                if (!p0.result.child("Watch").child(stintId).exists()) {
                    for (element in 1..numberOfTeams) {
                        val watch = Watch(element, 40000.0, false, 40000.0)
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
                        el.child("Info").child("hasQualiDone").value.toString().toBoolean(), el.child("Info").child("stintsDone").value.toString().toIntOrNull(), el.child("Info").child("gp2").value.toString().toBooleanStrictOrNull())
                    itemsTeams?.add(teamsGet)
                }

                adapter.update2(items!!)
                adapter.update2Teams(itemsTeams!!)

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

    override fun dataChangedBoolFalse(position: Int) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                dbRef.child("Watch").child(stintId).child((position+1).toString()).child("hasDone").setValue(false)
                dbRef.child("Watch").child(stintId).child((position+1).toString()).child("initialTime").setValue(40000.0)
            }
        }
    }
}