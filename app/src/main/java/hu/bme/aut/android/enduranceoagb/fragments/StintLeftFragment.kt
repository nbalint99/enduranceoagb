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
                        val changeCheck = "Etap: ${position.toString().toInt()-1}"
                        val teamStintCheck = "${position.toString().toInt()-1}-box11"
                        if (!p0.result.child("Stints").child(changeCheck).child("Info").child(teamStintCheck).child("kartNumber").exists()) {

                            val numberOfTeams =
                                p0.result.child("Info").child("numberOfTeams").value.toString()
                                    .toInt()
                            val firstMore =
                                p0.result.child("Info").child("firstMore").value.toString()
                                    .toBooleanStrictOrNull()
                            val secondMore =
                                p0.result.child("Info").child("secondMore").value.toString()
                                    .toBooleanStrictOrNull()
                            val equalGroup =
                                p0.result.child("Info").child("equalGroup").value.toString()
                                    .toBooleanStrictOrNull()
                            if (numberOfTeams < 10) {
                                val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                                    requireContext(),
                                    android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                                )

                                val inflater = this.layoutInflater
                                val dialogView: View =
                                    inflater.inflate(
                                        hu.bme.aut.android.enduranceoagb.R.layout.parking_kart_fragment,
                                        null
                                    )
                                dialogBuilder.setView(dialogView)
                                dialogBuilder.setTitle(hu.bme.aut.android.enduranceoagb.R.string.parkingKart)

                                val parkingKart =
                                    dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart)

                                val kartNumberOri =
                                    p0.result.child("Stints").child("Etap: 1").child("Info")
                                        .child("1-box12")
                                        .child("kartNumber").value.toString().toIntOrNull()
                                if (kartNumberOri != null || kartNumberOri.toString() != "null") {
                                    parkingKart.setText(kartNumberOri.toString())
                                }

                                dialogBuilder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok) { _, _ ->
                                    if (parkingKart.text.toString()
                                            .isNotEmpty()
                                    ) {
                                        dbRef.child("Stints").child("Etap: 1").child("Info")
                                            .child("1-box11")
                                            .child("kartNumber")
                                            .setValue(parkingKart.text.toString())
                                        dbRef.child("Stints").child("Etap: 1").child("Info")
                                            .child("1-box12")
                                            .child("kartNumber")
                                            .setValue(parkingKart.text.toString())
                                        val id = p0.result.child("Id").value.toString()
                                        var idNumber: Int
                                        if (id == "-1") {
                                            idNumber = 0
                                        } else {
                                            idNumber = id.toInt()
                                            idNumber++
                                        }
                                        dbRef.child("Id").setValue(idNumber)
                                        //val teamStintId = idNumber
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("stintNumber").setValue("1. etap")
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("teamNumber").setValue("box11")
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("driver").setValue("-")
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("plusWeight").setValue("-")
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("totalWeight").setValue("-")
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("kartNumber")
                                            .setValue(parkingKart.text.toString())
                                        idNumber++

                                        dbRef.child("Id").setValue(idNumber)
                                        //val teamStintId = idNumber
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("stintNumber").setValue("1. etap")
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("teamNumber").setValue("box12")
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("driver").setValue("-")
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("plusWeight").setValue("-")
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("totalWeight").setValue("-")
                                        dbRef.child("Excel").child(idNumber.toString())
                                            .child("kartNumber")
                                            .setValue(parkingKart.text.toString())

                                    } else {
                                        AlertDialog.Builder(requireContext())
                                            .setTitle(hu.bme.aut.android.enduranceoagb.R.string.warning)
                                            .setMessage(hu.bme.aut.android.enduranceoagb.R.string.validAll)
                                            .setPositiveButton(
                                                hu.bme.aut.android.enduranceoagb.R.string.button_ok,
                                                null
                                            )
                                            .setNegativeButton("", null)
                                            .show()
                                    }
                                }
                                dialogBuilder.setNegativeButton(
                                    hu.bme.aut.android.enduranceoagb.R.string.button_megse,
                                    null
                                )
                                val alertDialog = dialogBuilder.create()
                                alertDialog.show()
                            } else {
                                //ha firstmore, akkor legyen egy olyan layout, ahol 1 első csoport, 2 másik
                                //ha secondmore, akkor legyen egy olyan layout, ahol 2 első csoport, 1 másik
                                //ha equalGroup, akkor legyen egy olyan layout, ahol 1 első csoport, 1 másik
                                val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                                    requireContext(),
                                    android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                                )

                                if (firstMore == true) {
                                    val inflater = this.layoutInflater
                                    val dialogView: View =
                                        inflater.inflate(
                                            hu.bme.aut.android.enduranceoagb.R.layout.parking_kart_fragment_firstmore,
                                            null
                                        )
                                    dialogBuilder.setView(dialogView)
                                    dialogBuilder.setTitle(hu.bme.aut.android.enduranceoagb.R.string.parkingKarts)

                                    val parkingKart11 =
                                        dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart11)
                                    val parkingKart21 =
                                        dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart21)
                                    val parkingKart22 =
                                        dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart22)


                                    dialogBuilder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok) { _, _ ->
                                        if (parkingKart11.text.toString()
                                                .isNotEmpty() && parkingKart21.text.toString()
                                                .isNotEmpty() && parkingKart22.text.toString()
                                                .isNotEmpty()
                                        ) {
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box11")
                                                .child("kartNumber")
                                                .setValue(parkingKart11.text.toString())
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box12")
                                                .child("kartNumber")
                                                .setValue(parkingKart11.text.toString())
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box21")
                                                .child("kartNumber")
                                                .setValue(parkingKart21.text.toString())
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box22")
                                                .child("kartNumber")
                                                .setValue(parkingKart22.text.toString())
                                            val id = p0.result.child("Id").value.toString()
                                            var idNumber: Int
                                            if (id == "-1") {
                                                idNumber = 0
                                            } else {
                                                idNumber = id.toInt()
                                                idNumber++
                                            }
                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box11")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart11.text.toString())
                                            idNumber++

                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box12")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart11.text.toString())
                                            idNumber++

                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box21")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart21.text.toString())
                                            idNumber++

                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box22")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart22.text.toString())

                                        } else {
                                            AlertDialog.Builder(requireContext())
                                                .setTitle(hu.bme.aut.android.enduranceoagb.R.string.warning)
                                                .setMessage(hu.bme.aut.android.enduranceoagb.R.string.validNot)
                                                .setPositiveButton(
                                                    hu.bme.aut.android.enduranceoagb.R.string.button_ok,
                                                    null
                                                )
                                                .setNegativeButton("", null)
                                                .show()
                                        }
                                    }
                                    dialogBuilder.setNegativeButton(
                                        hu.bme.aut.android.enduranceoagb.R.string.button_megse,
                                        null
                                    )
                                    val alertDialog = dialogBuilder.create()
                                    alertDialog.show()
                                } else if (secondMore == true) {
                                    val inflater = this.layoutInflater
                                    val dialogView: View =
                                        inflater.inflate(
                                            hu.bme.aut.android.enduranceoagb.R.layout.parking_kart_fragment_secondmore,
                                            null
                                        )
                                    dialogBuilder.setView(dialogView)
                                    dialogBuilder.setTitle(hu.bme.aut.android.enduranceoagb.R.string.parkingKarts)

                                    val parkingKart11 =
                                        dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart11)
                                    val parkingKart12 =
                                        dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart12)
                                    val parkingKart21 =
                                        dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart21)


                                    dialogBuilder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok) { _, _ ->
                                        if (parkingKart11.text.toString()
                                                .isNotEmpty() && parkingKart12.text.toString()
                                                .isNotEmpty() && parkingKart21.text.toString()
                                                .isNotEmpty()
                                        ) {
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box11")
                                                .child("kartNumber")
                                                .setValue(parkingKart11.text.toString())
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box12")
                                                .child("kartNumber")
                                                .setValue(parkingKart12.text.toString())
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box21")
                                                .child("kartNumber")
                                                .setValue(parkingKart21.text.toString())
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box22")
                                                .child("kartNumber")
                                                .setValue(parkingKart21.text.toString())
                                            val id = p0.result.child("Id").value.toString()
                                            var idNumber: Int
                                            if (id == "-1") {
                                                idNumber = 0
                                            } else {
                                                idNumber = id.toInt()
                                                idNumber++
                                            }
                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box11")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart11.text.toString())
                                            idNumber++

                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box12")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart12.text.toString())
                                            idNumber++

                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box21")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart21.text.toString())
                                            idNumber++

                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box22")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart21.text.toString())

                                        } else {
                                            AlertDialog.Builder(requireContext())
                                                .setTitle(hu.bme.aut.android.enduranceoagb.R.string.warning)
                                                .setMessage(hu.bme.aut.android.enduranceoagb.R.string.validNot)
                                                .setPositiveButton(
                                                    hu.bme.aut.android.enduranceoagb.R.string.button_ok,
                                                    null
                                                )
                                                .setNegativeButton("", null)
                                                .show()
                                        }
                                    }
                                    dialogBuilder.setNegativeButton(
                                        hu.bme.aut.android.enduranceoagb.R.string.button_megse,
                                        null
                                    )
                                    val alertDialog = dialogBuilder.create()
                                    alertDialog.show()
                                } else if (equalGroup == true) {
                                    val inflater = this.layoutInflater
                                    val dialogView: View =
                                        inflater.inflate(
                                            hu.bme.aut.android.enduranceoagb.R.layout.parking_kart_fragment_equalgroup,
                                            null
                                        )
                                    dialogBuilder.setView(dialogView)
                                    dialogBuilder.setTitle(hu.bme.aut.android.enduranceoagb.R.string.parkingKarts)

                                    val parkingKart11 =
                                        dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart11)
                                    val parkingKart21 =
                                        dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart21)


                                    dialogBuilder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok) { _, _ ->
                                        if (parkingKart11.text.toString()
                                                .isNotEmpty() && parkingKart21.text.toString()
                                                .isNotEmpty()
                                        ) {
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box11")
                                                .child("kartNumber")
                                                .setValue(parkingKart11.text.toString())
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box12")
                                                .child("kartNumber")
                                                .setValue(parkingKart11.text.toString())
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box21")
                                                .child("kartNumber")
                                                .setValue(parkingKart21.text.toString())
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box22")
                                                .child("kartNumber")
                                                .setValue(parkingKart21.text.toString())
                                            val id = p0.result.child("Id").value.toString()
                                            var idNumber: Int
                                            if (id == "-1") {
                                                idNumber = 0
                                            } else {
                                                idNumber = id.toInt()
                                                idNumber++
                                            }
                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box11")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart11.text.toString())
                                            idNumber++

                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box12")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart11.text.toString())
                                            idNumber++

                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box21")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart21.text.toString())
                                            idNumber++

                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box22")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart21.text.toString())

                                        } else {
                                            AlertDialog.Builder(requireContext())
                                                .setTitle(hu.bme.aut.android.enduranceoagb.R.string.warning)
                                                .setMessage(hu.bme.aut.android.enduranceoagb.R.string.validNot)
                                                .setPositiveButton(
                                                    hu.bme.aut.android.enduranceoagb.R.string.button_ok,
                                                    null
                                                )
                                                .setNegativeButton("", null)
                                                .show()
                                        }
                                    }
                                    dialogBuilder.setNegativeButton(
                                        hu.bme.aut.android.enduranceoagb.R.string.button_megse,
                                        null
                                    )
                                    val alertDialog = dialogBuilder.create()
                                    alertDialog.show()
                                } else {
                                    val inflater = this.layoutInflater
                                    val dialogView: View =
                                        inflater.inflate(
                                            hu.bme.aut.android.enduranceoagb.R.layout.parking_kart_fragment,
                                            null
                                        )
                                    dialogBuilder.setView(dialogView)
                                    dialogBuilder.setTitle(hu.bme.aut.android.enduranceoagb.R.string.parkingKart)

                                    val parkingKart =
                                        dialogView.findViewById<EditText>(hu.bme.aut.android.enduranceoagb.R.id.etParkingKart)

                                    dialogBuilder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok) { _, _ ->
                                        if (parkingKart.text.toString()
                                                .isNotEmpty()
                                        ) {
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box11")
                                                .child("kartNumber")
                                                .setValue(parkingKart.text.toString())
                                            dbRef.child("Stints").child("Etap: 1").child("Info")
                                                .child("1-box12")
                                                .child("kartNumber")
                                                .setValue(parkingKart.text.toString())
                                            val id = p0.result.child("Id").value.toString()
                                            var idNumber: Int
                                            if (id == "-1") {
                                                idNumber = 0
                                            } else {
                                                idNumber = id.toInt()
                                                idNumber++
                                            }
                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box11")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart.text.toString())
                                            idNumber++

                                            dbRef.child("Id").setValue(idNumber)
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("stintNumber").setValue("1. etap")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("teamNumber").setValue("box12")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("driver").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("plusWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("totalWeight").setValue("-")
                                            dbRef.child("Excel").child(idNumber.toString())
                                                .child("kartNumber")
                                                .setValue(parkingKart.text.toString())

                                        } else {
                                            AlertDialog.Builder(requireContext())
                                                .setTitle(hu.bme.aut.android.enduranceoagb.R.string.warning)
                                                .setMessage(hu.bme.aut.android.enduranceoagb.R.string.validNot)
                                                .setPositiveButton(
                                                    hu.bme.aut.android.enduranceoagb.R.string.button_ok,
                                                    null
                                                )
                                                .setNegativeButton("", null)
                                                .show()
                                        }
                                    }
                                    dialogBuilder.setNegativeButton(
                                        hu.bme.aut.android.enduranceoagb.R.string.button_megse,
                                        null
                                    )
                                    val alertDialog = dialogBuilder.create()
                                    alertDialog.show()
                                }
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