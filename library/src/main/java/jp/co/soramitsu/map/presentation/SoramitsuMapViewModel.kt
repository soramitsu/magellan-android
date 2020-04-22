package jp.co.soramitsu.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.co.soramitsu.map.data.DemoPlacesRepository
import jp.co.soramitsu.map.data.PlacesRepository
import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.presentation.lifycycle.SingleLiveEvent
import java.util.*

internal class SoramitsuMapViewModel(
    private val placesRepository: PlacesRepository = DemoPlacesRepository()
) : ViewModel() {

    private val viewState: MutableLiveData<SoramitsuMapViewState> = MutableLiveData()
    private val selectedPlaceSingleLiveEvent: SingleLiveEvent<Place> = SingleLiveEvent()

    private var query: String = ""
    private var category: Category? = null

    private val currentState: SoramitsuMapViewState
        get() = viewState.value!!

    fun viewState(): LiveData<SoramitsuMapViewState> = viewState
    fun placeSelected(): LiveData<Place> = selectedPlaceSingleLiveEvent

    fun onMapReady() {
        updatePlaces()
    }

    fun onSearchTextChanged(query: String) {
        this.query = query
        updatePlaces()
    }

    fun onPlaceSelected(place: Place) {
        selectedPlaceSingleLiveEvent.value = place
    }

    fun onCategorySelected(category: Category) {
        if (this.category != category) {
            this.category = category
        } else {
            // user pressed on already selected category. It'll be unselected
            this.category = null
        }

        updatePlaces()
    }

    private fun updatePlaces() {
        val places = placesRepository.getAllPlaces()
            .filter { place ->
                val placeName = place.name.toLowerCase(Locale.getDefault())
                val placePhone = place.phone.toLowerCase(Locale.getDefault())
                val acceptedByQuery =
                    "#$placeName #$placePhone".contains(query.toLowerCase(Locale.getDefault()))

                val acceptedByCategory = category == null || category == place.category
                acceptedByQuery && acceptedByCategory
            }

        viewState.value = currentState.copy(
            places = places,
            query = query,
            category = category
        )
    }

    init {
        viewState.value = SoramitsuMapViewState()
    }
}

internal data class SoramitsuMapViewState(
    val showLoadingIndicator: Boolean = false,
    val places: List<Place> = emptyList(),
    val query: String = "",
    val category: Category? = null
)