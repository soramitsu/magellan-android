package jp.co.soramitsu.map.presentation.places.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.soramitsu.map.BuildConfig
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.data.PlacesRepository
import jp.co.soramitsu.map.model.Place
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPlaceViewModel(
    private val placesRepository: PlacesRepository = SoramitsuMapLibraryConfig.repository,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val errorLogFun: (Exception) -> Unit = { if (BuildConfig.DEBUG) Log.w("AddPlace", it) }
) : ViewModel() {

    private val _viewState = MutableLiveData<AddPlaceViewState>()
    val viewState: LiveData<AddPlaceViewState> = _viewState

    fun addPlace(place: Place) {
        viewModelScope.launch {
            try {
                if (place.name.isEmpty()) {
                    _viewState.value = AddPlaceViewState.ValidationFailed(
                        AddPlaceViewState.ValidationFailed.Field.NAME
                    )
                    return@launch
                }

                _viewState.value = AddPlaceViewState.Loading
                withContext(backgroundDispatcher) { placesRepository.add(place) }
                _viewState.value = AddPlaceViewState.Success
            } catch (exception: Exception) {
                errorLogFun.invoke(exception)
                _viewState.value = AddPlaceViewState.Error(exception)
            }
        }
    }
}

sealed class AddPlaceViewState {
    object Loading : AddPlaceViewState()
    object Success : AddPlaceViewState()
    data class ValidationFailed(val invalidField: Field) : AddPlaceViewState() {
        enum class Field { NAME }
    }

    data class Error(val exception: Exception) : AddPlaceViewState()
}