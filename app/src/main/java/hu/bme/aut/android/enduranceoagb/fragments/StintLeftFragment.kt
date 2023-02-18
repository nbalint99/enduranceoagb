package hu.bme.aut.android.enduranceoagb.fragments

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.*
import hu.bme.aut.android.enduranceoagb.adapter.StintAdapter
import hu.bme.aut.android.enduranceoagb.data.DoneStint
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.databinding.StintleftfragmentBinding
import kotlin.concurrent.thread


class StintLeftFragment : Fragment(), StintAdapter.StintItemClickListener{

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: StintAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = StintleftfragmentBinding.inflate(layoutInflater)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)

        binding.rvMainStintLeft.setLayoutManager(layoutManager)

        adapter = StintAdapter(this)

        binding.rvMainStintLeft.adapter = adapter

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
                        if (!p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("hasStintDone").value.toString().toBoolean()){
                            items?.add(addDoneStint)
                        }
                    }

                requireActivity().runOnUiThread {
                    if (items != null) {
                        adapter.update2(items)
                        if (items.size == 0) {
                            val snack = Snackbar.make(requireView(), R.string.notMoreStint, Snackbar.LENGTH_LONG)
                            snack.show()
                        }
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

                if(items?.get(0)?.numberOfStint == position.toString().toInt()) {
                    if (position == 2) {
                        val change = "Etap: ${position.toString().toInt()-1}"
                        val teamStint = "${position.toString().toInt()-1}-box"
                        if (!p0.result.child("Stints").child(change).child("Info").child(teamStint).child("kartNumber").exists()) {
                            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                                requireContext(),
                                android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                            )

                            val inflater = this.layoutInflater
                            val dialogView: View =
                                inflater.inflate(R.layout.parking_kart_fragment, null)
                            dialogBuilder.setView(dialogView)
                            dialogBuilder.setTitle(R.string.parkingKart)

                            val parkingKart = dialogView.findViewById<EditText>(R.id.etParkingKart)

                            dialogBuilder.setPositiveButton(R.string.button_ok) { _, _ ->
                                if (parkingKart.text.toString()
                                        .isNotEmpty()
                                ) {
                                    dbRef.child("Stints").child("Etap: 1").child("Info").child("1-box").child("kartNumber").setValue(parkingKart.text.toString())
                                    dbRef.child("Cserék").child("Etap: 01").child("5 - Parkol").child("Parkol")
                                        .setValue(parkingKart.text.toString())

                                    showDetailsIntent.setClass(requireActivity(), DetailsStintWatchActivity::class.java)
                                    showDetailsIntent.putExtra(DetailsStintWatchActivity.EXTRA_STINT_NUMBER, position.toString())
                                    showDetailsIntent.putExtra(DetailsStintWatchActivity.EXTRA_RACE_NAME, raceId)
                                    startActivity(showDetailsIntent)

                                } else {
                                    AlertDialog.Builder(requireContext())
                                        .setTitle(R.string.warning)
                                        .setMessage(R.string.validAll)
                                        .setPositiveButton(R.string.button_ok, null)
                                        .setNegativeButton("", null)
                                        .show()
                                }
                            }
                            dialogBuilder.setNegativeButton(R.string.button_megse, null)
                            val alertDialog = dialogBuilder.create()
                            alertDialog.show()
                        }
                        else {
                            showDetailsIntent.setClass(requireActivity(), DetailsStintWatchActivity::class.java)
                            showDetailsIntent.putExtra(DetailsStintWatchActivity.EXTRA_STINT_NUMBER, position.toString())
                            showDetailsIntent.putExtra(DetailsStintWatchActivity.EXTRA_RACE_NAME, raceId)
                            startActivity(showDetailsIntent)
                        }
                    }
                    else {
                        showDetailsIntent.setClass(requireActivity(), DetailsStintWatchActivity::class.java)
                        showDetailsIntent.putExtra(DetailsStintWatchActivity.EXTRA_STINT_NUMBER, position.toString())
                        showDetailsIntent.putExtra(DetailsStintWatchActivity.EXTRA_RACE_NAME, raceId)
                        startActivity(showDetailsIntent)
                    }

                }
                else {
                    val snack = Snackbar.make(requireView(), R.string.notStint, Snackbar.LENGTH_LONG)
                    snack.show()
                }

            }
        }
    }
}