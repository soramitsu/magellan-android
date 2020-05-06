package jp.co.soramitsu.map.presentation.places

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import kotlinx.android.synthetic.main.places_fragment.*

internal class PlacesFragment : Fragment(R.layout.places_fragment) {

    private lateinit var placesAdapter: PlacesAdapter

    private lateinit var viewModel: SoramitsuMapViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesAdapter = PlacesAdapter { selectedPlace -> viewModel.onPlaceSelected(selectedPlace) }
        placesRecyclerView.adapter = placesAdapter
        placesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fragmentManager?.fragments?.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            viewModel = ViewModelProviders
                .of(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)

            viewModel.viewState().observe(viewLifecycleOwner, Observer { viewState ->
                placesAdapter.update(viewState.places)
            })
        }
    }
}
