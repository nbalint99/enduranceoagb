package hu.bme.aut.android.enduranceoagb.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.DetailsStintWatchActivity
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.adapter.BoxTimeAdapter
import hu.bme.aut.android.enduranceoagb.data.BoxTime
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityBoxtimeBinding
import hu.bme.aut.android.enduranceoagb.databinding.ActivityDetailsstintBinding

class BoxTimeFragment : Fragment(), BoxTimeAdapter.BoxTimeItemClickListener, NewBoxFragment.NewBoxListener {

    private lateinit var binding: ActivityBoxtimeBinding
    private lateinit var dbRef: DatabaseReference

    private lateinit var adapter: BoxTimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ActivityBoxtimeBinding.inflate(layoutInflater)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)

        binding.rvBoxtime.setLayoutManager(layoutManager)

        adapter = BoxTimeAdapter(this)

        binding.rvBoxtime.adapter = adapter

        val bundle = arguments
        val message = bundle?.getString("mText")

        val handler = Handler()

        if (message == null) {
            handler.postDelayed(Runnable { layoutManager.smoothScrollToPosition(binding.rvBoxtime, null, 1) }, 100)
        }
        else {
            handler.postDelayed(Runnable { layoutManager.smoothScrollToPosition(binding.rvBoxtime, null, message.toInt()) }, 100)
        }

        binding.rvBoxtime.setItemViewCacheSize(16)

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

        val items : MutableList<BoxTime>? = mutableListOf()
        val itemsTeams : MutableList<Teams>? = mutableListOf()
        val itemsTime : MutableList<Double>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val numberOfTeams = p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                if (!p0.result.child("BoxTime").child(stintId).exists()) {
                    val changeTime = p0.result.child("Info").child("changeTime").value.toString().toInt()
                    for (element in 1..numberOfTeams) {
                        val boxTime = BoxTime(element, changeTime.toDouble(), false)
                        dbRef.child("BoxTime").child(stintId).child(element.toString()).setValue(boxTime)
                        items?.add(boxTime)
                    }
                }
                else {
                    for (element in 1..numberOfTeams) {
                        val watchGet = BoxTime(p0.result.child("BoxTime").child(stintId).child(element.toString()).child("teamNumber").value.toString().toInt(),
                            p0.result.child("BoxTime").child(stintId).child(element.toString()).child("initialTime").value.toString().toDouble(), p0.result.child("BoxTime").child(stintId).child(element.toString()).child("hasDone").value.toString().toBoolean(),
                            p0.result.child("BoxTime").child(stintId).child(element.toString()).child("prevPenaltyTime").value.toString().toDoubleOrNull(),
                            p0.result.child("BoxTime").child(stintId).child(element.toString()).child("actualTime").value.toString().toDoubleOrNull(),
                            p0.result.child("BoxTime").child(stintId).child(element.toString()).child("penaltyTime").value.toString().toDoubleOrNull(),
                            p0.result.child("BoxTime").child(stintId).child(element.toString()).child("nextTime").value.toString().toDoubleOrNull())
                        items?.add(watchGet)
                    }
                }

                val teams = p0.result.child("Teams").children
                for (el in teams) {
                    val teamsGet = Teams(el.child("Info").child("nameTeam").value.toString(), el.child("Info").child("people").value.toString().toInt(),
                        el.child("Info").child("teamNumber").value.toString().toInt(), el.child("Info").child("avgWeight").value.toString().toDouble(),
                        el.child("Info").child("hasDriversDone").value.toString().toInt(), el.child("Info").child("startKartNumber").value.toString().toInt(),
                        el.child("Info").child("hasQualiDone").value.toString().toBoolean(), el.child("Info").child("stintsDone").value.toString().toIntOrNull(), el.child("Info").child("gp2").value.toString().toBooleanStrictOrNull(), el.child("Info").child("shortTeamName").value.toString())
                    itemsTeams?.add(teamsGet)
                }
                val changeTime = p0.result.child("Info").child("changeTime").value.toString().toInt().toDouble()
                itemsTime?.add(changeTime)

                requireActivity().runOnUiThread {
                    adapter.update2(items!!)
                    adapter.update2Teams(itemsTeams!!)
                    adapter.update3time(itemsTime!!)
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

    override fun onNewBoxListener(position: Int, teamNumber: Int, stintDone: Boolean, nameTeam: String) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
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


        }
    }

    override fun onBoxCreated(raceIdBox: String, teamName: String, teamNumber: Int, time: Double, stint: Int, activity: String) {
        //val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        //val raceId: String = activity?.getMyData().toString()
        //val stintId: String = activity?.getMyDataStint().toString()

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
                    adapter.update2(items!!)
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
    }

    override fun raceId(): String? {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        return activity?.getMyData().toString()
    }

    /*override fun dataChangedBoolFalse(position: Int) {
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
    }*/
}