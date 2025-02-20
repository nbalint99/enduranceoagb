package hu.bme.aut.android.enduranceoagb.fragments

import android.accounts.AccountManager
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.script.Script
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.script.model.ExecutionRequest
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.script.ScriptScopes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.RaceActivity
import hu.bme.aut.android.enduranceoagb.adapter.RaceAdapter
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.databinding.RaceleftfragmentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class RaceLeftFragment : Fragment(), RaceAdapter.RaceItemClickListener, NewRaceFragment.NewRaceListener {
    private lateinit var rvAdapter: RaceAdapter

    private lateinit var dbRef: DatabaseReference

    private lateinit var dbRef2: DatabaseReference

    //private lateinit var credential: GoogleAccountCredential

    //private val REQUEST_AUTHORIZATION = 1001

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

        //dbRef.keepSynced(true)

        val items : MutableList<Races>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.children) {
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

        dbRef2 = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("/")

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
                        dbRef2.get().addOnCompleteListener { p1 ->
                            if (p1.isSuccessful) {
                                val numberOfRace = race?.numberOfRace.toString() + ". futam"
                                val venue = race?.location
                                val title = "Endurance OAGB - $numberOfRace - $venue"
                                val raceKey = race?.id_r.toString()
                                val numberOfTeams = race?.numberOfTeams
                                val numberOfStints = race?.allStintNumber
                                dbRef2.child("endTime").child("info").child("title").setValue(title)
                                dbRef2.child("endTime").child("raceTime").child("serverTime").removeValue()
                                dbRef2.child("raceInfo").child("raceKey").setValue(raceKey)
                                dbRef2.child("raceInfo").child("numberOfTeams").setValue(numberOfTeams)
                                dbRef2.child("raceInfo").child("numberOfStints").setValue(numberOfStints)
                            }
                        }
                        //executeScriptFunction()
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

    /*private fun authenticateAndRunScript() {
        // Hitelesítési folyamat inicializálása
        credential = GoogleAccountCredential.usingOAuth2(
            requireContext(),
            listOf(SCOPES) // A szükséges scope-ok
        )

        // Indítsd el a hitelesítést
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ACCOUNT_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (accountName != null) {
                credential.selectedAccountName = accountName
                executeScriptFunction()
            }
        }
    }*/

    /*private fun executeScriptFunction() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val account = GoogleSignIn.getLastSignedInAccount(requireContext())
                val accountName = account?.email ?: ""

                val SCOPES = listOf(SheetsScopes.SPREADSHEETS, ScriptScopes.SCRIPT_PROJECTS)
                val scriptID = "1Hul6lMBvGu-K-Q6MH3JaVB7s6H7nZkIh6KFFquoHFTEKiUVv8GS-NGBL"
                // Felhasználói hitelesítés a GoogleAccountCredential használatával
                val credential = GoogleAccountCredential.usingOAuth2(
                    context,
                    //listOf("https://www.googleapis.com/auth/script.projects")
                    SCOPES
                ).setSelectedAccountName(accountName)

                // NetHttpTransport használata az AndroidHttp helyett
                val scriptService = Script.Builder(
                    NetHttpTransport(), // NetHttpTransport, nem AndroidHttp
                    JacksonFactory.getDefaultInstance(),
                    credential
                ).setApplicationName("Endurance OAGB").build()

                val request = ExecutionRequest().apply {
                    function = "createTriggers" // A Google Script függvény neve
                }

                /*
                // Script futtatása
                scriptService.scripts().run("1Hul6lMBvGu-K-Q6MH3JaVB7s6H7nZkIh6KFFquoHFTEKiUVv8GS-NGBL", request).execute().apply {
                    // Eredmény kezelése
                    Log.d("Script Result", "Result: ${this?.toString()}")
                }*/

                //try {
                    // Token megszerzése
                    val token = credential.token
                    // Itt folytathatod a script futtatást, ha a token sikeresen megszerezve
                    val response = scriptService.scripts().run(scriptID, request).execute()
                    // Kezelheted a response-t
                    if (response.error != null) {
                        val errorDetails = response.error.details
                        Log.e("Script Error", "Error details: ${errorDetails}")
                    } else {
                        Log.d("Script Result", "Result: ${response.response}")
                    }
                    println("token: $token")
                } catch (e: UserRecoverableAuthException) {
                    // Ha a felhasználónak beleegyezést kell adnia, kezeljük ezt az eseményt
                    startActivityForResult(e.intent, REQUEST_AUTHORIZATION)
                } catch (e: GoogleAuthException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                println("before response")
                //val response = scriptService.scripts().run("1Hul6lMBvGu-K-Q6MH3JaVB7s6H7nZkIh6KFFquoHFTEKiUVv8GS-NGBL", request).execute()

                println("response")

                //if (response.error != null) {
                //    val errorDetails = response.error.details
                //    Log.e("Script Error", "Error details: ${errorDetails}")
                //} else {
                //    Log.d("Script Result", "Result: ${response.response}")
                //}

            //} catch (e: Exception) {
                // Hibakezelés
                //Log.e("Script Error", "Error executing script: ${e.message}")
            //}
        }
    }*/


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
                            //val together = p0.result.child(key).child("Info").child("allTeamTogether").value.toString().toBooleanStrictOrNull()
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
            10 -> 11
            11 -> 12
            12 -> 7
            13 -> 8
            14 -> 8
            else -> 0
        }
    }


}