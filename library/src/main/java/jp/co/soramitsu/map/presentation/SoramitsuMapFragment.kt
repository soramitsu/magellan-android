package jp.co.soramitsu.map.presentation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.data.MapParams
import jp.co.soramitsu.map.databinding.SmFragmentMapSoramitsuBinding
import jp.co.soramitsu.map.ext.asLatLng
import jp.co.soramitsu.map.ext.getResourceIdForAttr
import jp.co.soramitsu.map.ext.toPosition
import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.GeoPoint
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.presentation.categories.CategoriesFragment
import jp.co.soramitsu.map.presentation.places.PlaceFragment
import jp.co.soramitsu.map.presentation.places.add.AddPlaceFragment
import jp.co.soramitsu.map.presentation.places.add.ProposePlaceActivity
import jp.co.soramitsu.map.presentation.places.add.RequestSentBottomSheetFragment
import jp.co.soramitsu.map.presentation.search.SearchBottomSheetFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Used fragment as a base class because Maps module have to minimize
 * number of connections with Bakong and its base classes
 */
open class SoramitsuMapFragment : Fragment(R.layout.sm_fragment_map_soramitsu), AddPlaceFragment.Listener {

    private lateinit var viewModel: SoramitsuMapViewModel

    private var googleMap: GoogleMap? = null

    private var _binding: SmFragmentMapSoramitsuBinding? = null
    private val binding get() = _binding!!

    private fun getMapParams(googleMap: GoogleMap): MapParams {
        val farLeft = googleMap.projection.visibleRegion.farLeft
        val nearRight = googleMap.projection.visibleRegion.nearRight
        val zoom = googleMap.cameraPosition.zoom.toInt()
        return MapParams(
            topLeft = GeoPoint(
                latitude = farLeft.latitude,
                longitude = farLeft.longitude
            ),
            bottomRight = GeoPoint(
                latitude = nearRight.latitude,
                longitude = nearRight.longitude
            ),
            zoom = zoom
        )
    }

    private var droppedPinMarker: Marker? = null
    private val markers = mutableListOf<Marker>()
    private val clusters = mutableListOf<Marker>()

    protected fun retryGetPlacesRequest() {
        googleMap?.let { googleMap ->
            viewModel.mapParams = getMapParams(googleMap)
        }
    }

    protected fun retryGetPlaceInfoRequest() {
        viewModel.onPlaceSelected(viewModel.placeSelected().value)
    }

    protected fun retryGetCategoriesRequest() {
        // will trigger "get categories" request before "get places"
        googleMap?.let { googleMap ->
            viewModel.mapParams = getMapParams(googleMap)
        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(SoramitsuMapViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SmFragmentMapSoramitsuBinding.bind(view)

        binding.soramitsuMapView.onCreate(savedInstanceState)
        binding.soramitsuMapView.getMapAsync { onMapReady(it) }

        initSearchPanel()

        binding.showFiltersButton.setOnClickListener { CategoriesFragment().show(parentFragmentManager, "Categories") }

        binding.findMeButton.setOnClickListener {
            googleMap?.let { googleMap -> onFindMeButtonClicked(googleMap) }
        }

        binding.zoomInButton.setOnClickListener {
            googleMap?.let { googleMap ->
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        googleMap.cameraPosition.target,
                        googleMap.cameraPosition.zoom + 2
                    )
                )
            }
        }

        binding.zoomOutButton.setOnClickListener {
            googleMap?.let { googleMap ->
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        googleMap.cameraPosition.target,
                        googleMap.cameraPosition.zoom - 2
                    )
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.soramitsuMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.soramitsuMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.soramitsuMapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.soramitsuMapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.soramitsuMapView.onDestroy()

        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_PLACE && resultCode == Activity.RESULT_OK) {
            RequestSentBottomSheetFragment().show(parentFragmentManager, "RequestSent")
        }
    }

    override fun onAddPlaceButtonClick(position: LatLng, address: String) {
        context?.let { context ->
            val intent = ProposePlaceActivity.createLaunchIntent(context, position, address)
            startActivityForResult(intent, REQUEST_CODE_ADD_PLACE)
        }
    }

    override fun onAddPlaceFragmentDismiss() {
        viewModel.onAddPlaceCancelled()
    }

    private fun onMapReady(map: GoogleMap?) {
        map?.let { googleMap ->
            this.googleMap = googleMap

            googleMap.uiSettings.apply {
                isZoomGesturesEnabled = true
                isRotateGesturesEnabled = false
                isCompassEnabled = true
                isMyLocationButtonEnabled = true
            }

            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    SoramitsuMapLibraryConfig.defaultPosition.asLatLng(),
                    SoramitsuMapLibraryConfig.defaultZoom
                )
            )

            googleMap.setOnCameraMoveListener {
                val farLeft = googleMap.projection.visibleRegion.farLeft
                val nearRight = googleMap.projection.visibleRegion.nearRight
                val zoom = googleMap.cameraPosition.zoom.toInt()
                viewModel.mapParams = MapParams(
                    topLeft = GeoPoint(
                        latitude = farLeft.latitude,
                        longitude = farLeft.longitude
                    ),
                    bottomRight = GeoPoint(
                        latitude = nearRight.latitude,
                        longitude = nearRight.longitude
                    ),
                    zoom = zoom
                )

                activity?.onUserInteraction()
            }

            viewModel.placeSelected().observe(viewLifecycleOwner) { selectedPlace ->
                val buttonsVisibility = if (selectedPlace == null) View.VISIBLE else View.GONE
                binding.zoomButtonsPanel.visibility = buttonsVisibility
                binding.findMeButton.visibility = buttonsVisibility
                binding.showFiltersButton.visibility = buttonsVisibility
                binding.searchPanelFakeBottomSheet.visibility = buttonsVisibility

                highlightSelectedPlace()

                activity?.onUserInteraction()
            }

            viewModel.viewState().observe(viewLifecycleOwner) { viewState ->
                displayMarkers(viewState)
                displayClusters(viewState)

                highlightSelectedPlace()

                googleMap.setOnMarkerClickListener { marker ->
                    if (marker in clusters) {
                        // zoom in
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                marker.position,
                                googleMap.cameraPosition.zoom + 2
                            )
                        )
                    } else if (marker in markers) {
                        val placePoint = GeoPoint(
                            latitude = marker.position.latitude,
                            longitude = marker.position.longitude
                        )
                        viewModel.onExtendedPlaceInfoRequested(placePoint)

                        (parentFragmentManager.findFragmentByTag("Place") as? PlaceFragment)?.dismiss()
                        PlaceFragment().show(parentFragmentManager, "Place")
                    }

                    true
                }
            }

            lifecycleScope.launch {
                // wait some time to prevent passing [(0,0), (0,0)] rectangle
                delay(100)
                viewModel.mapParams = getMapParams(googleMap)
            }

            viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
                binding.searchOnFragmentInputEditText.setText(query)
            }

            if (SoramitsuMapLibraryConfig.enablePlaceProposal) {
                enablePlaceProposal()
            }
        }
    }

    private fun enablePlaceProposal() {
        googleMap?.setOnMapClickListener { position ->
            viewModel.onMapClickedAtPosition(position.toPosition())

            (parentFragmentManager.findFragmentByTag("Place") as? PlaceFragment)?.dismiss()

            val addPlaceFragment = AddPlaceFragment().withPosition(position)
            addPlaceFragment.setTargetFragment(this, REQUEST_CODE_ADD_PLACE)
            addPlaceFragment.show(parentFragmentManager, "AddPlaceFragment")
        }
    }

    private fun highlightSelectedPlace() {
        val selectedPlace = viewModel.placeSelected().value

        // change selected place icon:
        // 1. change icon of previously selected place
        // 2. updated selected place (change icon of currently selected element)
        markers.forEach { marker ->
            val tagData = marker.tag as? MarkerTagData
            if (tagData?.selected == true && tagData.place.id != selectedPlace?.id) {
                marker.setIcon(placePinIcon(tagData.place))
                marker.tag = tagData.copy(selected = false)
                marker.zIndex = 0f
            }

            if (tagData != null && tagData.place.id == selectedPlace?.id) {
                marker.setIcon(placePinIcon(tagData.place, scale = 1.25f))
                marker.tag = tagData.copy(selected = true)
                marker.zIndex = 1f
            }
        }
    }

    private fun displayMarkers(viewState: SoramitsuMapViewState) {
        SoramitsuMapLibraryConfig.logger.log("MapViewState", viewState.toString())
        if (viewState.dropPinPosition != null) {
            val addPlaceDrawablePin = ContextCompat.getDrawable(requireContext(), R.drawable.sm_pin_add_place) ?: return
            val bitmap = Bitmap.createBitmap(
                addPlaceDrawablePin.intrinsicWidth,
                addPlaceDrawablePin.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            addPlaceDrawablePin.setBounds(0, 0, canvas.width, canvas.height)
            addPlaceDrawablePin.draw(canvas)

            droppedPinMarker?.remove()
            droppedPinMarker = googleMap?.addMarker(
                MarkerOptions()
                    .position(viewState.dropPinPosition.asLatLng())
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            )

            markers.forEach { marker -> marker.remove() }
            return
        }

        droppedPinMarker?.remove()

        val markersToRemove = markers.filter { marker ->
            if (marker.tag is MarkerTagData) {
                val markerTagData = marker.tag as MarkerTagData
                markerTagData.place !in viewState.places
            } else {
                true
            }
        }
        markersToRemove.forEach { it.remove() }
        markers.removeAll(markersToRemove)

        val displayedPlaces = markers.mapNotNull { marker -> (marker.tag as? MarkerTagData)?.place }
        val newPlaces = viewState.places.filter { place -> place !in displayedPlaces }
        val newMarkers = newPlaces.mapNotNull {
            val markerOptions = MarkerOptions()
                .position(it.position.asLatLng())
                .icon(placePinIcon(it))
            googleMap?.addMarker(markerOptions)?.apply {
                tag = MarkerTagData(it, false)
            }
        }
        markers.addAll(newMarkers)
    }

    private fun placePinIcon(place: Place, scale: Float = 1f): BitmapDescriptor {
        val iconId = iconForCategory(requireContext(), place.category)
        val drawable = ContextCompat.getDrawable(requireContext(), iconId)
        val iconWidth = (drawable!!.intrinsicWidth * scale).toInt()
        val iconHeight = (drawable.intrinsicHeight * scale).toInt()
        val bitmap = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    @DrawableRes
    private fun iconForCategory(context: Context, category: Category): Int = when (category.name) {
        Category.BANK.name -> context.getResourceIdForAttr(R.attr.sm_categoryPinIconDeposit)
        Category.FOOD.name -> context.getResourceIdForAttr(R.attr.sm_categoryPinIconRestaurant)
        Category.SERVICES.name -> context.getResourceIdForAttr(R.attr.sm_categoryPinIconServices)
        Category.SUPERMARKETS.name -> context.getResourceIdForAttr(R.attr.sm_categoryPinIconSupermarket)
        Category.PHARMACY.name -> context.getResourceIdForAttr(R.attr.sm_categoryPinIconPharmacy)
        Category.ENTERTAINMENT.name -> context.getResourceIdForAttr(R.attr.sm_categoryPinIconEntertainment)
        Category.EDUCATION.name -> context.getResourceIdForAttr(R.attr.sm_categoryPinIconEducation)
        else -> context.getResourceIdForAttr(R.attr.sm_categoryPinIconOther)
    }

    private fun displayClusters(viewState: SoramitsuMapViewState) {
        clusters.forEach { it.remove() }
        clusters.clear()
        val iconGen = IconGenerator(requireContext())
        val newClusters = viewState.clusters.mapNotNull {
            val icon = iconGen.createClusterDrawable(it.count.toString())
            val markerOptions = MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(it.asLatLng())
            googleMap?.addMarker(markerOptions)
        }
        clusters.addAll(newClusters)
    }

    private fun onFindMeButtonClicked(googleMap: GoogleMap) {
        activity?.onUserInteraction()

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
        val defaultLocationButton = binding.soramitsuMapView.findViewById<View>(defaultFindMeButtonResId)
        defaultLocationButton.visibility = View.GONE

        // get last location from google services
        LocationServices.getFusedLocationProviderClient(requireContext())
            .lastLocation
            .addOnSuccessListener { locationOrNull: Location? ->
                locationOrNull?.let { location ->
                    val currentDevicePosition = LatLng(location.latitude, location.longitude)
                    val cameraUpdate = CameraUpdateFactory.newLatLng(currentDevicePosition)
                    googleMap.moveCamera(cameraUpdate)
                }
            }
    }

    private fun initSearchPanel() {
        // show fullscreen search fragment when user try to enter search query
        binding.searchOnFragmentInputEditText.setOnClickListener {
            activity?.onUserInteraction()

            SearchBottomSheetFragment().show(parentFragmentManager, "SearchBottomSheetFragment")
        }

        // clear edit text button click handler
        binding.placesWithSearchTextInputLayout.setEndIconOnClickListener { v ->
            activity?.onUserInteraction()

            binding.searchOnFragmentInputEditText.text = null
            viewModel.requestParams = viewModel.requestParams.copy(query = "")
        }

        binding.searchOnFragmentInputEditText.setOnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_DOWN) {
                activity?.onUserInteraction()

                v.performClick()
            }

            true
        }
    }

    data class MarkerTagData(
        val place: Place,
        val selected: Boolean = false
    )

    companion object {
        private const val LOCATION_REQUEST_CODE = 777
        private const val REQUEST_CODE_ADD_PLACE = 1
    }
}