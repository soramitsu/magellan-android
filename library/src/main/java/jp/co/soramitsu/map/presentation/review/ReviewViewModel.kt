package jp.co.soramitsu.map.presentation.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.data.PlacesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ReviewViewModel(
    private val placesRepository: PlacesRepository = SoramitsuMapLibraryConfig.repository,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>(ViewState.InputComment)
    val viewState: LiveData<ViewState> = _viewState

    fun addReview(placeId: String, rating: Int, comment: String) {
        execute {
            placesRepository.addReview(
                placeId = placeId,
                newRating = rating,
                comment = comment
            )
        }
    }

    fun updateReview(placeId: String, rating: Int, comment: String) {
        execute {
            placesRepository.updatePlaceRating(
                placeId = placeId,
                newRating = rating,
                comment = comment
            )
        }
    }

    private fun execute(block: () -> Unit) {
        viewModelScope.launch {
            _viewState.value = ViewState.Loading
            val viewState = withContext(backgroundDispatcher) {
                runCatching {
                    block()
                    ViewState.Submitted
                }.getOrDefault(ViewState.Error)
            }
            _viewState.value = viewState
        }
    }

    sealed class ViewState {
        object InputComment : ViewState()
        object Loading : ViewState()
        object Error : ViewState()
        object Submitted : ViewState()
    }
}
