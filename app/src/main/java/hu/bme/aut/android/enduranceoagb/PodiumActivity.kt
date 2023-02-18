package hu.bme.aut.android.enduranceoagb

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hu.bme.aut.android.enduranceoagb.databinding.ActivityPodiumBinding
import hu.bme.aut.android.enduranceoagb.ui.podium.SectionsPagerAdapterPodium

class PodiumActivity : FragmentActivity() {

    private lateinit var binding: ActivityPodiumBinding

    private lateinit var dbRef: DatabaseReference

    companion object {
        const val EXTRA_RACE_NAME = "extra.race_name"
    }

    private var raceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPodiumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        raceId = intent.getStringExtra(EXTRA_RACE_NAME)

        val sectionsPagerAdapterPodium = SectionsPagerAdapterPodium(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPagerPodium
        viewPager.adapter = sectionsPagerAdapterPodium
        val tabs: TabLayout = binding.tabsPodium
        tabs.setupWithViewPager(viewPager)

        dbRef = FirebaseDatabase.getInstance("https://enduranceoagb-bb301-default-rtdb.europe-west1.firebasedatabase.app").getReference("Races").child(raceId.toString())

    }

    fun getMyData(): String? {
        return raceId
    }

    /*override fun onBackPressed() {
        super.onBackPressed()
        finish()
        val myIntent = Intent(this@StintActivity2, RaceActivity::class.java)
        myIntent.putExtra("extra.race_name", raceId)

        startActivity(myIntent)
    }*/
}