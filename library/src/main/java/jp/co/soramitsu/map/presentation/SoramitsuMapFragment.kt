package jp.co.soramitsu.map.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
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
import com.google.maps.android.ui.IconGenerator
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.data.MapParams
import jp.co.soramitsu.map.ext.asLatLng
import jp.co.soramitsu.map.ext.getResourceIdForAttr
import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.GeoPoint
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
    private lateinit var placeInformationBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var categoriesWithPlacesBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

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

    private val placesAdapter = PlacesAdapter { place ->
        viewModel.onPlaceSelected(place)
        val placePosition = GeoPoint(
            latitude = place.position.latitude,
            longitude = place.position.longitude
        )
        viewModel.onExtendedPlaceInfoRequested(placePosition)
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
        placeInformationBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        // show back categories bottom sheet
                        categoriesWithPlacesBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        categoriesWithPlacesBottomSheetBehavior.isHideable = false

                        closePlaceInfoButton.visibility = View.GONE
                    }
                    else -> {
                        // hide categories bottom sheet to prevent touch events propagation
                        categoriesWithPlacesBottomSheetBehavior.isHideable = true
                        categoriesWithPlacesBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                        closePlaceInfoButton.visibility = View.VISIBLE
                    }
                }
            }
        })

        placesWithSearchTextInputEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                inputMethodService?.hideSoftInputFromWindow(v.windowToken, 0)

                val searchText = placesWithSearchTextInputEditText.text?.toString().orEmpty()
                viewModel.requestParams = viewModel.requestParams.copy(query = searchText)
            }

            true;
        }
        placesWithSearchTextInputLayout.setEndIconOnClickListener {
            placesWithSearchTextInputEditText.text = null
            inputMethodService?.hideSoftInputFromWindow(
                placesWithSearchTextInputEditText.windowToken, 0
            )
            viewModel.requestParams = viewModel.requestParams.copy(query = "")
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
                // throttleLast(onCardScrollStopCallback, 2000ms)
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
                inputMethodService?.hideSoftInputFromWindow(
                    placesWithSearchTextInputEditText.windowToken, 0
                )

                categoriesWithPlacesBottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_COLLAPSED
                placeInformationBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                bindBottomSheetWithPlace(selectedPlace)

                // change selected place icon:
                // 1. change icon of previously selected place
                // 2. updated selected place (change icon of currently selected element)
                markers.forEach { marker ->
                    val tagData = marker.tag as? MarkerTagData
                    if (tagData?.selected == true && tagData.place.id != selectedPlace.id) {
                        marker.setIcon(placePinIcon(tagData.place))
                        marker.tag = tagData.copy(selected = false)
                        marker.zIndex = 0f
                    }

                    if (tagData != null && tagData.place.id == selectedPlace.id) {
                        marker.setIcon(placePinIcon(tagData.place, scale = 1.25f))
                        marker.tag = tagData.copy(selected = true)
                        marker.zIndex = 1f
                    }
                }
            })

            viewModel.viewState().observe(viewLifecycleOwner, Observer { viewState ->
                displayMarkers(viewState)
                displayClusters(viewState)

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
                    }

                    true
                }

                placesAdapter.update(viewState.places)
            })

            viewModel.mapParams = getMapParams(googleMap)

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
        val iconGen = IconGenerator(requireContext()).apply {
            val background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.sm_cluster_background
            )
            setBackground(background)
            setTextAppearance(R.style.SM_TextAppearance_Soramitsu_MaterialComponents_Caption_White)
        }
        val newClusters = viewState.clusters.mapNotNull {
            val icon = iconGen.makeIcon(it.count.toString())
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

        additionalInfoMobilePhone.text = place.phone
        additionalInfoWebsite.text = place.website
        additionalInfoFacebook.text = place.facebook
        additionalInfoAddress.text = place.address

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

        closePlaceInfoButton.setOnClickListener {
            placeInformationBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    data class MarkerTagData(
        val place: Place,
        val selected: Boolean = false
    )

    companion object {
        const val LOCATION_REQUEST_CODE = 777
    }
}