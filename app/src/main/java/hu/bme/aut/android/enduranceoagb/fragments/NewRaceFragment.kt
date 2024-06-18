package hu.bme.aut.android.enduranceoagb.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.room.ColumnInfo
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.databinding.NewRaceFragmentBinding
import java.text.SimpleDateFormat
import java.util.*


class NewRaceFragment : DialogFragment() {
    interface NewRaceListener {
        fun onRaceCreated(newItem: Races)
        fun onRaceNotCreated()
    }

    private lateinit var listener: NewRaceListener

    private lateinit var binding: NewRaceFragmentBinding

    private lateinit var dbRef: DatabaseReference

    private val cal = Calendar.getInstance()

    private val year2 = cal.get(Calendar.YEAR).toString()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewRaceListener
            ?: throw RuntimeException("Activity must implement the NewRaceListener interface!")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = NewRaceFragmentBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            .setTitle(R.string.newRace)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok) { _, _ ->
                if (isValid() && isValidLocation() && isValidTeams()) {
                    listener.onRaceCreated(getRaceItem())

                    dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races")

                    val c = Calendar.getInstance()

                    val year = c.get(Calendar.YEAR)

                    dbRef.child(dbRef.push().key + ": " + year + " - " + getRaceItem().location).child("Info").setValue(getRaceItem())

                    val intent = requireActivity().intent
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                or Intent.FLAG_ACTIVITY_NO_ANIMATION
                    )
                    requireActivity().overridePendingTransition(0, 0)
                    requireActivity().finish()

                    requireActivity().overridePendingTransition(0, 0)
                    startActivity(intent)

                }
                else if (!isValid() || !isValidLocation() || !isValidTeams()){
                    listener.onRaceNotCreated()
                }
            }
            .setNegativeButton(R.string.button_megse, null)
            .create()
    }

    companion object {
        const val TAG = "NewRaceFragment"
    }


    private fun isValid() = binding.etNameRace.text.isNotEmpty()
    private fun isValidLocation() = binding.etNewLocation.text.isNotEmpty()
    private fun isValidTeams() : Boolean {
        return binding.etNewRaceTeams.text.isNotEmpty() && binding.etNewRaceTeams.text.toString().toInt() in 5..14
    }


    private fun getRaceItem() = Races(
        nameR = year2 + " - " + binding.etNameRace.text.toString() + ". verseny",
        location = binding.etNewLocation.text.toString(),
        numberOfTeams = binding.etNewRaceTeams.text.toString().toInt(),
        allStintNumber = numberOfStints(binding.etNewRaceTeams.text.toString().toInt()),
        hasStintReady = false,
        hasRaceDone = false,
        petrolDone = false,
        hasTeamsDone = 0,
        hasResultsDone = false,
        hasQualiDone = 0,
        numberOfRace = binding.etNameRace.text.toString().toInt(),
        hasGroupDone = false,
        hasTeamsCreated = false,
        hasFinalTeamsCreated = false
    )

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
