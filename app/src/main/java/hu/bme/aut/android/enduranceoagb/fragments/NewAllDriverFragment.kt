package hu.bme.aut.android.enduranceoagb.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.databinding.NewAlldriverFragmentBinding
import hu.bme.aut.android.enduranceoagb.databinding.NewDriverFragmentBinding


class NewAllDriverFragment : DialogFragment() {
    interface NewAllDriverListener {
        fun onDriverCreated(nameDriver: String)
        fun onDriverNotCreated()
        fun teamName() : String?
    }

    private lateinit var listener: NewAllDriverListener

    private lateinit var binding: NewAlldriverFragmentBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewAllDriverListener
            ?: throw RuntimeException("Activity must implement the NewAllDriverListener interface!")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = NewAlldriverFragmentBinding.inflate(LayoutInflater.from(context))

        val team = listener.teamName().toString()

        binding.tvNameAllTeam.text = "$team"

        return AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            .setTitle(R.string.newDriver)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok) { _, _ ->
                if (isValid()) {
                    val driver: String = binding.etNewNameAllDriver.text.toString()

                    listener.onDriverCreated(driver)
                }
                else if (!isValid()){
                    listener.onDriverNotCreated()
                }
            }
            .setNegativeButton(R.string.button_megse, null)
            .create()
    }

    companion object {
        const val TAG = "NewAllDriverFragment"
    }


    private fun isValid() = binding.etNewNameAllDriver.text.isNotEmpty()

}
