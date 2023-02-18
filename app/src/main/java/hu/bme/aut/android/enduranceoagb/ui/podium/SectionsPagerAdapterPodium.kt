package hu.bme.aut.android.enduranceoagb.ui.podium

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.bme.aut.android.enduranceoagb.R
import hu.bme.aut.android.enduranceoagb.fragments.PodiumAbsFragment
import hu.bme.aut.android.enduranceoagb.fragments.PodiumGp2Fragment
import hu.bme.aut.android.enduranceoagb.fragments.StintDoneFragment
import hu.bme.aut.android.enduranceoagb.fragments.StintLeftFragment
import hu.bme.aut.android.enduranceoagb.ui.stint.PlaceholderFragmentStint

private val TAB_TITLES = arrayOf(
    R.string.gp2,
    R.string.app_name
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapterPodium(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return PodiumGp2Fragment()
            }
            1 -> {
                return PodiumAbsFragment()
            }

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            else -> return PlaceholderFragmentPodium.newInstance(position + 1)

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