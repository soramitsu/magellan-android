package jp.co.soramitsu.map.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import jp.co.soramitsu.map.presentation.categories.CategoriesFragment
import jp.co.soramitsu.map.presentation.places.PlacesFragment

internal class BottomSheetFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    private val titleDelegate: (Int) -> String
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> CategoriesFragment()
        else -> PlacesFragment()
    }

    override fun getPageTitle(position: Int): CharSequence? = titleDelegate(position)

    override fun getCount(): Int = 2
}
