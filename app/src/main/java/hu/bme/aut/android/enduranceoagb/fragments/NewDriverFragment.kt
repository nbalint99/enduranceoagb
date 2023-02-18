package hu.bme.aut.android.enduranceoagb.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.AllTeams
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.databinding.NewDriverFragmentBinding
import java.util.*


class NewDriverFragment : DialogFragment() {
    interface NewDriverListener {
        fun onDriverCreated(nameDriver: String, weightDriver: Double?)
        fun onDriverNotCreated()
        fun teamName() : String?
        fun driversList() : MutableList<String>
    }

    private lateinit var listener: NewDriverListener

    private lateinit var binding: NewDriverFragmentBinding

    val selectOne = "-- Válassz egyet! --"


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewDriverListener
            ?: throw RuntimeException("Activity must implement the NewDriverListener interface!")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = NewDriverFragmentBinding.inflate(LayoutInflater.from(context))

        val team = listener.teamName().toString()

        val driversListFragment = listener.driversList()

        binding.tvNameTeam.text = "$team"

        binding.spNewNameDriver.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            driversListFragment
        )

        return AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            .setTitle(R.string.newDriver)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok) { _, _ ->
                if (isValid()) {
                    val driver: String = binding.spNewNameDriver.selectedItem.toString()
                    val weight: Double? = binding.etNewWeightDriver.text.toString().toDoubleOrNull()

                    listener.onDriverCreated(driver, weight)
                }
                else if (!isValid()){
                    listener.onDriverNotCreated()
                }
            }
            .setNegativeButton(R.string.button_megse, null)
            .create()
    }

    companion object {
        const val TAG = "NewDriverFragment"
    }


    private fun isValid() = binding.spNewNameDriver.selectedItem.toString() != selectOne

}
