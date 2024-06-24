package hu.bme.aut.android.enduranceoagb.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.NewDriverFragmentBinding
import hu.bme.aut.android.enduranceoagb.databinding.QualiTeamFragmentBinding
import hu.bme.aut.android.enduranceoagb.databinding.StartKartTeamFragmentBinding

class StartKartFragment : DialogFragment() {
    interface QualiListener {
        fun onQualiCreated2(teamName: String, teamNumber: Int?, kartNumber: Int?, group: Int?, prevKartNumber: Int?)
    }

    private lateinit var listener: QualiListener

    private lateinit var dbRef: DatabaseReference

    private lateinit var binding: StartKartTeamFragmentBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? QualiListener
            ?: throw RuntimeException("Activity must implement the NewDriverListener interface!")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = StartKartTeamFragmentBinding.inflate(LayoutInflater.from(context))

        val dataPassedTeamName: String? = arguments?.getString("teamName")
        val dataPassedTeamNumber: String? = arguments?.getString("teamNumber")
        val dataPassedPeople: String? = arguments?.getString("people")
        val dataPassedKart: String? = arguments?.getString("startKartNumber")
        val dataPassedGP2: String? = arguments?.getString("gp2")
        val dataPassedGroup: String? = arguments?.getString("group")
        val dataPassedHasQualiDone: String? = arguments?.getString("hasQualiDone")

        if (dataPassedGP2 == "true") {
            binding.tvNameTeamQuali.text = "$dataPassedTeamName (GP2)"
        }
        else {
            binding.tvNameTeamQuali.text = "$dataPassedTeamName"
        }

        binding.tvPeopleTeamQuali.text = "$dataPassedPeople fő"

        binding.tvQualiResult.text = "$dataPassedTeamNumber. csapat"

        binding.tvQualiKartNumber.text = "Gokart: $dataPassedKart"


        dbRef =
            FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference("nyers_gokart")

        var sendMembersList: MutableList<String>? = mutableListOf()
        var alreadyOnTheList: MutableList<Int>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {

                for (ele in p0.result.children) {
                    val kart = ele.child("kart").value.toString()
                    val id = ele.child("id").value.toString().toInt()
                    val group = ele.child("group").value.toString()
                    val oriGroup = ele.child("ori_group").value.toString()
                    val alreadyAdded = ele.child("selected").value.toString().toBooleanStrictOrNull()
                    if (dataPassedGroup?.toInt() == 1) {
                        if (oriGroup == 1.toString() && alreadyAdded != true) {
                            if (kart != "") {
                                alreadyOnTheList?.add(kart.toInt())
                            }
                        }
                    }
                    else if (dataPassedGroup?.toInt() == 2) {
                        if (oriGroup == 2.toString() && alreadyAdded != true) {
                            if (kart != "") {
                                alreadyOnTheList?.add(kart.toInt())
                            }
                        }
                    }
                }


                val selectOne = "-- Válassz egyet! --"
                if (sendMembersList != null) {
                    sendMembersList.add(0, selectOne)

                    if (alreadyOnTheList != null) {
                        for (elem in alreadyOnTheList) {
                            sendMembersList.add(elem.toString())
                        }
                    }
                }

                if (sendMembersList != null) {
                    binding.spStartKartNumber.adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        sendMembersList
                    )
                }
            }
        }


        return AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            .setTitle(R.string.startKartNumberFragment)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok) { _, _ ->
                val kartNumber: Int = binding.spStartKartNumber.selectedItem.toString().toInt()
                if (dataPassedHasQualiDone == "true") {
                    listener.onQualiCreated2(dataPassedTeamName.toString(), dataPassedTeamNumber?.toIntOrNull(), kartNumber, dataPassedGroup.toString().toIntOrNull(), dataPassedKart.toString().toIntOrNull())
                }
                else {
                    listener.onQualiCreated2(dataPassedTeamName.toString(), dataPassedTeamNumber?.toIntOrNull(), kartNumber, dataPassedGroup.toString().toIntOrNull(), null)

                }
            }
            .setNegativeButton(R.string.button_megse, null)
            .create()
    }

    companion object {

        @JvmStatic //This can be avoided if you are in a complete Kotlin project
        fun newInstance(teamName: String, teamNumber: String?, people: String, startKartNumber: String?, gp2: String, group: Int?, hasQualiDone: String?): StartKartFragment {
            val args = Bundle()
            args.putString("teamName", teamName)
            args.putString("teamNumber", teamNumber.toString())
            args.putString("people", people)
            args.putString("startKartNumber", startKartNumber)
            args.putString("gp2", gp2)
            args.putString("group", group.toString())
            args.putString("hasQualiDone", hasQualiDone)
            val fragment = StartKartFragment()
            fragment.arguments = args
            return fragment
        }

    }

}