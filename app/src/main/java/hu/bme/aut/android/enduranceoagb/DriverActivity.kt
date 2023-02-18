package hu.bme.aut.android.enduranceoagb

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import hu.bme.aut.android.enduranceoagb.adapter.DriverAdapter
import hu.bme.aut.android.enduranceoagb.data.*
import hu.bme.aut.android.enduranceoagb.databinding.ActivityDriverBinding
import hu.bme.aut.android.enduranceoagb.fragments.NewDriverFragment
import java.util.*


class DriverActivity : AppCompatActivity(), DriverAdapter.DriverItemClickListener, NewDriverFragment.NewDriverListener {
    private lateinit var binding: ActivityDriverBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var dbRef2: DatabaseReference

    private lateinit var adapter: DriverAdapter

    companion object {
        const val EXTRA_TEAM_MEMBERS = "extra.team_members"
        const val EXTRA_RACE_NAME = "extra.race_name"
        const val EXTRA_TEAM_NUMBER = "extra.team_number"
        const val EXTRA_NAMETEAM= "extra.nameteam"
        const val EXTRA_GP2 = "extra.gp2"
    }

    private var raceId: String? = null
    private var teamId: String? = null
    private var teamName: String? = null
    private var gp2: String? = null
    private var teamMembers: String? = null
    private var membersList: MutableList<String>? = mutableListOf()
    private var sendMembersList: MutableList<String>? = mutableListOf()

    private val c = Calendar.getInstance()

    private val year = c.get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)
        teamId = intent.getStringExtra(EXTRA_TEAM_NUMBER)
        teamName = intent.getStringExtra(EXTRA_NAMETEAM)
        gp2 = intent.getStringExtra(EXTRA_GP2)
        teamMembers = intent.getStringExtra(EXTRA_TEAM_MEMBERS)
        membersList = teamMembers?.split(",")?.map { it.trim() } as MutableList<String>




        if (gp2 == "true") {
            binding.tvNameTeamDriverActivity.text = "Csapatnév: $teamName (GP2)"
        }
        else {
            binding.tvNameTeamDriverActivity.text = "Csapatnév: $teamName"
        }

        initRecyclerView()

        binding.fab.setOnClickListener {
            dbRef =
                FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                    .getReference("Races").child(raceId.toString()).child("Teams").child(teamName.toString())

            val items: MutableList<Drivers>? = mutableListOf()

            sendMembersList!!.clear()

            dbRef.get().addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    for (element in p0.result.child("Drivers").children) {
                        val addDriver = Drivers(
                            element.child("nameDriver").value.toString(),
                            element.child("weight").value.toString().toDoubleOrNull(),
                            element.child("races").value.toString().toIntOrNull(),
                            element.child("joker").value.toString().toBooleanStrictOrNull()
                        )

                        items?.add(addDriver)
                    }


                    val selectOne = "-- Válassz egyet! --"
                    if (sendMembersList != null && membersList != null) {
                        sendMembersList!!.add(0, selectOne)

                        for (element in membersList!!) {
                            var isExisted = false
                            if (items != null) {
                                for (i in items) {
                                    val existed = i.nameDriver
                                    if (element == existed) {
                                        isExisted = true
                                    }
                                }
                            }
                            if (!isExisted) {
                                sendMembersList!!.add(element)
                            }
                        }
                    }


                    val people = p0.result.child("Info").child("people").value.toString().toInt()
                    val missingDrivers = people - items?.size!!

                    if (missingDrivers != 0) {
                        NewDriverFragment().show(
                            supportFragmentManager,
                            NewDriverFragment.TAG
                        )
                    } else {
                        val snack = Snackbar.make(
                            binding.root,
                            R.string.doneAddDriver,
                            Snackbar.LENGTH_LONG
                        )
                        snack.show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.menu_home) {
            val myIntent = Intent(this@DriverActivity, MainActivity::class.java)

            startActivity(myIntent)
            return true
        }
        if (id == R.id.menu_etc) {
            val myIntent = Intent(this@DriverActivity, RaceActivity::class.java)
            myIntent.putExtra("extra.race_name", raceId)

            startActivity(myIntent)
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    private fun initRecyclerView() {

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        binding.rvMainDriver.setLayoutManager(layoutManager)

        adapter = DriverAdapter(this)

        binding.rvMainDriver.adapter = adapter

        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString()).child("Teams").child(teamName.toString())

        val items : MutableList<Drivers>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.child("Drivers").children) {
                    val addDriver = Drivers(
                        element.child("nameDriver").value.toString(),
                        element.child("weight").value.toString().toDoubleOrNull(),
                        element.child("races").value.toString().toIntOrNull(),
                        element.child("joker").value.toString().toBooleanStrictOrNull()
                    )
                    items?.add(addDriver)

                }
                runOnUiThread {
                    if (items != null) {
                        adapter.update2(items)
                    }
                }
                val people = p0.result.child("Info").child("people").value.toString().toInt()
                val missingDrivers = people - items?.size!!
                if (missingDrivers != 0) {
                    val snack = Snackbar.make(
                        binding.root,
                        "Még $missingDrivers versenyzőt fel kell venned!",
                        Snackbar.LENGTH_LONG
                    )
                    snack.show()
                }
            }
        }
    }

    override fun onDriverCreated(driverName: String, weight: Double?) {
        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        val newItem = Drivers(driverName, weight)
        dbRef.child("Teams").child(teamName.toString()).child("Drivers").child(driverName).setValue(newItem)
        dbRef.child("Drivers").child(dbRef.push().key.toString()).setValue(newItem)
        if (weight != null) {
            dbRef.child("Teams").child(teamName.toString()).child("Info").child("hasDriversDone").setValue(ServerValue.increment(1))
        }

        dbRef2 =
            FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                .getReference(year.toString())
        dbRef2.child("Teams").child(teamName.toString()).child("Drivers").child(driverName).child("races").setValue(ServerValue.increment(1))
        dbRef2.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val racesDriver = p0.result.child("Teams").child(teamName.toString()).child("Drivers").child(driverName).child("races").value.toString().toInt()
                if (racesDriver == 2) {
                    dbRef2.child("Teams").child(teamName.toString()).child("joker").setValue(ServerValue.increment(1))
                }
            }
        }

        dbRef2.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val jokers = p0.result.child("Teams").child(teamName.toString()).child("joker").value.toString().toInt()
                if (jokers == 4) {
                    for (element in p0.result.child("Teams").child(teamName.toString()).child("Drivers").children) {
                        val raceNumberByDriver = element.child("races").value.toString().toInt()
                        if (raceNumberByDriver == 0 || raceNumberByDriver == 1) {
                            val driverName = element.child("nameDriver").value.toString()
                            dbRef2.child("Teams").child(teamName.toString()).child("Drivers").child(driverName).child("joker").setValue(true)
                        }
                    }
                }
            }
        }

        val items: MutableList<Drivers>? = mutableListOf()

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                for (element in p0.result.child("Teams").child(teamName.toString()).child("Drivers").children) {
                    val addDriver = Drivers(
                        element.child("nameDriver").value.toString(),
                        element.child("weight").value.toString().toDoubleOrNull(),
                        element.child("races").value.toString().toIntOrNull(),
                        element.child("joker").value.toString().toBooleanStrictOrNull()
                    )

                    items?.add(addDriver)
                }

                val people = p0.result.child("Teams").child(teamName.toString()).child("Info").child("people").value.toString().toInt()
                val missingDrivers = people - items?.size!!

                val doneDrivers = p0.result.child("Teams").child(teamName.toString()).child("Info").child("hasDriversDone").value.toString().toInt()

                if (missingDrivers != 0) {
                    val snack = Snackbar.make(binding.root,"Még $missingDrivers versenyzőt fel kell venned!", Snackbar.LENGTH_LONG)
                    snack.show()
                }
                if (doneDrivers == people) {
                    dbRef.child("Info").child("hasTeamsDone").setValue(ServerValue.increment(1))
                    val snack = Snackbar.make(binding.root, R.string.addDriverDone, Snackbar.LENGTH_LONG)
                    snack.show()
                }
            }
        }

        runOnUiThread {
            adapter.addItem(newItem)
        }
    }

    override fun onDriverNotCreated() {
        val snack = Snackbar.make(binding.root, R.string.notAddDriver, Snackbar.LENGTH_LONG)
        snack.show()
    }

    override fun teamName(): String? {
        if (gp2 == "true") {
            return "$teamName (GP2)"
        }
        else {
            return teamName
        }
    }

    override fun driversList(): MutableList<String> {
        return if (sendMembersList != null) {
            sendMembersList!!
        } else {
            teamMembers?.split(",")?.map { it.trim() } as MutableList<String>
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

        val showDetailsIntent = Intent()
        showDetailsIntent.setClass(this@DriverActivity, TeamActivity::class.java)
        showDetailsIntent.putExtra(TeamActivity.EXTRA_RACE_NAME, raceId)
        startActivity(showDetailsIntent)
    }

    override fun onItemClick(nameDriver: String?, weight: String?) {
        val builder = AlertDialog.Builder(this)

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val initDone =
                    p0.result.child("Info").child("hasStintReady").value.toString().toBoolean()
                if (initDone) {
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Mostmár nem módosíthatod a versenyzőket, mivel már elkezdődött a verseny!")
                    builder.setPositiveButton(R.string.button_ok, null)
                    builder.show()

                } else {
                    val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(
                        this,
                        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
                    )

                    val inflater = this.layoutInflater
                    val dialogView: View =
                        inflater.inflate(R.layout.modify_driver_fragment, null)
                    dialogBuilder.setView(dialogView)
                    dialogBuilder.setTitle(R.string.modifyDriver)

                    val driverEdit = dialogView.findViewById<TextView>(R.id.tvNameDriverModify)
                    val weightEdit = dialogView.findViewById<EditText>(R.id.etNewWeightDriverModify)
                    val teamNameEdit = dialogView.findViewById<TextView>(R.id.tvNameTeamModify)


                    if (weight == "null") {
                        weightEdit.setText("")
                    }
                    else {
                        weightEdit.setText(weight)
                    }

                    driverEdit.text = nameDriver
                    teamNameEdit.text = teamName

                    dialogBuilder.setPositiveButton(R.string.button_ok) { _, _ ->
                        if (weight != "null") {
                            if (weightEdit.text.toString().isNotEmpty()) {
                                val newItem = Drivers(driverEdit.text.toString(), weightEdit.text.toString().toDoubleOrNull())
                                dbRef.child("Teams").child(teamName.toString()).child("Drivers").child(nameDriver.toString()).removeValue()
                                val drivers = p0.result.child("Drivers").children
                                for (element in drivers) {
                                    if (element.child("nameDriver").value.toString() == nameDriver.toString()) {
                                        val key = element.ref.key.toString()
                                        dbRef.child("Drivers").child(key).removeValue()
                                    }
                                }
                                val oldItem = Drivers(nameDriver.toString(), weight.toString().toDoubleOrNull())
                                dbRef.child("Teams").child(teamName.toString()).child("Drivers").child(driverEdit.text.toString()).setValue(newItem)
                                dbRef.child("Drivers").child(dbRef.push().key.toString()).setValue(newItem)

                                runOnUiThread {
                                    adapter.deleteItem(oldItem)
                                    adapter.addItem(newItem)
                                }
                            }
                            else if (weightEdit.text.toString().isEmpty()) {
                                val newItem = Drivers(driverEdit.text.toString(), null)
                                dbRef.child("Teams").child(teamName.toString()).child("Drivers").child(nameDriver.toString()).removeValue()
                                val drivers = p0.result.child("Drivers").children
                                for (element in drivers) {
                                    if (element.child("nameDriver").value.toString() == nameDriver.toString()) {
                                        val key = element.ref.key.toString()
                                        dbRef.child("Drivers").child(key).removeValue()
                                    }
                                }
                                val oldItem = Drivers(nameDriver.toString(), weight.toString().toDoubleOrNull())
                                dbRef.child("Teams").child(teamName.toString()).child("Drivers").child(driverEdit.text.toString()).setValue(newItem)

                                val doneDrivers = p0.result.child("Teams").child(teamName.toString()).child("Info").child("hasDriversDone").value.toString().toInt()
                                val people = p0.result.child("Teams").child(teamName.toString()).child("Info").child("people").value.toString().toInt()

                                if (doneDrivers == people) {
                                    dbRef.child("Teams").child(teamName.toString()).child("Info")
                                        .child("hasDriversDone").setValue(doneDrivers - 1)
                                    val teamsDone = p0.result.child("Info")
                                        .child("hasTeamsDone").value.toString().toInt()
                                    dbRef.child("Info").child("hasTeamsDone")
                                        .setValue(teamsDone - 1)
                                }
                                else {
                                    dbRef.child("Teams").child(teamName.toString()).child("Info")
                                        .child("hasDriversDone").setValue(doneDrivers - 1)
                                }

                                runOnUiThread {
                                    adapter.deleteItem(oldItem)
                                    adapter.addItem(newItem)
                                }
                            }
                            else {
                                AlertDialog.Builder(this)
                                    .setTitle(R.string.warning)
                                    .setMessage(R.string.notValidDriver)
                                    .setPositiveButton(R.string.button_ok, null)
                                    .setNegativeButton("", null)
                                    .show()
                            }
                        }
                        else if (weight == "null") {
                            if (weightEdit.text.toString().isNotEmpty()) {
                                val newItem = Drivers(driverEdit.text.toString(), weightEdit.text.toString().toDoubleOrNull())
                                dbRef.child("Teams").child(teamName.toString()).child("Drivers").child(nameDriver.toString()).removeValue()
                                val drivers = p0.result.child("Drivers").children
                                for (element in drivers) {
                                    if (element.child("nameDriver").value.toString() == nameDriver.toString()) {
                                        val key = element.ref.key.toString()
                                        dbRef.child("Drivers").child(key).removeValue()
                                    }
                                }
                                val oldItem = Drivers(nameDriver.toString(), weight.toString().toDoubleOrNull())
                                dbRef.child("Teams").child(teamName.toString()).child("Drivers").child(driverEdit.text.toString()).setValue(newItem)
                                dbRef.child("Drivers").child(dbRef.push().key.toString()).setValue(newItem)

                                val doneDrivers = p0.result.child("Teams").child(teamName.toString()).child("Info").child("hasDriversDone").value.toString().toInt()
                                dbRef.child("Teams").child(teamName.toString()).child("Info").child("hasDriversDone").setValue(ServerValue.increment(1))
                                val people = p0.result.child("Teams").child(teamName.toString()).child("Info").child("people").value.toString().toInt()

                                if (doneDrivers + 1 == people) {
                                    dbRef.child("Info").child("hasTeamsDone").setValue(ServerValue.increment(1))
                                }
                                runOnUiThread {
                                    adapter.deleteItem(oldItem)
                                    adapter.addItem(newItem)
                                }
                            }
                            else if (weightEdit.text.toString().isEmpty()) {
                                val newItem = Drivers(driverEdit.text.toString(), null)
                                dbRef.child("Teams").child(teamName.toString()).child("Drivers").child(nameDriver.toString()).removeValue()
                                val drivers = p0.result.child("Drivers").children
                                for (element in drivers) {
                                    if (element.child("nameDriver").value.toString() == nameDriver.toString()) {
                                        val key = element.ref.key.toString()
                                        dbRef.child("Drivers").child(key).removeValue()
                                    }
                                }
                                val oldItem = Drivers(nameDriver.toString(), weight.toString().toDoubleOrNull())
                                dbRef.child("Teams").child(teamName.toString()).child("Drivers").child(driverEdit.text.toString()).setValue(newItem)

                                runOnUiThread {
                                    adapter.deleteItem(oldItem)
                                    adapter.addItem(newItem)
                                }
                            }
                            else {
                                AlertDialog.Builder(this)
                                    .setTitle(R.string.warning)
                                    .setMessage(R.string.notValidDriver)
                                    .setPositiveButton(R.string.button_ok, null)
                                    .setNegativeButton("", null)
                                    .show()
                            }
                        }
                        else {
                            val snack = Snackbar.make(binding.root, R.string.fail, Snackbar.LENGTH_LONG)
                            snack.show()
                        }
                    }
                    dialogBuilder.setNegativeButton(R.string.button_megse, null)
                    val alertDialog = dialogBuilder.create()
                    alertDialog.show()
                }
            }
        }
    }

    override fun onItemLongClick(driver: Drivers?): Boolean {
        val builder = AlertDialog.Builder(this)

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

        dbRef.get().addOnCompleteListener { p0 ->
            if (p0.isSuccessful) {
                val initDone = p0.result.child("Info").child("hasStintReady").value.toString().toBoolean()
                if (initDone) {
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Mostmár nem törölheted a versenyzőket, mivel már elkezdődött a verseny!")
                    builder.setPositiveButton(R.string.button_ok, null)
                    builder.show()

                } else if (!initDone) {
                    builder.setTitle("Figyelem!")
                    builder.setMessage("Biztos, hogy törölni szeretnéd ezt a versenyzőt?")

                    builder.setPositiveButton(R.string.button_ok) { dialog, which ->
                        adapter.deleteItem(driver!!)

                        dbRef.get().addOnCompleteListener { p0 ->
                            if (p0.isSuccessful) {
                                val list = p0.result.child("Drivers").children
                                for (element in list) {
                                    if (element.child("nameDriver").value.toString() == driver?.nameDriver) {
                                        val key = element.ref.key.toString()
                                        dbRef.child("Drivers").child(key).removeValue()
                                    }
                                }
                                dbRef.child("Teams").child(teamName.toString()).child("Drivers")
                                    .child(driver.nameDriver).removeValue()
                                dbRef2 =
                                    FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app")
                                        .getReference(year.toString())
                                dbRef2.child("Teams").child(teamName.toString()).child("Drivers").child(driver.nameDriver).child("races").setValue(ServerValue.increment(-1))

                                dbRef2.get().addOnCompleteListener { p0 ->
                                    if (p0.isSuccessful) {
                                        val racesDriver = p0.result.child("Teams").child(teamName.toString()).child("Drivers").child(driver.nameDriver).child("races").value.toString().toInt()
                                        if (racesDriver == 1) {
                                            dbRef2.child("Teams").child(teamName.toString()).child("joker").setValue(ServerValue.increment(-1))
                                            for (element in p0.result.child("Teams").child(teamName.toString()).child("Drivers").children) {
                                                if (element.child("joker").value.toString().toBoolean()) {
                                                    dbRef2.child("Teams").child(teamName.toString()).child("Drivers").child(driver.nameDriver).child("joker").setValue(false)
                                                }
                                            }
                                        }
                                    }
                                }

                                if (driver.weight != null) {
                                    val doneDrivers = p0.result.child("Teams").child(teamName.toString()).child("Info").child("hasDriversDone").value.toString().toInt()
                                    val people = p0.result.child("Teams").child(teamName.toString()).child("Info").child("people").value.toString().toInt()
                                    if (doneDrivers == people) {
                                        dbRef.child("Teams").child(teamName.toString()).child("Info")
                                            .child("hasDriversDone").setValue(doneDrivers - 1)
                                        val teamsDone = p0.result.child("Info")
                                            .child("hasTeamsDone").value.toString().toInt()
                                        dbRef.child("Info").child("hasTeamsDone")
                                            .setValue(teamsDone - 1)
                                    }
                                    else {
                                        dbRef.child("Teams").child(teamName.toString()).child("Info")
                                            .child("hasDriversDone").setValue(doneDrivers - 1)
                                    }
                                }
                            }
                        }
                    }
                    builder.setNeutralButton(R.string.button_megse, null)
                    builder.show()
                }
            }
        }
        return true
    }
}

