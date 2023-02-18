package hu.bme.aut.android.enduranceoagb

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.databinding.ActivityMainBinding
import hu.bme.aut.android.enduranceoagb.fragments.NewRaceFragment
import hu.bme.aut.android.enduranceoagb.ui.main.SectionsPagerAdapter
import java.util.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), NewRaceFragment.NewRaceListener {

    private lateinit var binding: ActivityMainBinding


    private lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.database.setPersistenceEnabled(true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(viewPager)

        val fab: FloatingActionButton = binding.fab

        fab.setOnClickListener { view ->
            NewRaceFragment().show(
                supportFragmentManager,
                NewRaceFragment.TAG
            )
        }

        binding.teams.setOnClickListener {
            val intent = Intent(this@MainActivity, AllTeamActivity::class.java)
            startActivity(intent)
            finish()
        }

        /*binding.signOut.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }*/

    }

    override fun onRaceCreated(newItem: Races) {

    }

    override fun onRaceNotCreated() {
        val snack = Snackbar.make(binding.root, R.string.notGoodTeamsNumber, Snackbar.LENGTH_LONG)
        snack.show()
    }

    override fun onBackPressed() {
        //do nothing
    }
}