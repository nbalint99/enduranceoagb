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
import hu.bme.aut.android.enduranceoagb.databinding.NewStintFragmentBinding


class NewQualiStintFragment : DialogFragment() {
    interface NewQualiStintListener {
        fun onStintCreated(teamName: String, driver: String, stintNumber: Int, shortTeamName: String?, weight: Double, info: String? = null, kartNumber: Int, raceIdpass: String? = null)
        fun onStintNotCreated()
        fun raceId() : String?
    }

    private lateinit var listener: NewQualiStintListener

    private lateinit var binding: NewStintFragmentBinding

    private lateinit var dbRef: DatabaseReference

    private val plusWeight: MutableList<String> = arrayListOf("-", "2,5 kg", "5 kg", "7,5 kg", "10 kg", "12,5 kg", "15 kg", "17,5 kg", "20 kg", "22,5 kg", "25 kg", "27,5 kg", "30 kg", "Egyéb")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewQualiStintListener
            ?: throw RuntimeException("Activity must implement the NewQualiStintListener interface!")
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = NewStintFragmentBinding.inflate(LayoutInflater.from(context))

        val raceId = listener.raceId().toString()

        binding.spWeight.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            plusWeight
        )

        val dataPassedStint: String? = arguments?.getString("stint")
        val dataPassedTeamName: String? = arguments?.getString("teamName")
        val dataPassedShortTeamName: String? = arguments?.getString("shortTeamName")

        binding.tvNameTeamStint.text = dataPassedTeamName

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId)

        val items: MutableList<String> = mutableListOf()

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


                val kartNum = p0.result.child("Teams").child(dataPassedTeamName.toString()).child("Info").child("startKartNumber").value.toString()
                binding.etKartNumberStint.setText(kartNum)
            }
        }


        binding.tvNumberOfStintNew.text = dataPassedStint




        return AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth)
            .setTitle(R.string.newQuali)
            .setView(binding.root)
            .setPositiveButton(R.string.button_ok) { _, _ ->
                if (isValid() && isValidWeight()) {
                    val itemId = binding.spWeight.selectedItemId.toInt()
                    val selectedWeight = selectWeight(itemId)
                    val info = binding.etStintInfo.text.toString()
                    //val selectedDriver: String = binding.spPeople.selectedItem.toString()
                    val kartNumber: Int = binding.etKartNumberStint.text.toString().toInt()

                    val selectedDriver: String = binding.spPeople.selectedItem.toString()

                    if (selectedWeight == 100.0) {
                        val dialogBuilder: androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(
                            requireContext(),
                            android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                        )

                        val inflater = this.layoutInflater
                        val dialogView: View =
                            inflater.inflate(R.layout.special_weight_fragment, null)
                        dialogBuilder.setView(dialogView)
                        dialogBuilder.setTitle(R.string.specialWeight)

                        val specialWeight = dialogView.findViewById<EditText>(R.id.etSpecialWeight)

                        dialogBuilder.setPositiveButton(R.string.button_ok) { _, _ ->
                            if (specialWeight.text.toString()
                                    .isNotEmpty()
                            ) {
                                listener.onStintCreated(
                                    dataPassedTeamName.toString(),
                                    selectedDriver,
                                    dataPassedStint.toString().toInt(),
                                    dataPassedShortTeamName,
                                    specialWeight.text.toString().toDouble(),
                                    info,
                                    kartNumber,
                                    raceId
                                )
                            } else {
                                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                    .setTitle(R.string.warning)
                                    .setMessage(R.string.validSpecial)
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
                        listener.onStintCreated(
                            dataPassedTeamName.toString(),
                            selectedDriver,
                            dataPassedStint.toString().toInt(),
                            dataPassedShortTeamName,
                            selectedWeight,
                            info,
                            kartNumber,
                            raceId
                        )
                    }
                }
                else if (!isValid() || !isValidWeight()){
                    listener.onStintNotCreated()
                }
            }
            .setNegativeButton(R.string.button_megse, null)
            .create()
    }

    companion object {

        @JvmStatic //This can be avoided if you are in a complete Kotlin project
        fun newInstance(
            stint: String,
            teamName: String,
            shortTeamName: String?
        ): NewQualiStintFragment {
            val args = Bundle()
            args.putString("stint", stint)
            args.putString("teamName", teamName)
            args.putString("shortTeamName", shortTeamName)
            val fragment = NewQualiStintFragment()
            fragment.arguments = args
            return fragment
        }

    }

    private fun selectWeight(itemId: Int) : Double {
        when (itemId) {
            0 -> {
                return 0.0
            }
            1 -> {
                return 2.5
            }
            2 -> {
                return 5.0
            }
            3 -> {
                return 7.5
            }
            4 -> {
                return 10.0
            }
            5 -> {
                return 12.5
            }
            6 -> {
                return 15.0
            }
            7 -> {
                return 17.5
            }
            8 -> {
                return 20.0
            }
            9 -> {
                return 22.5
            }
            10 -> {
                return 25.0
            }
            11 -> {
                return 27.5
            }
            12 -> {
                return 30.0
            }
            13 -> {
                return 100.0
            }
            else -> {
                return 200.0
            }
        }

    }



    private fun isValid() = !binding.spPeople.isSelected
    private fun isValidWeight() = !binding.spWeight.isSelected

}
