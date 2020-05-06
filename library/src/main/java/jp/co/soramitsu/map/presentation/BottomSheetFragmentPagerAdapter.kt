package jp.co.soramitsu.map.presentation

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import jp.co.soramitsu.map.presentation.categories.CategoriesFragment
import jp.co.soramitsu.map.presentation.places.PlacesFragment

internal class BottomSheetFragmentPagerAdapter(
    fragmentManager: FragmentManager,
    private val titleDelegate: (Int) -> String
) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments = SparseArray<Fragment?>(NUMBER_OF_ITEMS)

    fun findFragmentAtPosition(position: Int): Fragment? = fragments[position]

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> CategoriesFragment()
        else -> PlacesFragment()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        fragments.put(position, fragment as Fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)

        fragments.remove(position)
    }

    override fun getPageTitle(position: Int): CharSequence? = titleDelegate(position)

    override fun getCount(): Int = NUMBER_OF_ITEMS

    companion object {
        private const val NUMBER_OF_ITEMS = 2
    }
}
