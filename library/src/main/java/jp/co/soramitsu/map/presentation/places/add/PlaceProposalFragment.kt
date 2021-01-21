package jp.co.soramitsu.map.presentation.places.add

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Category
import kotlinx.android.synthetic.main.sm_fragment_place_proposal.*

class PlaceProposalFragment : Fragment(R.layout.sm_fragment_place_proposal),
    SelectPlaceCategoryFragment.OnCategorySelected {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        addressTextView.text = requireArguments().getString(EXTRA_ADDRESS)

        categoryTextView.setOnClickListener {
            SelectPlaceCategoryFragment().show(childFragmentManager, "SelectCategoryFragment")
        }
    }

    fun withParams(position: LatLng, address: String) = this.apply {
        arguments = bundleOf(
            EXTRA_POSITION to position,
            EXTRA_ADDRESS to address
        )
    }

    override fun onCategorySelected(category: Category) {
        categoryTextView.category = category
    }

    private companion object {
        private const val EXTRA_POSITION = "jp.co.soramitsu.map.presentation.places.add.Position"
        private const val EXTRA_ADDRESS = "jp.co.soramitsu.map.presentation.places.add.Address"
    }
}