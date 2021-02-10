package jp.co.soramitsu.map.presentation.places.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.soramitsu.map.Logger
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.data.PlacesRepository
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.presentation.places.add.image.ImageUriToByteArrayConverter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class AddPlaceViewModel(
    private val imageUriToByteArrayConverter: ImageUriToByteArrayConverter,
    private val placesRepository: PlacesRepository = SoramitsuMapLibraryConfig.repository,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val logger: Logger = SoramitsuMapLibraryConfig.logger
) : ViewModel() {

    private val _viewState = MutableLiveData<AddPlaceViewState>()
    internal val viewState: LiveData<AddPlaceViewState> = _viewState

    internal var logoUriString: String = ""
    internal var promoImageUriString: String = ""

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
                withContext(backgroundDispatcher) {
                    placesRepository.add(
                        place.copy(
                            logo = imageUriToByteArrayConverter.convert(logoUriString),
                            promoImage = imageUriToByteArrayConverter.convert(promoImageUriString),
                        )
                    )
                }
                _viewState.value = AddPlaceViewState.Success
            } catch (exception: Exception) {
                logger.log("AddPlace", exception)
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