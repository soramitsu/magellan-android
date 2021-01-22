package jp.co.soramitsu.map.presentation.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.data.PlacesRepository
import jp.co.soramitsu.map.model.Review
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class ReviewListViewModel(
    private val placesRepository: PlacesRepository = SoramitsuMapLibraryConfig.repository,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _viewState = MutableLiveData<ReviewListViewState>()
    val viewState: LiveData<ReviewListViewState> = _viewState

    fun reloadReviews(placeId: String) {
        viewModelScope.launch {
            _viewState.value = ReviewListViewState.Loading
            _viewState.value = loadAllReviews(placeId)
        }
    }

    private suspend fun loadAllReviews(placeId: String) = withContext(backgroundDispatcher) {
        try {
            val reviews = placesRepository.getPlaceReviews(placeId).popUserReviewOnTop()
            ReviewListViewState.Content(reviews)
        } catch(exception: Exception) {
            ReviewListViewState.Error(exception)
        }
    }

    private fun List<Review>.popUserReviewOnTop(): List<Review> {
        val allReviews = this
        val userReview = allReviews.find { it.author.user } ?: return allReviews
        return listOf(userReview) + (allReviews - userReview)
    }

    internal sealed class ReviewListViewState {
        object Loading : ReviewListViewState()
        data class Error(val exception: Exception) : ReviewListViewState()
        data class Content(val reviews: List<Review>): ReviewListViewState()
        object NoReviewsYet: ReviewListViewState()
    }
}