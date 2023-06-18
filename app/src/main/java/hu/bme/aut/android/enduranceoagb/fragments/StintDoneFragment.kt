package hu.bme.aut.android.enduranceoagb.fragments

import android.content.Intent
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
import hu.bme.aut.android.enduranceoagb.StintActivity2
import hu.bme.aut.android.enduranceoagb.adapter.StintAdapter
import hu.bme.aut.android.enduranceoagb.data.DoneStint
import hu.bme.aut.android.enduranceoagb.databinding.StintdonefragmentBinding

class StintDoneFragment : Fragment(), StintAdapter.StintItemClickListener{

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: StintAdapter

    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = StintdonefragmentBinding.inflate(layoutInflater)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)

        binding.rvMainStintDone.setLayoutManager(layoutManager)

        adapter = StintAdapter(this)

        binding.rvMainStintDone.adapter = adapter

        loadItemsInBackground()

        return binding.root
    }

    private fun loadItemsInBackground() {
        getData()
    }

    private fun getData() {
        val activity: StintActivity2? = activity as StintActivity2?
        val raceId: String = activity?.getMyData().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId)

        val items : MutableList<DoneStint>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val stintNumber =
                    p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                for (element in 1..stintNumber) {

                    val addDoneStint = DoneStint(p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("numberOfStint").value.toString().toInt(), p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("hasStintDone").value.toString().toBoolean(),
                        p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("hasDetailsStintDone").value.toString().toBoolean(), p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("zeroToUp").value.toString(),
                        p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("upToZero").value.toString())
                    if (p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("hasStintDone").value.toString().toBoolean()){
                        items?.add(addDoneStint)
                    }
                }

                requireActivity().runOnUiThread {
                    if (items != null) {
                        adapter.update2(items)
                    }
                }
            }
        }
    }

    override fun onStintSelected(position: Int) {
        val showDetailsIntent = Intent()
        val activity: StintActivity2? = activity as StintActivity2?
        val raceId: String = activity?.getMyData().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId)

        val items : MutableList<DoneStint>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val stintNumber =
                    p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                for (element in 1..stintNumber) {

                    val addDoneStint = DoneStint(p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("numberOfStint").value.toString().toInt(), p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("hasStintDone").value.toString().toBoolean(),
                        p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("hasDetailsStintDone").value.toString().toBoolean(), p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("zeroToUp").value.toString(),
                        p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("upToZero").value.toString())
                    if (!p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("hasStintDone").value.toString().toBoolean()){
                        items?.add(addDoneStint)
                    }
                }

               showDetailsIntent.setClass(requireActivity(), DetailsStintWatchActivity::class.java)
               showDetailsIntent.putExtra(DetailsStintWatchActivity.EXTRA_STINT_NUMBER, position.toString())
               showDetailsIntent.putExtra(DetailsStintWatchActivity.EXTRA_RACE_NAME, raceId)
               startActivity(showDetailsIntent)

            }
        }
    }
}