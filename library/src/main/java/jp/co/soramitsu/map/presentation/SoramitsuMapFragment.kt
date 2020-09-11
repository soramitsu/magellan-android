package jp.co.soramitsu.map.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.data.MapParams
import jp.co.soramitsu.map.ext.asLatLng
import jp.co.soramitsu.map.ext.getResourceIdForAttr
import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.GeoPoint
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.presentation.categories.CategoriesFragment
import jp.co.soramitsu.map.presentation.places.PlaceFragment
import jp.co.soramitsu.map.presentation.places.PlacesSearchResultsAdapter
import kotlinx.android.synthetic.main.sm_fragment_map_soramitsu.*
import kotlinx.android.synthetic.main.sm_search_panel.*

/**
 * Used fragment as a base class because Maps module have to minimize
 * number of connections with Bakong and its base classes
 */
open class SoramitsuMapFragment : Fragment(R.layout.sm_fragment_map_soramitsu) {

    private lateinit var viewModel: SoramitsuMapViewModel
    private lateinit var searchPanelBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val inputMethodService: InputMethodManager?
        get() = context?.let { context ->
            getSystemService(context, InputMethodManager::class.java)
        }

    private var googleMap: GoogleMap? = null

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

    private val markers = mutableListOf<Marker>()
    private val clusters = mutableListOf<Marker>()

    // will be used for throttling. Maybe we can achieve the same
    // behavior using delay() before request and cancelling corresponding job
    private val handler = Handler()
    private var onMapScrollStopCallback: Runnable? = null

    private val placesAdapter = PlacesSearchResultsAdapter { place ->
        viewModel.onPlaceSelected(place)
        val placePosition = GeoPoint(
            latitude = place.position.latitude,
            longitude = place.position.longitude
        )
        viewModel.onExtendedPlaceInfoRequested(placePosition)

        PlaceFragment().show(parentFragmentManager, "Place")
    }

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
        soramitsuMapView.onCreate(savedInstanceState)
        soramitsuMapView.getMapAsync { onMapReady(it) }

        initSearchPanel()

        showFiltersButton.setOnClickListener { CategoriesFragment().show(parentFragmentManager, "Categories") }

        findMeButton.setOnClickListener {
            googleMap?.let { googleMap -> onFindMeButtonClicked(googleMap) }
        }

        zoomInButton.setOnClickListener {
            googleMap?.let { googleMap ->
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        googleMap.cameraPosition.target,
                        googleMap.cameraPosition.zoom + 2
                    )
                )
            }
        }

        placesWithSearchTextInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchPanelBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        placesWithSearchTextInputEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                inputMethodService?.hideSoftInputFromWindow(v.windowToken, 0)

                val searchText = placesWithSearchTextInputEditText.text?.toString().orEmpty()
                viewModel.requestParams = viewModel.requestParams.copy(query = searchText)
            }

            true
        }

        zoomOutButton.setOnClickListener {
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
        soramitsuMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        soramitsuMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        soramitsuMapView.onPause()

        inputMethodService?.hideSoftInputFromWindow(view?.windowToken, 0)
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        soramitsuMapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soramitsuMapView.onDestroy()
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
                // throttleLast(onCardScrollStopCallback, 500ms)
                onMapScrollStopCallback?.let { handler.removeCallbacks(it) }
                onMapScrollStopCallback = Runnable {
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
                }
                onMapScrollStopCallback?.let { handler.postDelayed(it, 500) }
            }

            viewModel.placeSelected().observe(viewLifecycleOwner, Observer { selectedPlace ->
                val buttonsVisibility = if (selectedPlace == null) View.VISIBLE else View.GONE
                zoomButtonsPanel.visibility = buttonsVisibility
                findMeButton.visibility = buttonsVisibility
                showFiltersButton.visibility = buttonsVisibility
                searchPanelBottomSheet.visibility = buttonsVisibility

                inputMethodService?.hideSoftInputFromWindow(placesWithSearchTextInputEditText.windowToken, 0)
                highlightSelectedPlace()
            })

            viewModel.viewState().observe(viewLifecycleOwner, Observer { viewState ->
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
                        PlaceFragment().show(parentFragmentManager, "Place")
                    }

                    true
                }

                placesAdapter.update(viewState.places)
            })

            viewModel.mapParams = getMapParams(googleMap)
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

    private fun initSearchPanel() {
        placesRecyclerView.adapter = placesAdapter
        placesRecyclerView.layoutManager = LinearLayoutManager(context)

        searchPanelBottomSheetBehavior = BottomSheetBehavior.from(searchPanelBottomSheet)
        searchPanelBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        searchPanelBottomSheetBehavior.isHideable = false
        searchPanelBottomSheetBehavior.isFitToContents = false
        searchPanelBottomSheetBehavior.halfExpandedRatio = 0.0001f
        searchPanelBottomSheetBehavior.onStateChanged { newState ->
            if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                searchPanelBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            if (searchPanelBottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                inputMethodService?.hideSoftInputFromWindow(placesWithSearchTextInputEditText.windowToken, 0)
                placesWithSearchTextInputEditText.clearFocus()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(searchPanelBottomSheet) { _, insets ->
            val statusBarHeight = insets.systemWindowInsetTop
            val topOffset = (statusBarHeight * 1.15f).toInt()
            searchPanelBottomSheetBehavior.setExpandedOffset(topOffset)

            insets.consumeSystemWindowInsets()
        }

        placesWithSearchTextInputEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                inputMethodService?.hideSoftInputFromWindow(v.windowToken, 0)

                val searchText = placesWithSearchTextInputEditText.text?.toString().orEmpty()
                viewModel.requestParams = viewModel.requestParams.copy(query = searchText)
            }

            true
        }

        placesWithSearchTextInputLayout.setEndIconOnClickListener { v ->
            placesWithSearchTextInputEditText.text = null
            inputMethodService?.hideSoftInputFromWindow(v.windowToken, 0)
            viewModel.requestParams = viewModel.requestParams.copy(query = "")
        }
    }

    /**
     * Just a syntax sugar to hide onSlide method from bottom sheet state change callbacks
     */
    private fun <T : View> BottomSheetBehavior<T>.onStateChanged(stateChangeHandler: (Int) -> Unit) {
        this.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                stateChangeHandler.invoke(newState)
            }
        })
    }

    data class MarkerTagData(
        val place: Place,
        val selected: Boolean = false
    )

    companion object {
        const val LOCATION_REQUEST_CODE = 777
    }
}