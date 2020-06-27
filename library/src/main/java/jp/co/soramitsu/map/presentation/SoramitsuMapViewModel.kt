package jp.co.soramitsu.map.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.data.MapParams
import jp.co.soramitsu.map.data.PlacesRepository
import jp.co.soramitsu.map.data.RequestParams
import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.Cluster
import jp.co.soramitsu.map.model.GeoPoint
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.presentation.categories.CategoryListItem
import jp.co.soramitsu.map.presentation.lifycycle.SingleLiveEvent
import kotlinx.coroutines.*

internal class SoramitsuMapViewModel(
    private val placesRepository: PlacesRepository = SoramitsuMapLibraryConfig.repository,
    private val mainThreadDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val viewState: MutableLiveData<SoramitsuMapViewState> = MutableLiveData()
    private val selectedPlaceSingleLiveEvent: SingleLiveEvent<Place> = SingleLiveEvent()

    private val currentState: SoramitsuMapViewState
        get() = viewState.value!!

    private var loadAllPlacesAndClustersJob: Job? = null

    var requestParams = RequestParams("", emptyList())
        set(value) {
            field = value

            updateScreen()
        }

    var mapParams = MapParams(GeoPoint(0.0, 0.0), GeoPoint(0.0, 0.0), 0)
        set(value) {
            field = value

            updateScreen()
        }

    fun viewState(): LiveData<SoramitsuMapViewState> = viewState
    fun placeSelected(): LiveData<Place> = selectedPlaceSingleLiveEvent

    fun applyNewFilters() {
        val selectedCategories = currentState.categories
            .filter { it.selected }
            .map { it.category.name }

        requestParams = requestParams.copy(
            categories = selectedCategories
        )
    }

    private fun updateScreen() {
        loadAllPlacesAndClustersJob?.cancel()
        loadAllPlacesAndClustersJob = viewModelScope.launch(mainThreadDispatcher) {
            val allCategories = getAllCategoriesSuspend()
            val categoryListItems = allCategories.map { category ->
                CategoryListItem(
                    category,
                    category.name in requestParams.categories || requestParams.categories.isEmpty()
                )
            }
            val placesAndClusters = getAllPlacesAndClustersSuspend(mapParams, requestParams)
            val clusters = placesAndClusters.second.filter { cluster -> cluster.count > 1 }
            viewState.value = currentState.copy(
                places = placesAndClusters.first,
                categories = categoryListItems,
                enableResetButton = categoryListItems.any { !it.selected },
                clusters = clusters
            )
        }
    }

    fun onPlaceSelected(place: Place) {
        selectedPlaceSingleLiveEvent.value = place
    }

    fun onExtendedPlaceInfoRequested(placePosition: GeoPoint) =
        viewModelScope.launch(mainThreadDispatcher) {
            val place = currentState.places.find {
                it.position.latitude == placePosition.latitude &&
                        it.position.longitude == placePosition.longitude
            }
            if (place != null) {
                selectedPlaceSingleLiveEvent.value = place

                val placeWithFullInfo = loadPlaceInfo(place)

                selectedPlaceSingleLiveEvent.value = placeWithFullInfo
            }
        }

    private suspend fun getAllCategoriesSuspend(): List<Category> =
        withContext(backgroundDispatcher) {
            try {
                placesRepository.getCategories()
            } catch (exception: Exception) {
                Log.w("Network", exception)
                emptyList<Category>()
            }
        }

    private suspend fun loadPlaceInfo(place: Place): Place = withContext(backgroundDispatcher) {
        try {
            placesRepository.getPlaceInfo(place)
        } catch (exception: Exception) {
            Log.w("Network", exception)
            place
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
            enableResetButton = newCategoriesList.any { !it.selected }
        )
    }

    fun onResetCategoriesFilterButtonClicked() {
        val categories = placesRepository
            .getCategories()
            .map { category -> CategoryListItem(category, false) }

        requestParams = requestParams.copy(
            categories = categories.filter { it.selected }.map { it.category.name }
        )
        viewState.value = currentState.copy(
            categories = categories,
            enableResetButton = false
        )
    }

    private suspend fun getAllPlacesAndClustersSuspend(
        mapParams: MapParams,
        requestParams: RequestParams
    ): Pair<List<Place>, List<Cluster>> = withContext(backgroundDispatcher) {
        try {
            placesRepository.getCategories()
            placesRepository.getAllPlaces(mapParams, requestParams)
        } catch (exception: Exception) {
            Log.w("Network", exception)
            Pair<List<Place>, List<Cluster>>(emptyList<Place>(), emptyList<Cluster>())
        }
    }

    init {
        viewState.value = SoramitsuMapViewState()
    }
}

internal data class SoramitsuMapViewState(
    val places: List<Place> = emptyList(),
    val clusters: List<Cluster> = emptyList(),
    val enableResetButton: Boolean = false,
    val categories: List<CategoryListItem> = emptyList()
)