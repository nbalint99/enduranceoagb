package hu.bme.aut.android.enduranceoagb.fragments


import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.DetailsStintWatchActivity
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.databinding.NewBoxFragmentBinding
import hu.bme.aut.android.enduranceoagb.databinding.NewStintFragmentBinding


class NewBoxFragment : DialogFragment() {
    interface NewBoxListener {
        fun onBoxCreated(raceIdBox: String, teamName: String, teamNumber: Int, time: Double, stint: Int, activity: String)
        fun onBoxNotCreated()
        fun raceId() : String?
    }

    private lateinit var listener: NewBoxListener

    private lateinit var binding: NewBoxFragmentBinding

    private lateinit var dbRef: DatabaseReference

    //private val plusWeight: MutableList<String> = arrayListOf("-", "2,5 kg", "5 kg", "7,5 kg", "10 kg", "12,5 kg", "15 kg", "17,5 kg", "20 kg", "22,5 kg", "25 kg", "27,5 kg", "30 kg", "Egyéb")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewBoxListener
            ?: throw RuntimeException("Activity must implement the NewBoxListener interface!")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = NewBoxFragmentBinding.inflate(LayoutInflater.from(context))

        val dataPassed: String? = arguments?.getString("position")
        val dataPassedStint: String? = arguments?.getString("stint")
        val dataPassedTeamName: String? = arguments?.getString("teamName")
        val dataPassedTeamId: String? = arguments?.getString("teamId")
        val dataPassedStintDone: String? = arguments?.getString("stintDone")
        val dataPassedRaceId: String? = arguments?.getString("raceIdBox")
        val dataPassedActivity: String? = arguments?.getString("activity")

        binding.tvNameTeamBox.text = dataPassedTeamName

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(dataPassedRaceId.toString())

        /*val items: MutableList<String> = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.child("Teams").child(dataPassedTeamName.toString()).child("Drivers").children) {
                    val addDriver = element.child("nameDriver").value.toString()
                    items.add(addDriver)
                }

                binding.spPeople.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    items
                )

                if (dataPassedStint.toString().toInt() == 1) {
                    val kartNum = p0.result.child("Teams").child(dataPassedTeamName.toString()).child("Info").child("startKartNumber").value.toString()
                    binding.etKartNumberStint.setText(kartNum)
                }
                else if (dataPassedStint.toString().toInt() > 1) {
                    val change = "Etap: " + (dataPassedStint.toString().toInt()-1)
                    if (dataPassedTeamId.toString().toInt() == 1) {
                        val stintTeam = (dataPassedStint.toString().toInt()-1).toString() + "-" + "box"
                        val kartNum = p0.result.child("Stints").child(change).child("Info").child(stintTeam).child("kartNumber").value.toString()
                        binding.etKartNumberStint.setText(kartNum)
                    }
                    else if (dataPassedTeamId.toString().toInt() > 1) {
                        val stintTeam = (dataPassedStint.toString().toInt()-1).toString() + "-" + (dataPassedTeamId.toString().toInt()-1).toString()
                        val kartNum = p0.result.child("Stints").child(change).child("Info").child(stintTeam).child("kartNumber").value.toString()
                        binding.etKartNumberStint.setText(kartNum)
                    }

                }

            }
        }*/

        return AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            .setTitle(R.string.changeTime)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok) { _, _ ->
                if (isValid()) {
                    val minBox = binding.etChangeTimeMinBox.text.toString().toDouble()
                    val secBox = binding.etChangeTimeSecBox.text.toString().toDouble()
                    val totalChangeTime = (minBox * 60000) + (secBox * 1000)
                    listener.onBoxCreated(dataPassedRaceId.toString(), dataPassedTeamName.toString(), dataPassedTeamId.toString().toInt(), totalChangeTime, dataPassedStint.toString().toInt(), dataPassedActivity.toString())
                }
                else if (!isValid()){
                    listener.onBoxNotCreated()
                }
            }
            .setNegativeButton(R.string.button_megse, null)
            .create()
    }

    companion object {
        @JvmStatic //This can be avoided if you are in a complete Kotlin project
        fun newInstance(
            raceIdBox: String,
            position: String,
            stint: String,
            teamName: String,
            teamId: String,
            stintDone: String,
            activity: String
        ): NewBoxFragment {
            val args = Bundle()
            args.putString("position", position)
            args.putString("stint", stint)
            args.putString("teamName", teamName)
            args.putString("teamId", teamId)
            args.putString("stintDone", stintDone)
            args.putString("raceIdBox", raceIdBox)
            args.putString("activity", activity)
            val fragment = NewBoxFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun isValid() = binding.etChangeTimeMinBox.text.toString() != "" && binding.etChangeTimeSecBox.text.toString() != ""
}
