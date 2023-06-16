package hu.bme.aut.android.enduranceoagb.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.databinding.ResultFragmentBinding

class ResultFragment : DialogFragment() {
    interface ResultFragmentListener {
        fun onResultCreated(
            result: Int,
            team: String,
            gp2: Boolean?
        )

        fun raceId(): String?
    }

    private lateinit var listener: ResultFragmentListener

    private lateinit var binding: ResultFragmentBinding

    private lateinit var dbRef: DatabaseReference

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? ResultFragmentListener
            ?: throw RuntimeException("Activity must implement the ResultFragmentListener interface!")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = ResultFragmentBinding.inflate(LayoutInflater.from(context))

        val raceId = listener.raceId().toString()

        val dataPassed: String? = arguments?.getString("position")

        binding.tvResult.text = "$dataPassed. helyezett"


        dbRef =
            FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("Races").child(raceId)

        val items: MutableList<String> = mutableListOf()
        val gp2Bool: MutableList<Boolean> = mutableListOf()
        val membersList: MutableList<String> = mutableListOf()
        var sendMembersList: MutableList<String>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.child("Teams").children) {
                    val addTeam = element.child("Info").child("shortTeamName").value.toString()
                    val addGP2 = element.child("Info").child("gp2").value.toString().toBoolean()
                    items.add(addTeam)
                    gp2Bool.add(addGP2)
                }

                for (el in p0.result.child("Result").children) {
                    val resultTeam = el.child("team").value.toString()
                    var checkNumber = 0
                    for (e in items) {
                        if (e == resultTeam) {
                            checkNumber++
                        }
                    }
                    if (checkNumber == 0) {
                        membersList.add(resultTeam)
                    }
                }

                val selectOne = "-- Válassz egyet! --"
                if (sendMembersList != null) {
                    sendMembersList.add(0, selectOne)

                    for (elem in membersList) {
                        sendMembersList.add(elem)
                    }
                }

                if (sendMembersList != null) {
                    binding.spTeam.adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        sendMembersList
                    )
                }
            }
        }

        return AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            .setTitle(hu.bme.aut.android.enduranceoagb.R.string.placeHolder)
            .setView(binding.root)
            .setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok) { _, _ ->
                val result: Int = dataPassed.toString().toInt()
                val team: String = binding.spTeam.selectedItem.toString()
                val itemId = binding.spTeam.selectedItemId.toInt()
                val selectedWeight = gp2Bool[itemId]

                listener.onResultCreated(result, team, selectedWeight)

            }
            .setNegativeButton(hu.bme.aut.android.enduranceoagb.R.string.button_megse, null)
            .create()


    }

    companion object {

        @JvmStatic //This can be avoided if you are in a complete Kotlin project
        fun newInstance(
            position: String
        ): ResultFragment {
            val args = Bundle()
            args.putString("position", position)
            val fragment = ResultFragment()
            fragment.arguments = args
            return fragment
        }

    }
}