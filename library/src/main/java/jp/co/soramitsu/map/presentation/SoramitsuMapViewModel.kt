package jp.co.soramitsu.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.co.soramitsu.map.data.DemoPlacesRepository
import jp.co.soramitsu.map.data.PlacesRepository
import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.presentation.categories.CategoryListItem
import jp.co.soramitsu.map.presentation.lifycycle.SingleLiveEvent
import java.util.*

internal class SoramitsuMapViewModel(
    private val placesRepository: PlacesRepository = DemoPlacesRepository()
) : ViewModel() {

    private val viewState: MutableLiveData<SoramitsuMapViewState> = MutableLiveData()
    private val selectedPlaceSingleLiveEvent: SingleLiveEvent<Place> = SingleLiveEvent()

    private val currentState: SoramitsuMapViewState
        get() = viewState.value!!

    fun viewState(): LiveData<SoramitsuMapViewState> = viewState
    fun placeSelected(): LiveData<Place> = selectedPlaceSingleLiveEvent

    fun onMapReady() {
        val categories = Category.values().map { category -> CategoryListItem(category, true) }
        val places = placesRepository.getAllPlaces()
        viewState.value = currentState.copy(
            places = places,
            categories = categories,
            enableResetButton = true
        )
    }

    fun onSearchTextChanged(query: String) {
        val places = placesRepository.getAllPlaces()
            .filter { place ->
                val placeName = place.name.toLowerCase(Locale.getDefault())
                val placePhone = place.phone.toLowerCase(Locale.getDefault())
                val acceptedByQuery =
                    "#$placeName #$placePhone".contains(query.toLowerCase(Locale.getDefault()))

                val groupedBySelectionProperty = currentState.categories.groupBy { it.selected }
                val selectedCategories = groupedBySelectionProperty[true]?.map { it.category }
                val acceptedByCategory = selectedCategories.isNullOrEmpty()
                        || place.category in selectedCategories

                acceptedByQuery && acceptedByCategory
            }

        viewState.value = currentState.copy(places = places)
    }

    fun onPlaceSelected(place: Place) {
        selectedPlaceSingleLiveEvent.value = place
    }

    fun onCategorySelected(category: Category) {
        val categoryListItems = currentState.categories
        val clickedListItemPosition = categoryListItems.indexOfFirst { it.category == category }
        val clickedItem = categoryListItems[clickedListItemPosition]
        val clickedItemNewState = clickedItem.copy(selected = !clickedItem.selected)
        val newCategoriesList = categoryListItems.toMutableList()
        newCategoriesList[clickedListItemPosition] = clickedItemNewState
        viewState.value = currentState.copy(
            categories = newCategoriesList,
            enableResetButton = newCategoriesList.any { it.selected }
        )
    }

    fun onResetCategoriesFilterButtonClicked() {
        val categories = Category.values().map { category -> CategoryListItem(category, false) }
        viewState.value = currentState.copy(
            categories = categories,
            enableResetButton = false
        )
    }

    init {
        viewState.value = SoramitsuMapViewState()
    }
}

internal data class SoramitsuMapViewState(
    val showLoadingIndicator: Boolean = false,
    val places: List<Place> = emptyList(),
    val enableResetButton: Boolean = false,
    val categories: List<CategoryListItem> = emptyList()
)