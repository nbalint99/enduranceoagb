package hu.bme.aut.android.enduranceoagb

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.data.Races
import hu.bme.aut.android.enduranceoagb.databinding.ActivityMainBinding
import hu.bme.aut.android.enduranceoagb.fragments.NewRaceFragment
import hu.bme.aut.android.enduranceoagb.ui.main.SectionsPagerAdapter


class MainActivity : AppCompatActivity(), NewRaceFragment.NewRaceListener {

    private lateinit var binding: ActivityMainBinding


    private lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)

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