package hu.bme.aut.android.enduranceoagb

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.adapter.DetailsStintAdapter
import hu.bme.aut.android.enduranceoagb.data.Drivers
import hu.bme.aut.android.enduranceoagb.data.Stint
import hu.bme.aut.android.enduranceoagb.data.Teams
import hu.bme.aut.android.enduranceoagb.databinding.ActivityDetailsstintBinding
import hu.bme.aut.android.enduranceoagb.fragments.NewStintFragment


class DetailsStintActivity : Fragment(), DetailsStintAdapter.DetailsStintItemClickListener, NewStintFragment.NewStintListener{
    private lateinit var binding: ActivityDetailsstintBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var adapter: DetailsStintAdapter

    private val CHANNEL_ID = "channel_id_01"
    private val notificationId = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = ActivityDetailsstintBinding.inflate(layoutInflater)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)

        binding.rvMainDetailsStint.setLayoutManager(layoutManager)

        adapter = DetailsStintAdapter(this)

        binding.rvMainDetailsStint.adapter = adapter

        val bundle = arguments
        val message = bundle?.getString("mText")

        val handler = Handler()

        if (message == null) {
            handler.postDelayed(Runnable { layoutManager.smoothScrollToPosition(binding.rvMainDetailsStint, null, 1) }, 100)
        }
        else {
            handler.postDelayed(Runnable { layoutManager.smoothScrollToPosition(binding.rvMainDetailsStint, null, message.toInt()) }, 100)
        }

        binding.rvMainDetailsStint.setItemViewCacheSize(16)

        createNotificationChannel()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()


        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val itemsTeams : MutableList<Teams>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                //var change = "Etap: $stintId"
                if (!p0.result.child("AllStint").child("numberOfStint").child(stintId.toString())
                        .child("hasDetailsStintReady").value.toString().toBoolean()
                ) {
                    for (element in p0.result.child("Teams").children) {
                        val addTeam = Teams(
                            element.child("Info").child("nameTeam").value.toString(),
                            element.child("Info").child("people").value.toString().toInt(),
                            element.child("Info").child("teamNumber").value.toString().toInt(),
                            element.child("Info").child("avgWeight").value.toString()
                                .toDoubleOrNull(),
                            element.child("Info").child("hasDriversDone").value.toString()
                                .toInt(),
                            element.child("Info").child("startKartNumber").value.toString().toInt(),
                            element.child("Info").child("hasQualiDone").value.toString().toBoolean(),
                            element.child("Info").child("stintsDone").value.toString().toIntOrNull(),
                            element.child("Info").child("gp2").value.toString().toBooleanStrictOrNull()
                        )

                        itemsTeams?.add(addTeam)
                        if (stintId.toString().toInt() in 1..9) {
                            val change = "Etap: 0$stintId"
                            dbRef.child("Cserék").child(change).child("1 - Versenyzők")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null2")
                            dbRef.child("Cserék").child(change).child("4 - Plusz súlyok")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null3")
                            dbRef.child("Cserék").child(change).child("2 - Összsúly")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null4")
                            dbRef.child("Cserék").child(change).child("3 - Gépszámok")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null4")
                        }
                        else {
                            val changeOrigi = "Etap: $stintId"
                            dbRef.child("Cserék").child(changeOrigi).child("1 - Versenyzők")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null2")
                            dbRef.child("Cserék").child(changeOrigi).child("4 - Plusz súlyok")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null3")
                            dbRef.child("Cserék").child(changeOrigi).child("2 - Összsúly")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null4")
                            dbRef.child("Cserék").child(changeOrigi).child("3 - Gépszámok")
                                .child(element.child("Info").child("teamNumber").value.toString()).setValue("null4")
                        }


                        val stint = Stint(element.child("Info").child("nameTeam").value.toString(),
                            element.child("Info").child("teamNumber").value.toString().toInt(),
                            null, stintId.toString().toInt(), null, null, null, false, null)

                        val teamStint = stintId + "-" + element.child("Info").child("teamNumber").value.toString().toInt()

                        val changePush = "Etap: $stintId"

                        dbRef.child("Stints").child(changePush).child("Info").child(teamStint).setValue(stint)
                    }
                    dbRef.child("AllStint").child("numberOfStint").child(stintId.toString())
                        .child("hasDetailsStintReady").setValue(true)
                }
                else {
                    for (element in p0.result.child("Teams").children) {
                        val addTeam = Teams(
                            element.child("Info").child("nameTeam").value.toString(),
                            element.child("Info").child("people").value.toString().toInt(),
                            element.child("Info").child("teamNumber").value.toString().toInt(),
                            element.child("Info").child("avgWeight").value.toString()
                                .toDoubleOrNull(),
                            element.child("Info").child("hasDriversDone").value.toString()
                                .toInt(),
                            element.child("Info").child("startKartNumber").value.toString().toInt(),
                            element.child("Info").child("hasQualiDone").value.toString().toBoolean(),
                            element.child("Info").child("stintsDone").value.toString().toIntOrNull(),
                            element.child("Info").child("gp2").value.toString().toBooleanStrictOrNull()

                        )
                        itemsTeams?.add(addTeam)
                    }
                }

                loadBackgroundItems()

                requireActivity().runOnUiThread {
                    adapter.teams(itemsTeams!!.toMutableList())
                }

            }
        }
    }

    private fun loadBackgroundItems() {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val items : MutableList<Stint>? = mutableListOf()

        val itemsDrivers : MutableList<Drivers>? = mutableListOf()

        val change = "Etap: $stintId"

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {

                for (elem in p0.result.child("Drivers").children) {

                    val nameDriver = elem.child("nameDriver").value.toString()
                    val weight = elem.child("weight").value.toString().toDouble()

                    val addDriver = Drivers(nameDriver, weight)

                    itemsDrivers?.add(addDriver)
                }

                val numberOfTeam =
                    p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                for (iterator in 1..numberOfTeam) {
                    val stintTeam = stintId.toString() + "-" + iterator.toString()
                    val nameDriver = p0.result.child("Stints").child(change).child("Info")
                        .child(stintTeam).child("driverName").value.toString()
                    val plus = p0.result.child("Stints").child(change).child("Info")
                        .child(stintTeam).child("plusWeight").value.toString().toDoubleOrNull()

                    for (element in p0.result.child("Teams").children) {
                        val nameTeam = element.child("Info").child("nameTeam").value.toString()
                        val teamNumber =
                            element.child("Info").child("teamNumber").value.toString().toInt()
                        if (teamNumber == iterator) {
                            if (stintId.toString().toInt() == 1) {
                                val hasStintDone =
                                    p0.result.child("Stints").child(change).child("Info").children
                                for (ready in hasStintDone) {
                                    val hasStintD =
                                        ready.child("hasStintDone").value.toString().toBoolean()
                                    val teamNum = ready.child("teamNumber").value.toString().toIntOrNull()
                                    val stintNum =
                                        ready.child("numberStint").value.toString().toIntOrNull()
                                    val info = ready.child("info").value.toString()
                                    val kartNumber =
                                        ready.child("kartNumber").value.toString().toIntOrNull()
                                    val expectedKartNumber =
                                        p0.result.child("Teams").child(nameTeam).child("Info").child("startKartNumber").value.toString().toIntOrNull()

                                    if (teamNum == teamNumber && stintNum == stintId.toString()
                                            .toInt()
                                    ) {
                                        val addStint = Stint(
                                            nameTeam,
                                            teamNumber,
                                            nameDriver,
                                            stintId.toString().toInt(),
                                            plus,
                                            info,
                                            null,
                                            hasStintD, kartNumber, expectedKartNumber, null, null, null
                                        )
                                        items?.add(addStint)
                                    }
                                }
                            } else if (stintId.toString().toInt() > 1) {
                                val hasStintDone =
                                    p0.result.child("Stints").child(change).child("Info").children
                                for (ready in hasStintDone) {
                                    val hasStintD =
                                        ready.child("hasStintDone").value.toString().toBoolean()
                                    val teamNum = ready.child("teamNumber").value.toString().toIntOrNull()
                                    val stintNum =
                                        ready.child("numberStint").value.toString().toIntOrNull()
                                    val info = ready.child("info").value.toString()
                                    val changePrevStint = "Etap: " + (stintId.toString().toInt()-1).toString()
                                    val changePrev = (stintId.toString().toInt()-1).toString() + "-" + teamNum
                                    val prevInfo = p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("info").value.toString()
                                    val kartNumber =
                                        ready.child("kartNumber").value.toString().toIntOrNull()
                                    val prevKartNumber =
                                        p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("kartNumber").value.toString().toIntOrNull()
                                    val prevDriverName =
                                        p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("driverName").value.toString()
                                    val prevPlusWeight =
                                        p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("plusWeight").value.toString().toDoubleOrNull()
                                    if (teamNumber > 1) {
                                        if (teamNum.toString().toIntOrNull() != null) {
                                            val changePrevTeam = (stintId.toString()
                                                .toInt() - 1).toString() + "-" + (teamNum.toString()
                                                .toInt() - 1)
                                            val expectedKartNumber =
                                                p0.result.child("Stints").child(changePrevStint)
                                                    .child("Info").child(changePrevTeam)
                                                    .child("kartNumber").value.toString()
                                                    .toIntOrNull()

                                            if (teamNum == teamNumber && stintNum == stintId.toString()
                                                    .toInt()
                                            ) {
                                                val addStint = Stint(
                                                    nameTeam,
                                                    teamNumber,
                                                    nameDriver,
                                                    stintId.toString().toInt(),
                                                    plus,
                                                    info,
                                                    prevInfo,
                                                    hasStintD,
                                                    kartNumber, expectedKartNumber, prevDriverName, prevPlusWeight, prevKartNumber
                                                )
                                                items?.add(addStint)
                                            }
                                        } else {
                                            val changePrevTeam = (stintId.toString()
                                                .toInt() - 1).toString() + "-box"
                                            val expectedKartNumber =
                                                p0.result.child("Stints").child(changePrevStint)
                                                    .child("Info").child(changePrevTeam)
                                                    .child("kartNumber").value.toString()
                                                    .toIntOrNull()

                                            if (teamNum == teamNumber && stintNum == stintId.toString()
                                                    .toInt()
                                            ) {
                                                val addStint = Stint(
                                                    nameTeam,
                                                    teamNumber,
                                                    nameDriver,
                                                    stintId.toString().toInt(),
                                                    plus,
                                                    info,
                                                    prevInfo,
                                                    hasStintD,
                                                    kartNumber, expectedKartNumber, prevDriverName, prevPlusWeight, prevKartNumber
                                                )
                                                items?.add(addStint)
                                            }
                                        }

                                    } else if (teamNumber == 1) {
                                        val changePrevTeam = (stintId.toString()
                                            .toInt() - 1).toString() + "-" + "box"
                                        val expectedKartNumber =
                                            p0.result.child("Stints").child(changePrevStint)
                                                .child("Info").child(changePrevTeam)
                                                .child("kartNumber").value.toString()
                                                .toIntOrNull()


                                        if (teamNum == teamNumber && stintNum == stintId.toString()
                                                .toInt()
                                        ) {
                                            val addStint = Stint(
                                                nameTeam,
                                                teamNumber,
                                                nameDriver,
                                                stintId.toString().toInt(),
                                                plus,
                                                info,
                                                prevInfo,
                                                hasStintD,
                                                kartNumber, expectedKartNumber, prevDriverName, prevPlusWeight, prevKartNumber
                                            )
                                            items?.add(addStint)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                requireActivity().runOnUiThread {
                    adapter.update2(items!!.toMutableList())
                    adapter.drivers(itemsDrivers!!.toMutableList())
                }
            }
        }
    }

    private fun loadBackgroundItemsParam(raceIdpass: String?, stintIdpass: String?) {
        val raceId = raceIdpass.toString()
        val stintId = stintIdpass.toString()


        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val items : MutableList<Stint>? = mutableListOf()

        val itemsDrivers : MutableList<Drivers>? = mutableListOf()

        val change = "Etap: $stintId"

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {

                for (elem in p0.result.child("Drivers").children) {

                    val nameDriver = elem.child("nameDriver").value.toString()
                    val weight = elem.child("weight").value.toString().toDouble()

                    val addDriver = Drivers(nameDriver, weight)

                    itemsDrivers?.add(addDriver)
                }

                val numberOfTeam =
                    p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                for (iterator in 1..numberOfTeam) {
                    val stintTeam = stintId.toString() + "-" + iterator.toString()
                    val nameDriver = p0.result.child("Stints").child(change).child("Info")
                        .child(stintTeam).child("driverName").value.toString()
                    val plus = p0.result.child("Stints").child(change).child("Info")
                        .child(stintTeam).child("plusWeight").value.toString().toDoubleOrNull()

                    for (element in p0.result.child("Teams").children) {
                        val nameTeam = element.child("Info").child("nameTeam").value.toString()
                        val teamNumber =
                            element.child("Info").child("teamNumber").value.toString().toInt()
                        if (teamNumber == iterator) {
                            if (stintId.toString().toInt() == 1) {
                                val hasStintDone =
                                    p0.result.child("Stints").child(change).child("Info").children
                                for (ready in hasStintDone) {
                                    val hasStintD =
                                        ready.child("hasStintDone").value.toString().toBoolean()
                                    val teamNum = ready.child("teamNumber").value.toString().toIntOrNull()
                                    val stintNum =
                                        ready.child("numberStint").value.toString().toIntOrNull()
                                    val info = ready.child("info").value.toString()
                                    val kartNumber =
                                        ready.child("kartNumber").value.toString().toIntOrNull()
                                    val expectedKartNumber =
                                        p0.result.child("Teams").child(nameTeam).child("Info").child("startKartNumber").value.toString().toIntOrNull()

                                    if (teamNum == teamNumber && stintNum == stintId.toString()
                                            .toInt()
                                    ) {
                                        val addStint = Stint(
                                            nameTeam,
                                            teamNumber,
                                            nameDriver,
                                            stintId.toString().toInt(),
                                            plus,
                                            info,
                                            null,
                                            hasStintD, kartNumber, expectedKartNumber, null, null, null
                                        )
                                        items?.add(addStint)
                                    }
                                }
                            } else if (stintId.toString().toInt() > 1) {
                                val hasStintDone =
                                    p0.result.child("Stints").child(change).child("Info").children
                                for (ready in hasStintDone) {
                                    val hasStintD =
                                        ready.child("hasStintDone").value.toString().toBoolean()
                                    val teamNum = ready.child("teamNumber").value.toString().toIntOrNull()
                                    val stintNum =
                                        ready.child("numberStint").value.toString().toIntOrNull()
                                    val info = ready.child("info").value.toString()
                                    val changePrevStint = "Etap: " + (stintId.toString().toInt()-1).toString()
                                    val changePrev = (stintId.toString().toInt()-1).toString() + "-" + teamNum
                                    val prevInfo = p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("info").value.toString()
                                    val kartNumber =
                                        ready.child("kartNumber").value.toString().toIntOrNull()
                                    val prevKartNumber =
                                        p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("kartNumber").value.toString().toIntOrNull()
                                    val prevDriverName =
                                        p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("driverName").value.toString()
                                    val prevPlusWeight =
                                        p0.result.child("Stints").child(changePrevStint).child("Info").child(changePrev).child("plusWeight").value.toString().toDoubleOrNull()
                                    if (teamNumber > 1) {
                                        if (teamNum.toString().toIntOrNull() != null) {
                                            val changePrevTeam = (stintId.toString()
                                                .toInt() - 1).toString() + "-" + (teamNum.toString()
                                                .toInt() - 1)
                                            val expectedKartNumber =
                                                p0.result.child("Stints").child(changePrevStint)
                                                    .child("Info").child(changePrevTeam)
                                                    .child("kartNumber").value.toString()
                                                    .toIntOrNull()

                                            if (teamNum == teamNumber && stintNum == stintId.toString()
                                                    .toInt()
                                            ) {
                                                val addStint = Stint(
                                                    nameTeam,
                                                    teamNumber,
                                                    nameDriver,
                                                    stintId.toString().toInt(),
                                                    plus,
                                                    info,
                                                    prevInfo,
                                                    hasStintD,
                                                    kartNumber, expectedKartNumber, prevDriverName, prevPlusWeight, prevKartNumber
                                                )
                                                items?.add(addStint)
                                            }
                                        } else {
                                            val changePrevTeam = (stintId.toString()
                                                .toInt() - 1).toString() + "-box"
                                            val expectedKartNumber =
                                                p0.result.child("Stints").child(changePrevStint)
                                                    .child("Info").child(changePrevTeam)
                                                    .child("kartNumber").value.toString()
                                                    .toIntOrNull()

                                            if (teamNum == teamNumber && stintNum == stintId.toString()
                                                    .toInt()
                                            ) {
                                                val addStint = Stint(
                                                    nameTeam,
                                                    teamNumber,
                                                    nameDriver,
                                                    stintId.toString().toInt(),
                                                    plus,
                                                    info,
                                                    prevInfo,
                                                    hasStintD,
                                                    kartNumber, expectedKartNumber, prevDriverName, prevPlusWeight, prevKartNumber
                                                )
                                                items?.add(addStint)
                                            }
                                        }

                                    } else if (teamNumber == 1) {
                                        val changePrevTeam = (stintId.toString()
                                            .toInt() - 1).toString() + "-" + "box"
                                        val expectedKartNumber =
                                            p0.result.child("Stints").child(changePrevStint)
                                                .child("Info").child(changePrevTeam)
                                                .child("kartNumber").value.toString()
                                                .toIntOrNull()


                                        if (teamNum == teamNumber && stintNum == stintId.toString()
                                                .toInt()
                                        ) {
                                            val addStint = Stint(
                                                nameTeam,
                                                teamNumber,
                                                nameDriver,
                                                stintId.toString().toInt(),
                                                plus,
                                                info,
                                                prevInfo,
                                                hasStintD,
                                                kartNumber, expectedKartNumber, prevDriverName, prevPlusWeight, prevKartNumber
                                            )
                                            items?.add(addStint)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //if (isAdded) {
                    requireActivity().runOnUiThread {
                        adapter.update2(items!!.toMutableList())
                        adapter.drivers(itemsDrivers!!.toMutableList())
                    }
                //}

            }
        }
    }

    override fun onNewStintListener(position: Int, teamNumber: Int, teamName: String, stintDone: Boolean, driverName: String?, plusWeight: Double?) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId: String = activity?.getMyData().toString()
        val stintId: String = activity?.getMyDataStint().toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val change = "Etap: ${stintId.toString().toInt()+1}"
                val teamStint = "${stintId.toString().toInt()+1}-$teamNumber"
                if (p0.result.child("Stints").child(change).child("Info").child(teamStint).child("driverName").exists()) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Ezt az etapot már nem tudod módosítani!")

                    builder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.button_ok, null)
                    builder.show()
                }
                else if (stintDone) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Ezt az etapot egyszer már létrehoztad. Biztos, hogy módosítani szeretnéd?")

                    builder.setPositiveButton(hu.bme.aut.android.enduranceoagb.R.string.yes) { dialog, which ->
                        val fragment = NewStintFragment.newInstance(position.toString(), stintId.toString(), teamName, teamNumber.toString(), stintDone.toString(), driverName, plusWeight.toString())
                        fragment.show(requireActivity().supportFragmentManager, "NewStintFragment")
                    }
                    builder.setNeutralButton(hu.bme.aut.android.enduranceoagb.R.string.button_megse, null)
                    builder.show()
                }
                else {
                    val fragment = NewStintFragment.newInstance(
                        position.toString(),
                        stintId.toString(),
                        teamName,
                        teamNumber.toString(),
                        stintDone.toString(),
                        driverName,
                        plusWeight.toString()
                    )
                    fragment.show(requireActivity().supportFragmentManager, "NewStintFragment")
                }
            }


        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStintCreated(
        teamName: String,
        teamNumber: Int,
        driver: String,
        stintNumber: Int,
        weight: Double,
        info: String?,
        kartNumber: Int,
        expectedKartNumber: Int,
        driverName: String?,
        plusWeightDriver: String?,
        stintDonePrev: String,
        stintIdpass: String?,
        raceIdpass: String?
    ) {

        //val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId = raceIdpass.toString()
        val stintId = stintIdpass.toString()
        //val activityStint = activity as DetailsStintWatchActivity?

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                //var change = "Etap: $stintId"
                //val allStintNumberAll = p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                if (stintId.toString().toInt() == 1) {
                    val stint = Stint(
                        teamName,
                        teamNumber,
                        driver,
                        stintId.toString().toInt(),
                        weight,
                        info,
                        null,
                        true,
                        kartNumber,
                        expectedKartNumber
                    )
                    val teamStint = "$stintId-$teamNumber"
                    val change = "Etap: $stintId"
                    dbRef.child("Stints").child(change).child("Info").child(teamStint)
                        .setValue(stint)
                    val oriWeight =
                        p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                            .child("weight").value.toString().toDouble()
                    val sum = oriWeight + weight
                    dbRef.child("Teams").child(teamName).child("Info").child("avgWeight")
                        .setValue(sum)
                    dbRef.child("Teams").child(teamName).child("Info").child("stintsDone")
                        .setValue(stintNumber)

                    stintDoneCheck(raceIdpass, stintIdpass)

                    val numberOfTeams =
                        p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                    if (teamNumber == numberOfTeams) {
                        var changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                        val allStintNumber =
                            p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                        if ((stintId.toString().toInt() + 1) <= allStintNumber) {
                            val teamStintNext = "${stintId.toString().toInt() + 1}-box"
                            dbRef.child("Stints").child(changeNext).child("Info")
                                .child(teamStintNext).child("kartNumber").setValue(kartNumber)
                            if (stintId.toString().toInt() in 1..9) {
                                changeNext = "Etap: 0${stintId.toString().toInt() + 1}"
                                dbRef.child("Cserék").child(changeNext).child("5 - Parkol")
                                    .child("Parkol").setValue(kartNumber)
                            } else {
                                dbRef.child("Cserék").child(changeNext).child("5 - Parkol")
                                    .child("Parkol").setValue(kartNumber)
                            }
                        }
                    }
                } else if (stintId.toString().toInt() > 1) {
                    val changePrevStint = "Etap: " + (stintId.toString().toInt() - 1).toString()
                    val changePrev = (stintId.toString().toInt() - 1).toString() + "-" + teamNumber
                    val prevInfo = p0.result.child("Stints").child(changePrevStint).child("Info")
                        .child(changePrev).child("info").value.toString()

                    val stint = Stint(
                        teamName,
                        teamNumber,
                        driver,
                        stintId.toString().toInt(),
                        weight,
                        info,
                        prevInfo,
                        true,
                        kartNumber,
                        expectedKartNumber
                    )
                    val teamStint = "$stintId-$teamNumber"
                    if (stintDonePrev == "false") {
                        val change = "Etap: $stintId"
                        dbRef.child("Stints").child(change).child("Info").child(teamStint)
                            .setValue(stint)
                        val getWeight = p0.result.child("Teams").child(teamName).child("Info")
                            .child("avgWeight").value.toString().toDouble()
                        val oriWeight =
                            p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                                .child("weight").value.toString().toDouble()
                        val sum = oriWeight + weight + getWeight
                        dbRef.child("Teams").child(teamName).child("Info").child("avgWeight")
                            .setValue(sum)
                        dbRef.child("Teams").child(teamName).child("Info").child("stintsDone")
                            .setValue(stintNumber)

                        stintDoneCheck(raceIdpass, stintIdpass)
                    } else if (stintDonePrev == "true") {
                        val change = "Etap: $stintId"
                        val getWeight = p0.result.child("Teams").child(teamName).child("Info")
                            .child("avgWeight").value.toString().toDouble()

                        val prevDriverWeight =
                            p0.result.child("Teams").child(teamName).child("Drivers")
                                .child(driverName.toString()).child("weight").value.toString()
                                .toDouble()
                        val prevPlus =
                            p0.result.child("Stints").child(change).child("Info").child(teamStint)
                                .child("plusWeight").value.toString().toDouble()
                        val prevAll = prevDriverWeight + prevPlus
                        val allWeight = getWeight - prevAll
                        val oriWeight =
                            p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                                .child("weight").value.toString().toDouble()
                        val sum = oriWeight + weight + allWeight
                        dbRef.child("Teams").child(teamName).child("Info").child("avgWeight")
                            .setValue(sum)
                        dbRef.child("Stints").child(change).child("Info").child(teamStint)
                            .setValue(stint)
                        dbRef.child("Teams").child(teamName).child("Info").child("stintsDone")
                            .setValue(stintNumber)
                    }

                    val numberOfTeams =
                        p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                    if (teamNumber == numberOfTeams) {
                        val allStintNumber =
                            p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                        if ((stintId.toString().toInt() + 1) <= allStintNumber) {
                            val teamStintNext = "${stintId.toString().toInt() + 1}-box"
                            val changeNext = "Etap: ${stintId.toString().toInt() + 1}"
                            dbRef.child("Stints").child(changeNext).child("Info")
                                .child(teamStintNext).child("kartNumber").setValue(kartNumber)
                            if (stintId.toString().toInt() in 1..8) {
                                val changeNextZero = "Etap: 0${stintId.toString().toInt() + 1}"
                                dbRef.child("Cserék").child(changeNextZero).child("5 - Parkol")
                                    .child("Parkol").setValue(kartNumber)
                            } else {
                                dbRef.child("Cserék").child(changeNext).child("5 - Parkol")
                                    .child("Parkol").setValue(kartNumber)
                            }
                        }
                    }


                }

                if (stintId.toString().toInt() in 1..9) {
                    val change = "Etap: 0$stintId"
                    dbRef.child("Cserék").child(change).child("1 - Versenyzők")
                        .child(teamNumber.toString()).setValue(driver)
                    if (weight == 0.0) {
                        dbRef.child("Cserék").child(change).child("4 - Plusz súlyok")
                            .child(teamNumber.toString()).setValue("-")
                    } else {
                        val weightString = weight.toString()
                        val output = weightString.replace('.', ',')
                        dbRef.child("Cserék").child(change).child("4 - Plusz súlyok")
                            .child(teamNumber.toString()).setValue(output)
                    }
                    val originalWeight =
                        p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                            .child("weight").value.toString().toDouble()
                    val sumWeight = originalWeight + weight
                    val sumWeightStr = sumWeight.toString()
                    val outputSumWeight = sumWeightStr.replace('.', ',')
                    dbRef.child("Cserék").child(change).child("2 - Összsúly")
                        .child(teamNumber.toString()).setValue(outputSumWeight)
                    dbRef.child("Cserék").child(change).child("3 - Gépszámok")
                        .child(teamNumber.toString()).setValue(kartNumber)
                } else {
                    val changeUp = "Etap: $stintId"
                    dbRef.child("Cserék").child(changeUp).child("1 - Versenyzők")
                        .child(teamNumber.toString()).setValue(driver)
                    if (weight == 0.0) {
                        dbRef.child("Cserék").child(changeUp).child("4 - Plusz súlyok")
                            .child(teamNumber.toString()).setValue("-")
                    } else {
                        val weightString = weight.toString()
                        val output = weightString.replace('.', ',')
                        dbRef.child("Cserék").child(changeUp).child("4 - Plusz súlyok")
                            .child(teamNumber.toString()).setValue(output)
                    }
                    val originalWeight =
                        p0.result.child("Teams").child(teamName).child("Drivers").child(driver)
                            .child("weight").value.toString().toDouble()
                    val sumWeight = originalWeight + weight
                    val sumWeightStr = sumWeight.toString()
                    val outputSumWeight = sumWeightStr.replace('.', ',')
                    dbRef.child("Cserék").child(changeUp).child("2 - Összsúly")
                        .child(teamNumber.toString()).setValue(outputSumWeight)
                    dbRef.child("Cserék").child(changeUp).child("3 - Gépszámok")
                        .child(teamNumber.toString()).setValue(kartNumber)
                }

            }
        }
    }

    private fun stintDoneCheck(raceIdpass: String?, stintIdpass: String?) {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        val raceId = raceIdpass.toString()
        val stintId = stintIdpass.toString()

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val change = "Etap: $stintId"

                val numberOfTeams =
                    p0.result.child("Info").child("numberOfTeams").value.toString().toInt()
                var done = 0
                for (i in 1..numberOfTeams) {
                    val stint = "$stintId-$i"
                    val hasStintDone =
                        p0.result.child("Stints").child(change).child("Info").child(stint)
                            .child("hasStintDone").value.toString().toBoolean()
                    if (hasStintDone) {
                        done += 1
                    } else if (!hasStintDone) {
                        //Ez nem
                    }
                }
                if (done == numberOfTeams) {
                    dbRef.child("AllStint").child("numberOfStint").child(stintId.toString())
                        .child("hasStintDone").setValue(true)
                    val numberOfStints =
                        p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                    if (stintId.toString().toInt() == numberOfStints) {
                        dbRef.child("Info").child("hasRaceDone").setValue(true)
                    }

                    val petrolDone = p0.result.child("Info").child("petrolDone").value.toString().toBoolean()
                    if(!petrolDone) {
                        val allStint = p0.result.child("Info").child("allStintNumber").value.toString().toInt()
                        var stintPetrol = 0
                        for (element in 1..allStint) {
                            val hasStintDone = p0.result.child("AllStint").child("numberOfStint").child(element.toString()).child("hasStintDone").value.toString().toBoolean()

                            if (hasStintDone) {
                                stintPetrol += 1
                            }
                        }
                        if ((allStint.toDouble() / 2.0) == (stintPetrol.toDouble() + 1.0) || (allStint.toDouble() / 2.0) == (stintPetrol + 0.5)) {
                            sendNotification()
                            dbRef.child("Info").child("petrolDone").setValue(true)
                        }
                    }

                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Endurance OAGB"
            val descriptonText = "A következő cserénél tankolni kell!"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptonText
            }
            val notificationManager: NotificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(hu.bme.aut.android.enduranceoagb.R.mipmap.endu_logo_round)
            .setContentTitle("Endurance OAGB")
            .setContentText("A következő cserénél tankolni kell!")
            .setAutoCancel(true)
            .setSound(soundUri)
        with(NotificationManagerCompat.from(requireContext())) {
            notify(notificationId, notificationBuilder.build())
        }
    }

    override fun onStintNotCreated() {
        val snack = Snackbar.make(binding.root, hu.bme.aut.android.enduranceoagb.R.string.notAddDriver, Snackbar.LENGTH_LONG)
        snack.show()
    }

    override fun raceId(): String? {
        val activity: DetailsStintWatchActivity? = activity as DetailsStintWatchActivity?
        return activity?.getMyData().toString()
    }
}