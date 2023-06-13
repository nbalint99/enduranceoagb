package hu.bme.aut.android.enduranceoagb.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.fragments.RaceDoneFragment
import hu.bme.aut.android.enduranceoagb.fragments.RaceLeftFragment

private val TAB_TITLES = arrayOf(
    R.string.tab_text_main_1,
    R.string.tab_text_2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                //  val homeFragment: HomeFragment = HomeFragment()
                return RaceLeftFragment()
            }
            1 -> {
                return RaceDoneFragment()
            }

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            else -> return PlaceholderFragment.newInstance(position + 1)

        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}