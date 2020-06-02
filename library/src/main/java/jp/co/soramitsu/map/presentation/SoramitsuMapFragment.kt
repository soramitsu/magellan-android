package jp.co.soramitsu.map.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.presentation.categories.CategoriesFragment
import jp.co.soramitsu.map.presentation.places.PlacesAdapter
import kotlinx.android.synthetic.main.sm_fragment_map_soramitsu.*
import kotlinx.android.synthetic.main.sm_place_bottom_sheet.*
import kotlinx.android.synthetic.main.sm_places_with_search_field.*

/**
 * Used fragment as a base class because Maps module have to minimize
 * number of connections with Bakong and its base classes
 */
class SoramitsuMapFragment : Fragment(R.layout.sm_fragment_map_soramitsu) {

    private lateinit var viewModel: SoramitsuMapViewModel
    private lateinit var clusterManager: ClusterManager<PlaceClusterItem>
    private lateinit var placeInformationBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var categoriesWithPlacesBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private var googleMap: GoogleMap? = null

    private val placesAdapter = PlacesAdapter { place ->
        viewModel.onPlaceSelected(place)
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.toString()?.let { viewModel.onSearchTextChanged(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(SoramitsuMapViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        soramitsuMapView.onCreate(savedInstanceState)
        soramitsuMapView.getMapAsync { onMapReady(it) }

        categoriesWithPlacesBottomSheetBehavior =
            BottomSheetBehavior.from(placesWithSearchBottomSheet)
        categoriesWithPlacesBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        categoriesWithPlacesBottomSheetBehavior.isHideable = false
        placesRecyclerView.adapter = placesAdapter
        placesRecyclerView.layoutManager = LinearLayoutManager(context)

        showFiltersButton.setOnClickListener {
            CategoriesFragment().show(parentFragmentManager, "Categories")
        }

        placeInformationBottomSheetBehavior = BottomSheetBehavior.from(placeBottomSheet)
        placeInformationBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        placeInformationBottomSheetBehavior.isHideable = true

        placesWithSearchTextInputEditText.addTextChangedListener(textWatcher)
    }

    override fun onStart() {
        super.onStart()
        soramitsuMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        soramitsuMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        soramitsuMapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        soramitsuMapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soramitsuMapView.onDestroy()
        placesWithSearchTextInputEditText.removeTextChangedListener(textWatcher)
    }

    private fun onMapReady(map: GoogleMap?) {
        map?.let { googleMap ->
            this.googleMap = googleMap

            clusterManager = ClusterManager(requireContext(), googleMap)
            clusterManager.setAnimation(true)
            val algorithm = NonHierarchicalDistanceBasedAlgorithm<PlaceClusterItem>()
            // we have to adjust this value for better clusterization
            algorithm.maxDistanceBetweenClusteredItems = 100
            clusterManager.algorithm = algorithm
            googleMap.setOnCameraIdleListener(clusterManager)
            googleMap.setOnMarkerClickListener(clusterManager)
            googleMap.uiSettings.apply {
                isZoomGesturesEnabled = true
                isCompassEnabled = true
                isMyLocationButtonEnabled = true
            }

            val zoomLevel = 10f
            val cambodia = LatLng(11.541789, 104.913587)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cambodia, zoomLevel))

            viewModel.placeSelected().observe(viewLifecycleOwner, Observer { selectedPlace ->
                categoriesWithPlacesBottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_COLLAPSED
                placeInformationBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                bindBottomSheetWithPlace(selectedPlace)

                val latLng = LatLng(
                    selectedPlace.position.latitude,
                    selectedPlace.position.longitude
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                clusterManager.cluster()
            })

            viewModel.viewState().observe(viewLifecycleOwner, Observer { viewState ->
                val clusterItems = viewState.places.map { place -> PlaceClusterItem(place) }

                clusterManager.clearItems()
                clusterManager.addItems(clusterItems)
                clusterManager.cluster()

                placesAdapter.update(viewState.places)
            })

            clusterManager.setOnClusterItemClickListener { clusterItem ->
                placeInformationBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                bindBottomSheetWithPlace(clusterItem.place)
                true
            }

            clusterManager.setOnClusterClickListener { cluster ->
                val bounds = LatLngBounds.builder()
                cluster.items.forEach { bounds.include(it.position) }
                val paddingPx = 100
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds.build(),
                        paddingPx
                    )
                )
                true
            }

            viewModel.onMapReady()

            findMeButton.setOnClickListener {
                onFindMeButtonClicked(googleMap)
            }

            zoomInButton.setOnClickListener {
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        googleMap.cameraPosition.target,
                        googleMap.cameraPosition.zoom + 2
                    )
                )
            }

            zoomOutButton.setOnClickListener {
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        googleMap.cameraPosition.target,
                        googleMap.cameraPosition.zoom - 2
                    )
                )
            }
        }
    }

    private fun onFindMeButtonClicked(googleMap: GoogleMap) {
        // check permission and request when not granted. When permissions will
        // be granted, this method will be called again
        val fineLocationPermissionGranted = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermissionGranter = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (fineLocationPermissionGranted != PackageManager.PERMISSION_GRANTED
            && coarseLocationPermissionGranter != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            requestPermissions(permissions, LOCATION_REQUEST_CODE)
            return
        }

        // enable findMe functionality, but use custom findMe button
        googleMap.isMyLocationEnabled = true

        // dirty hack to use internal google implementation of the "find me" button
        val defaultFindMeButtonResId = 2
        val defaultLocationButton = soramitsuMapView.findViewById<View>(defaultFindMeButtonResId)
        defaultLocationButton.visibility = View.GONE
        defaultLocationButton?.callOnClick()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_REQUEST_CODE) {
            val anyPermissionGranted = grantResults.any { it == PackageManager.PERMISSION_GRANTED }
            if (anyPermissionGranted) {
                googleMap?.let { onFindMeButtonClicked(it) }
            }
        }
    }

    private fun bindBottomSheetWithPlace(place: Place) {
        // header info
        placeListItem.bind(place)

        // additional info
        additionalInfoMobilePhone.visibility =
            if (place.phone.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoWebsite.visibility =
            if (place.website.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoFacebook.visibility =
            if (place.facebook.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoAddress.visibility =
            if (place.address.isEmpty()) View.GONE else View.VISIBLE

        additionalInfoMobilePhone.setValue(place.phone)
        additionalInfoWebsite.setValue(place.website)
        additionalInfoFacebook.setValue(place.facebook)
        additionalInfoAddress.setValue(place.address)

        additionalInfoMobilePhone.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${place.phone}")
            })
        }

        additionalInfoWebsite.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(place.website)
            })
        }

        additionalInfoFacebook.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("facebook://facebook.com/${place.facebook}")
            }
            if (facebookIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(facebookIntent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://facebook.com/${place.facebook}")
                })
            }
        }

        additionalInfoAddress.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:0,0?q=${Uri.encode(place.address)}")
                `package` = "com.google.android.apps.maps"
            })
        }
    }

    internal class PlaceClusterItem(val place: Place) : ClusterItem {
        override fun getSnippet(): String = ""
        override fun getTitle(): String = place.name
        override fun getPosition(): LatLng =
            LatLng(place.position.latitude, place.position.longitude)
    }

    companion object {
        const val LOCATION_REQUEST_CODE = 777
    }
}