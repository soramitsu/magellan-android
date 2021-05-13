package jp.co.soramitsu.map.presentation.places.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.data.PlacesRepository
import jp.co.soramitsu.map.model.Category
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SelectPlaceViewModel(
    private val placesRepository: PlacesRepository = SoramitsuMapLibraryConfig.repository,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> by lazy {
        viewModelScope.launch { _categories.value = getAllCategoriesSuspend() }
        _categories
    }

    private suspend fun getAllCategoriesSuspend(): List<Category> =
        withContext(backgroundDispatcher) {
            kotlin.runCatching {
                placesRepository.getCategories()
            }.onFailure {
                SoramitsuMapLibraryConfig.logger.log("Network", it)
            }.getOrDefault(emptyList())
        }
}
