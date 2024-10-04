package hu.bme.aut.android.enduranceoagb.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.RaceActivity
import hu.bme.aut.android.enduranceoagb.adapter.RaceAdapter
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.databinding.RacedonefragmentBinding

class RaceDoneFragment : Fragment(), RaceAdapter.RaceItemClickListener, NewRaceFragment.NewRaceListener {
    private lateinit var rvAdapter: RaceAdapter

    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = RacedonefragmentBinding.inflate(layoutInflater)

        //Firebase.database.setPersistenceEnabled(true)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)

        binding.rvMainDone.setLayoutManager(layoutManager)

        rvAdapter = RaceAdapter(this)

        binding.rvMainDone.adapter = rvAdapter

        loadItemsInBackground()

        return binding.root
    }

    private fun loadItemsInBackground() {
        getData()
    }

    private fun getData() {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").orderByKey().ref

        //dbRef.keepSynced(true)

        val items : MutableList<Races>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.children) {
                    val addRace = element.child("Info").child("numberOfTeams").value.toString().toIntOrNull()
                        ?.let {
                            Races(element.key, element.child("Info").child("nameR").value.toString(), element.child("Info").child("location").value.toString(),
                                it, element.child("Info").child("allStintNumber").value.toString().toInt(),
                                element.child("Info").child("hasStintReady").value.toString().toBoolean(), element.child("Info").child("hasRaceDone").value.toString().toBoolean(),
                                element.child("Info").child("petrolDone").value.toString().toBoolean(), element.child("Info").child("hasTeamsDone").value.toString().toInt(),
                                element.child("Info").child("hasResultsDone").value.toString().toBoolean(), element.child("Info").child("hasQualiDone").value.toString().toInt(),
                                element.child("Info").child("numberOfRace").value.toString().toIntOrNull(), element.child("Info").child("hasGroupDone").value.toString().toBooleanStrictOrNull())
                        }
                    if (element.child("Info").child("hasRaceDone").value.toString().toBoolean()){
                        if (addRace != null) {
                            items?.add(addRace)
                        }
                    }
                }
                requireActivity().runOnUiThread {
                    if (items != null) {
                        items.sortByDescending { it.id_r }
                        rvAdapter.update2(items)
                    }
                }
            }
        }
    }

    override fun onItemClick(race: Races?) {
        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this.requireActivity(), RaceActivity::class.java)
        showDetailsIntent.putExtra(RaceActivity.EXTRA_RACE_NAME, race?.id_r.toString())
        startActivity(showDetailsIntent)
    }

    override fun onItemLongClick(race: Races?): Boolean {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Figyelem!")
        builder.setMessage("Biztos, hogy törölni szeretnéd ezt a versenyt?")

        builder.setPositiveButton(R.string.button_ok) { dialog, which ->
            rvAdapter.deleteItem(race)
            dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races")

            dbRef.keepSynced(true)

            dbRef.get().addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val child = p0.result.child(race?.id_r.toString()).key
                    dbRef.child(child.toString()).removeValue()
                }
            }
        }

        builder.setNeutralButton(R.string.button_megse, null)
        builder.show()
        return true
    }

    override fun onRaceCreated(newItem: Races) {

    }

    override fun onRaceNotCreated() {
        val snack = Snackbar.make(requireView(), R.string.notAddDriver, Snackbar.LENGTH_LONG)
        snack.show()
    }

    override fun onModifyRaceListener(key: String, location: String, numberOfTeams: Int, nameRace: String) {
        val snack = Snackbar.make(requireView(), R.string.notModifyRace, Snackbar.LENGTH_LONG)
        snack.show()
    }
}