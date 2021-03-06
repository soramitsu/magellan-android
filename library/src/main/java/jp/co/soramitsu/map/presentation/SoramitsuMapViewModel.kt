package jp.co.soramitsu.map.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.data.MapParams
import jp.co.soramitsu.map.data.PlacesRepository
import jp.co.soramitsu.map.data.RequestParams
import jp.co.soramitsu.map.model.*
import jp.co.soramitsu.map.presentation.categories.CategoryListItem
import jp.co.soramitsu.map.presentation.lifycycle.SingleLiveEvent
import kotlinx.coroutines.*

internal class SoramitsuMapViewModel(
    private val placesRepository: PlacesRepository = SoramitsuMapLibraryConfig.repository,
    private val mainThreadDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val viewState: MutableLiveData<SoramitsuMapViewState> = MutableLiveData()
    private val selectedPlaceSingleLiveEvent: MutableLiveData<Place?> = MutableLiveData()
    private val uploadReviewInProgress: SingleLiveEvent<Boolean> = SingleLiveEvent()

    private val editPlaceReview: SingleLiveEvent<Place> = SingleLiveEvent()

    private val currentState: SoramitsuMapViewState
        get() = viewState.value!!

    private var loadAllPlacesAndClustersJob: Job? = null

    private var selectedPlace: Place? = null
        set(value) {
            field = value
            selectedPlaceSingleLiveEvent.value = value
        }

    var requestParams = RequestParams("", emptyList())
        set(value) {
            field = value

            _searchQuery.value = value.query

            updateScreen()
        }

    var mapParams = MapParams(GeoPoint(0.0, 0.0), GeoPoint(0.0, 0.0), 0)
        set(value) {
            field = value

            updateScreen()
        }

    // Used to sync fake search field on the map screen and search fragment's search query input field
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    init {
        viewState.value = SoramitsuMapViewState()
    }

    fun uploadReviewInProgress(): LiveData<Boolean> = uploadReviewInProgress
    fun editPlaceReviewClicked(): LiveData<Place> = editPlaceReview
    fun viewState(): LiveData<SoramitsuMapViewState> = viewState
    fun placeSelected(): LiveData<Place?> = selectedPlaceSingleLiveEvent

    fun applyNewFilters() {
        val selectedCategories = currentState.categories
            .filter { it.selected }
            .map { it.category.id }

        requestParams = requestParams.copy(categoriesIds = selectedCategories)
    }

    fun onPlaceAdded() {
        viewState.value = viewState.value?.copy(dropPinPosition = null)
    }

    fun onPlaceSelected(place: Place?) {
        selectedPlace = place
    }

    fun onEditReviewClicked() {
        editPlaceReview.value = selectedPlace
    }

    fun onDeleteReviewClicked() {
        selectedPlace?.let { outdatedPlace ->
            viewModelScope.launch(mainThreadDispatcher) {
                uploadReviewInProgress.value = true
                val updatedPlace = deleteReview(outdatedPlace)
                uploadReviewInProgress.value = false
                selectedPlace = updatedPlace
            }
        }
    }

    fun onPlaceReviewAdded() {
        selectedPlace?.let { outdatedPlace ->
            viewModelScope.launch(mainThreadDispatcher) {
                uploadReviewInProgress.value = true
                val updatedPlace = loadPlaceInfo(outdatedPlace)
                uploadReviewInProgress.value = false
                selectedPlace = updatedPlace
            }
        }
    }

    fun onExtendedPlaceInfoRequested(placePosition: GeoPoint) =
        viewModelScope.launch(mainThreadDispatcher) {
            val place = currentState.places.find {
                it.position.latitude == placePosition.latitude &&
                    it.position.longitude == placePosition.longitude
            }
            if (place != null) {
                selectedPlace = place
                val placeWithFullInfo = loadPlaceInfo(place)
                selectedPlace = placeWithFullInfo
            }
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
        val categories = placesRepository
            .getCategories()
            .map { category -> CategoryListItem(category, false) }

        requestParams = requestParams.copy(
            categoriesIds = categories.filter { it.selected }.map { it.category.id }
        )
        viewState.value = currentState.copy(
            categories = categories,
            enableResetButton = false
        )
    }

    fun onMapClickedAtPosition(position: Position) {
        viewState.value = viewState.value?.copy(
            dropPinPosition = position
        )
    }

    fun onAddPlaceCancelled() {
        viewState.value = viewState.value?.copy(dropPinPosition = null)
    }

    private suspend fun getAllPlacesAndClustersSuspend(
        mapParams: MapParams,
        requestParams: RequestParams
    ): Pair<List<Place>, List<Cluster>> = withContext(backgroundDispatcher) {
        kotlin.runCatching {
            placesRepository.getCategories()
            placesRepository.getAllPlaces(mapParams, requestParams)
        }.onFailure {
            SoramitsuMapLibraryConfig.logger.log("Network", it)
        }.getOrDefault(Pair(emptyList(), emptyList()))
    }

    private fun updateScreen() {
        loadAllPlacesAndClustersJob?.cancel()
        loadAllPlacesAndClustersJob = viewModelScope.launch(mainThreadDispatcher) {
            val allCategories = getAllCategoriesSuspend()
            val categoryListItems = allCategories.map { category ->
                CategoryListItem(category, category.id in requestParams.categoriesIds)
            }

            if (mapParams.topLeft == GeoPoint(0.0, 0.0) && mapParams.bottomRight == GeoPoint(0.0, 0.0)) {
                return@launch
            }

            val placesAndClusters = getAllPlacesAndClustersSuspend(mapParams, requestParams)
            val clusters = placesAndClusters.second.filter { cluster -> cluster.count > 1 }
            viewState.value = currentState.copy(
                places = placesAndClusters.first,
                categories = categoryListItems,
                enableResetButton = requestParams.categoriesIds.isNotEmpty(),
                clusters = clusters
            )
        }
    }

    private suspend fun getAllCategoriesSuspend(): List<Category> =
        withContext(backgroundDispatcher) {
            kotlin.runCatching {
                placesRepository.getCategories()
            }.onFailure {
                SoramitsuMapLibraryConfig.logger.log("Network", it)
            }.getOrDefault(emptyList())
        }

    private suspend fun loadPlaceInfo(place: Place): Place = withContext(backgroundDispatcher) {
        kotlin.runCatching {
            placesRepository.getPlaceInfo(place)
        }.onFailure {
            SoramitsuMapLibraryConfig.logger.log("Network", it)
        }.getOrDefault(place)
    }

    private suspend fun deleteReview(place: Place): Place = withContext(backgroundDispatcher) {
        kotlin.runCatching {
            place.userReview?.let { userReview -> placesRepository.deleteReview(userReview, place) }
            placesRepository.getPlaceInfo(place)
        }.onFailure {
            SoramitsuMapLibraryConfig.logger.log("Network", it)
        }.getOrDefault(place)
    }
}

internal data class SoramitsuMapViewState(
    val places: List<Place> = emptyList(),
    val dropPinPosition: Position? = null,
    val clusters: List<Cluster> = emptyList(),
    val enableResetButton: Boolean = false,
    val categories: List<CategoryListItem> = emptyList()
)
