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
import hu.bme.aut.android.enduranceoagb.PodiumActivity
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.RaceActivity
import hu.bme.aut.android.enduranceoagb.StintActivity2
import hu.bme.aut.android.enduranceoagb.adapter.PodiumAdapter
import hu.bme.aut.android.enduranceoagb.adapter.RaceAdapter
import hu.bme.aut.android.enduranceoagb.data.DoneStint
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.data.Result
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.PodiumGp2FragmentBinding
import hu.bme.aut.android.enduranceoagb.databinding.RacedonefragmentBinding

class PodiumGp2Fragment : Fragment(), PodiumAdapter.PodiumItemClickListener {
    private lateinit var rvAdapter: PodiumAdapter

    private lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = PodiumGp2FragmentBinding.inflate(layoutInflater)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)

        binding.rvMainPodiumGp2.setLayoutManager(layoutManager)

        rvAdapter = PodiumAdapter(this)

        binding.rvMainPodiumGp2.adapter = rvAdapter

        loadItemsInBackground()

        return binding.root
    }

    private fun loadItemsInBackground() {
        getData()
    }

    private fun getData() {
        val activity: PodiumActivity? = activity as PodiumActivity?
        val raceId: String = activity?.getMyData().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId)

        val items : MutableList<Result>? = mutableListOf()
        val teams : MutableList<String>? = mutableListOf()
        val driversPass : MutableList<String>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val numberOfTeams =
                    p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                for (element in 1..numberOfTeams) {

                    val addResult = Result(p0.result.child("Result").child(element.toString()).child("team").value.toString(), p0.result.child("Result").child(element.toString()).child("gp2").value.toString().toBooleanStrictOrNull(), element)
                    if (p0.result.child("Result").child(element.toString()).child("gp2").value.toString().toBooleanStrictOrNull() == true && items?.size!! < 3) {
                        items.add(addResult)
                        teams?.add(addResult.nameTeam)
                    }
                }

                for (ele in 0 until items?.size!!) {
                    val driversString : MutableList<String>? = mutableListOf()
                    val drivers = p0.result.child("Teams").child(teams?.get(ele).toString()).child("Drivers").children
                    for (e in drivers) {
                        driversString?.add(e.child("nameDriver").value.toString())
                    }

                    if (driversString?.size == 1) {
                        val stringPass = driversString[0]
                        driversPass?.add(stringPass)
                    }
                    else if (driversString?.size == 2) {
                        val stringPass = driversString[0] + " - " + driversString[1]
                        driversPass?.add(stringPass)
                    }
                    else {
                        val stringPass = driversString!![0] + " - " + driversString[1] + " - " + driversString[2]
                        driversPass?.add(stringPass)
                    }
                }

                requireActivity().runOnUiThread {
                    rvAdapter.update2(items)
                    rvAdapter.update3(teams!!)
                    rvAdapter.update4(driversPass!!)
                }
            }
        }
    }
}