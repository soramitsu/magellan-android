package jp.co.soramitsu.map.presentation.search

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.GeoPoint
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import jp.co.soramitsu.map.presentation.places.PlaceFragment
import jp.co.soramitsu.map.presentation.places.PlacesSearchResultsAdapter
import kotlinx.android.synthetic.main.sm_search_panel.*

/**
 * Bottom sheet dialog that is shown when user try to enter something in search
 * field on maps screen
 */
class SearchBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: SoramitsuMapViewModel

    private val placesAdapter = PlacesSearchResultsAdapter { place ->
        viewModel.onPlaceSelected(place)
        val placePosition = GeoPoint(
            latitude = place.position.latitude,
            longitude = place.position.longitude
        )
        viewModel.onExtendedPlaceInfoRequested(placePosition)

        dismiss()

        PlaceFragment().show(parentFragmentManager, "Place")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sm_search_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // init view model
        parentFragmentManager.fragments.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            viewModel = ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)
        }

        // set transparent background to show rounded corners of the bottom sheet dialog (remove default white background)
        view.doOnLayout {
            val parent = (view.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)

            dialog?.window?.setDimAmount(0f)
        }

        // configure list of places
        placesRecyclerView.adapter = placesAdapter
        placesRecyclerView.layoutManager = LinearLayoutManager(context)
        placesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                activity?.onUserInteraction()
            }
        })

        // search button click handler
        placesWithSearchTextInputEditText.setOnEditorActionListener { v, actionId, _ ->
            activity?.onUserInteraction()

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = placesWithSearchTextInputEditText.text?.toString().orEmpty()
                viewModel.requestParams = viewModel.requestParams.copy(query = searchText)
            }

            true
        }

        // clear edit text button click handler
        placesWithSearchTextInputLayout.setEndIconOnClickListener { v ->
            activity?.onUserInteraction()

            placesWithSearchTextInputEditText.text = null
            viewModel.requestParams = viewModel.requestParams.copy(query = "")
        }

        // subscribe to places search result
        viewModel.viewState().observe(viewLifecycleOwner, Observer { viewState ->
            placesAdapter.update(viewState.places)
        })

        placesWithSearchTextInputEditText.setText(viewModel.requestParams.query)
        placesWithSearchTextInputEditText.doOnTextChanged { _, _, _, _ ->
            activity?.onUserInteraction()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        placesRecyclerView.clearOnScrollListeners()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            // configure and expand bottom sheet
            val bottomSheetDialog = dialog as BottomSheetDialog
            bottomSheetDialog.behavior.isHideable = true
            bottomSheetDialog.behavior.skipCollapsed = true
            bottomSheetDialog.behavior.isFitToContents = false
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                private var fromExpendedState = false

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        fromExpendedState = true
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    activity?.onUserInteraction()

                    if (fromExpendedState && slideOffset < .5f) {
                        dismiss()
                    }
                }
            })

            // request focus on search edit text
            placesWithSearchTextInputEditText.setSelection(placesWithSearchTextInputEditText.text?.length ?: 0)
        }
        return dialog
    }
}