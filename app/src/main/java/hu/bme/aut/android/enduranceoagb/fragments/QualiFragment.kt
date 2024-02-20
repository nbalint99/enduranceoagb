package hu.bme.aut.android.enduranceoagb.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.NewDriverFragmentBinding
import hu.bme.aut.android.enduranceoagb.databinding.QualiTeamFragmentBinding

class QualiFragment : DialogFragment() {
    interface QualiListener {
        fun onQualiCreated(teamName: String, teamNumber: Int?, kartNumber: Int?, group: Int?)
    }

    private lateinit var listener: QualiListener

    private lateinit var dbRef: DatabaseReference

    private lateinit var binding: QualiTeamFragmentBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? QualiListener
            ?: throw RuntimeException("Activity must implement the NewDriverListener interface!")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = QualiTeamFragmentBinding.inflate(LayoutInflater.from(context))

        val dataPassedTeamName: String? = arguments?.getString("teamName")
        val dataPassedTeamNumber: String? = arguments?.getString("teamNumber")
        val dataPassedPeople: String? = arguments?.getString("people")
        val dataPassedKart: String? = arguments?.getString("startKartNumber")
        val dataPassedGP2: String? = arguments?.getString("gp2")
        val dataPassedGroup: String? = arguments?.getString("group")

        if (dataPassedGP2 == "true") {
            binding.tvNameTeamQuali.text = "$dataPassedTeamName (GP2)"
        }
        else {
            binding.tvNameTeamQuali.text = "$dataPassedTeamName"
        }

        binding.tvPeopleTeamQuali.text = "$dataPassedPeople fő"

        if (dataPassedTeamNumber == "null") {
            binding.etQualiResult.setText("")
        }
        else {
            binding.etQualiResult.setText(dataPassedTeamNumber)
        }

        if (dataPassedKart == "null") {
            binding.etKartNumberQuali.setText("")
        }
        else {
            binding.etKartNumberQuali.setText(dataPassedKart)
        }


        return AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            .setTitle(R.string.qualiNumber)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok) { _, _ ->
                val teamNumber: Int? = binding.etQualiResult.text.toString().toIntOrNull()
                val kartNumber: Int? = binding.etKartNumberQuali.text.toString().toIntOrNull()

                listener.onQualiCreated(dataPassedTeamName.toString(), teamNumber, kartNumber, dataPassedGroup.toString().toIntOrNull())
            }
            .setNegativeButton(R.string.button_megse, null)
            .create()
    }

    companion object {

        @JvmStatic //This can be avoided if you are in a complete Kotlin project
        fun newInstance(teamName: String, teamNumber: String?, people: String, startKartNumber: String?, gp2: String, group: Int?): QualiFragment {
            val args = Bundle()
            args.putString("teamName", teamName)
            args.putString("teamNumber", teamNumber.toString())
            args.putString("people", people)
            args.putString("startKartNumber", startKartNumber)
            args.putString("gp2", gp2)
            args.putString("group", group.toString())
            val fragment = QualiFragment()
            fragment.arguments = args
            return fragment
        }

    }

}