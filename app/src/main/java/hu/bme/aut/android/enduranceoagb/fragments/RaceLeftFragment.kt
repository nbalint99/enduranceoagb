package hu.bme.aut.android.enduranceoagb.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.RaceActivity
import hu.bme.aut.android.enduranceoagb.adapter.RaceAdapter
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.databinding.RaceleftfragmentBinding
import java.util.*
import kotlin.concurrent.thread

class RaceLeftFragment : Fragment(), RaceAdapter.RaceItemClickListener, NewRaceFragment.NewRaceListener {
    private lateinit var rvAdapter: RaceAdapter

    private lateinit var dbRef: DatabaseReference

    private val cal = Calendar.getInstance()

    private val year2 = cal.get(Calendar.YEAR).toString()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = RaceleftfragmentBinding.inflate(layoutInflater)

        //Firebase.database.setPersistenceEnabled(true)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)

        binding.rvMainLeft.setLayoutManager(layoutManager)

        rvAdapter = RaceAdapter(this)

        binding.rvMainLeft.adapter = rvAdapter

        loadItemsInBackground()


        return binding.root
    }

    private fun loadItemsInBackground() {
        getData()
    }

    private fun getData() {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").orderByKey().ref

        dbRef.keepSynced(true)

        val items : MutableList<Races>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.children) {
                    /*println(element.child("Info").child("nameR").value.toString())
                    println(element.child("Info").child("location").value.toString())
                    println(element.child("Info").child("numberOfTeams").value.toString().toInt())
                    println(element.child("Info").child("allStintNumber").value.toString().toInt())*/
                    val addRace = element.child("Info").child("numberOfTeams").value.toString().toIntOrNull()
                        ?.let {
                            Races(element.key, element.child("Info").child("nameR").value.toString(), element.child("Info").child("location").value.toString(),
                                it, element.child("Info").child("allStintNumber").value.toString().toInt(),
                                element.child("Info").child("hasStintReady").value.toString().toBoolean(), element.child("Info").child("hasRaceDone").value.toString().toBoolean(),
                                element.child("Info").child("petrolDone").value.toString().toBoolean(), element.child("Info").child("hasTeamsDone").value.toString().toInt(),
                                element.child("Info").child("hasResultsDone").value.toString().toBoolean(), element.child("Info").child("hasQualiDone").value.toString().toInt(),
                                element.child("Info").child("numberOfRace").value.toString().toIntOrNull(), element.child("Info").child("hasGroupDone").value.toString().toBooleanStrictOrNull())
                        }
                    if (!element.child("Info").child("hasRaceDone").value.toString().toBoolean()){
                        if (addRace != null) {
                            items?.add(addRace)
                        }
                    }
                }
                requireActivity().runOnUiThread {
                    if (items != null) {
                        rvAdapter.update2(items)
                        if (items.size == 0) {
                            val snack = Snackbar.make(requireView(), R.string.notMoreRace, Snackbar.LENGTH_LONG)
                            snack.show()
                        }
                    }
                }
            }
        }
    }

    override fun onItemClick(race: Races?) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").orderByKey().ref

        dbRef.keepSynced(true)

        val items : MutableList<Races>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.children) {
                    val addRace = Races(element.key, element.child("Info").child("nameR").value.toString(), element.child("Info").child("location").value.toString(),
                        element.child("Info").child("numberOfTeams").value.toString().toInt(), element.child("Info").child("allStintNumber").value.toString().toInt(),
                        element.child("Info").child("hasStintReady").value.toString().toBoolean(), element.child("Info").child("hasRaceDone").value.toString().toBoolean(),
                        element.child("Info").child("petrolDone").value.toString().toBoolean(), element.child("Info").child("hasTeamsDone").value.toString().toInt(),
                        element.child("Info").child("hasResultsDone").value.toString().toBoolean(), element.child("Info").child("hasQualiDone").value.toString().toInt(),
                        element.child("Info").child("numberOfRace").value.toString().toIntOrNull(), element.child("Info").child("hasGroupDone").value.toString().toBooleanStrictOrNull())
                    if (!element.child("Info").child("hasRaceDone").value.toString().toBoolean()){
                        items?.add(addRace)
                    }
                }
                if (items?.get(0)?.id_r == race?.id_r) {
                    val showDetailsIntent = Intent()
                    showDetailsIntent.setClass(this.requireActivity(), RaceActivity::class.java)
                    showDetailsIntent.putExtra(RaceActivity.EXTRA_RACE_NAME, race?.id_r.toString())
                    startActivity(showDetailsIntent)
                    if (!p0.result.child(race?.id_r.toString()).child("Info").child("hasStintReady").value.toString().toBoolean()) {
                        dbRef.child(race?.id_r.toString()).child("Id").setValue(0)
                        val id = 0
                        dbRef.child(race?.id_r.toString()).child("Excel").child(id.toString()).child("stintNumber").setValue("-")
                        dbRef.child(race?.id_r.toString()).child("Excel").child(id.toString()).child("teamNumber").setValue("-")
                        dbRef.child(race?.id_r.toString()).child("Excel").child(id.toString()).child("driver").setValue("-")
                        dbRef.child(race?.id_r.toString()).child("Excel").child(id.toString()).child("plusWeight").setValue("-")
                        dbRef.child(race?.id_r.toString()).child("Excel").child(id.toString()).child("totalWeight").setValue("-")
                        dbRef.child(race?.id_r.toString()).child("Excel").child(id.toString()).child("kartNumber").setValue("-")
                    }
                }
                else {
                    val snack = Snackbar.make(requireView(),R.string.notRace, Snackbar.LENGTH_LONG)
                    snack.show()
                }
            }
        }
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
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races")

        dbRef.keepSynced(true)

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                if (p0.result.child(key).child("Info").child("hasStintReady").value.toString().toBoolean()) {
                    val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    builder.setTitle("Figyelem!")
                    builder.setMessage("A verseny adatait már nem módosíthatod! Szeretnéd ezt a versenyt az aktuális versennyé tenni?")

                    builder.setPositiveButton(R.string.yes) { _, _ ->
                        dbRef.child("Actual").child("key").setValue(key)
                        dbRef.child("Actual").child("name").setValue(location)
                    }
                    builder.setNegativeButton(R.string.no, null)
                    builder.setNeutralButton(R.string.button_megse, null)
                    builder.show()
                }
                else {
                    val dialogBuilder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(
                        requireContext(),
                        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                    )

                    val inflater = this.layoutInflater
                    val dialogView: View =
                        inflater.inflate(R.layout.modify_race_fragment, null)
                    dialogBuilder.setView(dialogView)
                    dialogBuilder.setTitle(R.string.modifyRace)

                    val nameRaceEdit = dialogView.findViewById<EditText>(R.id.etNameRaceEdit)
                    val numberOfTeamsEdit = dialogView.findViewById<EditText>(R.id.etNewRaceTeamsEdit)
                    val locationEdit = dialogView.findViewById<TextView>(R.id.tvLocationEdit)
                    val cbActual = dialogView.findViewById<CheckBox>(R.id.cbActual)

                    nameRaceEdit.setText(nameRace)
                    numberOfTeamsEdit.setText(numberOfTeams.toString())
                    locationEdit.text = location

                    dialogBuilder.setPositiveButton(R.string.button_ok) { _, _ ->
                        if (nameRaceEdit.text.toString()
                                .isNotEmpty() && numberOfTeamsEdit.text.toString().isNotEmpty() && numberOfTeamsEdit.text.toString().toInt() in 5..14
                        ) {
                            dbRef.child(key).child("Info").child("numberOfRace").setValue(nameRaceEdit.text.toString())
                            dbRef.child(key).child("Info").child("nameR").setValue(year2 + " - " + nameRaceEdit.text.toString() + ". verseny")
                            dbRef.child(key).child("Info").child("numberOfTeams").setValue(numberOfTeamsEdit.text.toString())
                            dbRef.child(key).child("Info").child("allStintNumber").setValue(numberOfStints(numberOfTeamsEdit.text.toString().toInt()).toString())

                            if (cbActual.isChecked) {
                                dbRef.child("Actual").child("key").setValue(key)
                                dbRef.child("Actual").child("name").setValue(location)
                            }

                            val intent = requireActivity().intent
                            intent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                        or Intent.FLAG_ACTIVITY_NO_ANIMATION
                            )
                            requireActivity().overridePendingTransition(0, 0)
                            requireActivity().finish()

                            requireActivity().overridePendingTransition(0, 0)
                            startActivity(intent)

                        } else {
                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle(R.string.warning)
                                .setMessage(R.string.validNot)
                                .setPositiveButton(R.string.button_ok, null)
                                .setNegativeButton("", null)
                                .show()
                        }
                    }
                    dialogBuilder.setNegativeButton(R.string.button_megse, null)
                    val alertDialog = dialogBuilder.create()
                    alertDialog.show()
                }
            }


        }
    }

    private fun numberOfStints(numberOfTeams: Int): Int {
        return when (numberOfTeams) {
            5 -> 6
            6 -> 7
            7 -> 8
            8 -> 9
            9 -> 10
            10 -> 6
            11 -> 7
            12 -> 7
            13 -> 8
            14 -> 8
            else -> 0
        }
    }


}