package jp.co.soramitsu.map.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.ViewPagerBottomSheetBehavior
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.ext.asTime
import jp.co.soramitsu.map.ext.getResourceIdForAttr
import jp.co.soramitsu.map.ext.toMinutesOfDay
import jp.co.soramitsu.map.ext.withTime
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.presentation.category_with_places.CategoriesWithPlacesAdapter
import kotlinx.android.synthetic.main.categories_bottom_sheet.*
import kotlinx.android.synthetic.main.category_with_places.*
import kotlinx.android.synthetic.main.fragment_map_soramitsu.*
import kotlinx.android.synthetic.main.place_bottom_sheet.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Used fragment as a base class because Maps module have to minimize
 * number of connections with Bakong and its base classes
 */
class SoramitsuMapFragment : Fragment(R.layout.fragment_map_soramitsu) {

    private lateinit var viewModel: SoramitsuMapViewModel
    private lateinit var clusterManager: ClusterManager<PlaceClusterItem>
    private lateinit var placeInformationBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var categoriesBottomSheetBehavior: ViewPagerBottomSheetBehavior<ConstraintLayout>
    private lateinit var categoriesWithPlacesBottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val categoriesWithPlacesAdapter: CategoriesWithPlacesAdapter =
        CategoriesWithPlacesAdapter(
            { category ->
                categoriesWithPlacesBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                viewModel.onCategorySelected(category)
            },
            { place -> viewModel.onPlaceSelected(place) }
        )

    private val viewPagerListener = UpdateNestedScrollViewWhenPageChanges()

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
            BottomSheetBehavior.from(categoriesWithPlacesBottomSheet)
        categoriesWithPlacesBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        categoriesWithPlacesBottomSheetBehavior.isHideable = false
        categoriesWithNearbyPlacesRecyclerView.adapter = categoriesWithPlacesAdapter
        categoriesWithNearbyPlacesRecyclerView.layoutManager = LinearLayoutManager(context)

        if (arguments?.getBoolean(EXTRA_SINGLE_TAB, false) == true) {
            categoriesBottomSheet.visibility = View.GONE
        } else {
            categoriesWithPlacesBottomSheet.visibility = View.GONE
        }

        categoriesBottomSheetBehavior = ViewPagerBottomSheetBehavior.from(categoriesBottomSheet)
        categoriesBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        categoriesBottomSheetBehavior.isHideable = false
        categoriesBottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}

            override fun onStateChanged(view: View, state: Int) {
                if (state == STATE_EXPANDED) {
                    viewPagerListener.onPageSelected(categoriesViewPager.currentItem)
                }
            }
        })

        categoriesViewPager.adapter =
            BottomSheetFragmentPagerAdapter(parentFragmentManager) { position ->
                @StringRes val titleId = when (position) {
                    0 -> R.string.sm_category
                    else -> R.string.sm_list_of_places
                }

                getString(titleId)
            }
        categoriesViewPager.addOnPageChangeListener(viewPagerListener)

        placeInformationBottomSheetBehavior = BottomSheetBehavior.from(placeBottomSheet)
        placeInformationBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        placeInformationBottomSheetBehavior.isHideable = true

        searchTextInputEditText.addTextChangedListener(textWatcher)
        categoryWithPlacesSearchTextInputEditText.addTextChangedListener(textWatcher)
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
        categoryWithPlacesSearchTextInputEditText.removeTextChangedListener(textWatcher)
        searchTextInputEditText.removeTextChangedListener(textWatcher)
        categoriesViewPager.removeOnPageChangeListener(viewPagerListener)
    }

    private fun onMapReady(map: GoogleMap?) {
        map?.let { googleMap ->
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
                categoriesBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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

                searchTextInputEditText.removeTextChangedListener(textWatcher)
                searchTextInputEditText.setText(viewState.query)
                searchTextInputEditText.addTextChangedListener(textWatcher)
                searchTextInputEditText.setSelection(searchTextInputEditText.text?.length ?: 0)

                categoryWithPlacesSearchTextInputEditText.removeTextChangedListener(textWatcher)
                categoryWithPlacesSearchTextInputEditText.setText(viewState.query)
                categoryWithPlacesSearchTextInputEditText.addTextChangedListener(textWatcher)
                categoryWithPlacesSearchTextInputEditText.setSelection(
                    categoryWithPlacesSearchTextInputEditText.text?.length ?: 0
                )

                categoriesWithPlacesAdapter.update(viewState.category, viewState.places)
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

            closeDialogButton.setOnClickListener {
                placeInformationBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            clearSearchViewButton.setOnClickListener {
                searchTextInputEditText.text = null
            }

            categoryWithPlacesClearSearchViewButton.setOnClickListener {
                categoryWithPlacesSearchTextInputEditText.text = null
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

    private fun bindBottomSheetWithPlace(place: Place) {
        // header info
        placeListItem.bind(place)

        // additional info
        additionalInfoMobilePhoneGroup.visibility =
            if (place.phone.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoWebsiteGroup.visibility =
            if (place.website.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoFacebookGroup.visibility =
            if (place.facebook.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoAddressGroup.visibility =
            if (place.address.isEmpty()) View.GONE else View.VISIBLE

        additionalInfoMobilePhoneValue.text = place.phone
        additionalInfoWebsiteValue.text = place.website
        additionalInfoFacebookValue.text = place.facebook
        additionalInfoAddressValue.text = place.address

        additionalInfoMobilePhoneValue.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${place.phone}")
            })
        }

        additionalInfoWebsiteValue.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(place.website)
            })
        }

        additionalInfoFacebookValue.setOnClickListener {
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

        additionalInfoAddressValue.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:0,0?q=${Uri.encode(place.address)}")
                `package` = "com.google.android.apps.maps"
            })
        }

        // we don't have working schedule in demo dataset
//        bindWorkingTime(place)
    }

    private fun bindWorkingTime(place: Place) {
        val calendar = Calendar.getInstance()
        val minutesSinceMidnightNow = calendar.asTime().toMinutesOfDay()
        val minutesSinceMidnightOpen = place.startWorkingAtTime.toMinutesOfDay()
        val minutesSinceMidnightClose = place.finishWorkingAtTime.toMinutesOfDay()

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val openNow = minutesSinceMidnightNow in minutesSinceMidnightOpen..minutesSinceMidnightClose
        if (openNow) {
            calendar.withTime(place.finishWorkingAtTime)
            additionalInfoOpenHoursStatus.setText(R.string.sm_open_now)


            @StyleRes val textAppearance = requireContext()
                .getResourceIdForAttr(R.attr.soramitsuMapOpenNowTextAppearance)

            TextViewCompat.setTextAppearance(additionalInfoOpenHoursStatus, textAppearance)
        } else {
            calendar.withTime(place.startWorkingAtTime)
            additionalInfoOpenHoursStatus.setText(R.string.sm_closed_now)

            @StyleRes val textAppearance = requireContext()
                .getResourceIdForAttr(R.attr.soramitsuMapCloseNowTextAppearance)

            TextViewCompat.setTextAppearance(additionalInfoOpenHoursStatus, textAppearance)
        }

        calendar[Calendar.HOUR_OF_DAY] = place.startWorkingAtTime.hour
        calendar[Calendar.MINUTE] = place.startWorkingAtTime.minute
        val startTimeHumanFormat = timeFormat.format(calendar.time)

        calendar[Calendar.HOUR_OF_DAY] = place.finishWorkingAtTime.hour
        calendar[Calendar.MINUTE] = place.finishWorkingAtTime.minute
        val finishTimeHumanFormat = timeFormat.format(calendar.time)

        additionalInfoOpenHoursValue.text =
            getString(R.string.sm_daily_interval, startTimeHumanFormat, finishTimeHumanFormat)
    }

    internal class PlaceClusterItem(val place: Place) : ClusterItem {
        override fun getSnippet(): String = ""
        override fun getTitle(): String = place.name
        override fun getPosition(): LatLng =
            LatLng(place.position.latitude, place.position.longitude)
    }

    /**
     * Part of dirty trick. See details [ViewPagerBottomSheetBehavior]
     */
    inner class UpdateNestedScrollViewWhenPageChanges : ViewPager.OnPageChangeListener {

        private val viewCache = hashMapOf<Int, View>()

        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        /**
         * ```
         * val scrollingChild = currencyFragment.view.recyclerView
         * ViewPagerBehaviour.updateScrollingChild(scrollingChild)
         * ```
         */
        override fun onPageSelected(position: Int) {
            // viewpager stores fragments with tag android:switcher:{view_pager_id}:{position}
            val currentFragment = fragmentManager
                ?.findFragmentByTag("android:switcher:${categoriesViewPager.id}:$position")
            if (!viewCache.containsKey(R.id.placesRecyclerView)) {
                currentFragment?.view?.findViewById<View>(R.id.placesRecyclerView)?.let {
                    viewCache[R.id.placesRecyclerView] = it
                }
            }
            viewCache[R.id.placesRecyclerView]?.let {
                categoriesBottomSheetBehavior.updateScrollingChild(it)
            }
        }
    }

    companion object {
        const val EXTRA_SINGLE_TAB = "EXTRA_TABS"
    }
}