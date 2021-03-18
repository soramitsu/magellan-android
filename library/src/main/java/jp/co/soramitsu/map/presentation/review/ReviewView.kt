package jp.co.soramitsu.map.presentation.review

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.databinding.SmReviewViewBinding
import jp.co.soramitsu.map.model.Review
import jp.co.soramitsu.map.presentation.InitialsExtractor
import java.text.SimpleDateFormat
import java.util.*

internal class ReviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onUserChangeRatingListener: (Int) -> Unit = {}
    private var onShowAllReviewsButtonClickListener: () -> Unit = {}
    private var onEditUserCommentClickListener: () -> Unit = {}

    private val reviewsAdapter: ReviewsAdapter = ReviewsAdapter()

    private val binding = SmReviewViewBinding.inflate(LayoutInflater.from(context), this)

    fun setOnUserChangeRatingListener(onUserChangeRatingListener: (Int) -> Unit) {
        this.onUserChangeRatingListener = onUserChangeRatingListener
    }

    fun setOnShowAllReviewsButtonClickListener(onShowAllReviewsButtonClickListener: () -> Unit) {
        this.onShowAllReviewsButtonClickListener = onShowAllReviewsButtonClickListener
    }

    fun setOnEditUserCommentClickListener(onEditUserCommentClickListener: () -> Unit) {
        this.onEditUserCommentClickListener = onEditUserCommentClickListener
    }

    fun bind(rating: Float, reviews: List<Review>) {
        binding.reviewAndSummaryPlaceRatingBar.rating = rating
        binding.reviewAndSummaryPlaceTotalRating.text = "%.1f".format(rating)
        binding.reviewAndSummaryTotalReviews.text = resources
            .getQuantityString(R.plurals.sm_reviews_format, reviews.size, reviews.size)

        val userReview = reviews.find { review -> review.author.user }
        val viewState = if (userReview == null) {
            UserCommentSectionState.CanBeReviewed
        } else {
            UserCommentSectionState.PlaceReviewedByUser(userReview)
        }
        viewState.renderCommentViewState()

        if (reviews.isEmpty()) {
            binding.reviewAndSummaryReviewSection.visibility = View.GONE
        } else {
            binding.reviewAndSummaryReviewSection.visibility = View.VISIBLE
            val nonUserReviews = reviews
                .take(MAXIMAL_NUMBER_OF_COMMENTS)
                .toMutableList()
                .apply { remove(userReview) }
                .toList()
            reviewsAdapter.setItems(nonUserReviews)
        }

        binding.reviewAndSummaryRateThisPlaceRatingBar.setOnRatingBarChangeListener { _, newRating, fromUser ->
            if (fromUser) {
                onUserChangeRatingListener.invoke(newRating.toInt())
                binding.reviewAndSummaryRateThisPlaceRatingBar.rating = 0f
            }
        }

        binding.reviewAndSummaryShowAllReviewsButton.setOnClickListener {
            onShowAllReviewsButtonClickListener.invoke()
        }

        binding.reviewAndSummaryEditCommentButton.setOnClickListener {
            onEditUserCommentClickListener.invoke()
        }
    }

    fun showUploadingReviewIndicator(placeUpdateInProgress: Boolean) {
        if (placeUpdateInProgress) UserCommentSectionState.Loading.renderCommentViewState()
    }

    /**
     * Don't modify comment block visibility directly. Use this method
     */
    private fun UserCommentSectionState.renderCommentViewState() = when(this) {
        UserCommentSectionState.Loading -> {
            binding.reviewAndSummaryUploadUserCommentProgressBar.visibility = View.VISIBLE
            binding.reviewAndSummaryUserCommentGroup.visibility = View.GONE
            binding.reviewAndSummaryRateThisPlaceProposalGroup.visibility = View.GONE
        }

        UserCommentSectionState.CanBeReviewed -> {
            binding.reviewAndSummaryUploadUserCommentProgressBar.visibility = View.GONE
            binding.reviewAndSummaryUserCommentGroup.visibility = View.GONE
            binding.reviewAndSummaryRateThisPlaceProposalGroup.visibility = View.VISIBLE
            binding.reviewAndSummaryRateThisPlaceRatingBar.rating = 0f
        }

        is UserCommentSectionState.PlaceReviewedByUser -> {
            binding.reviewAndSummaryUploadUserCommentProgressBar.visibility = View.GONE
            binding.reviewAndSummaryUserCommentGroup.visibility = View.VISIBLE
            binding.reviewAndSummaryRateThisPlaceProposalGroup.visibility = View.GONE

            binding.commentViewRoot.commentViewAuthorName.text = userReview.author.name
            binding.commentViewRoot.commentViewRating.rating = userReview.rating
            binding.commentViewRoot.commentViewCommentTextView.text = userReview.text
            binding.commentViewRoot.commentViewInitialsTextView.text = InitialsExtractor.extract(userReview.author.name)

            val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            binding.commentViewRoot.commentViewCommentDate.text = simpleDateFormat.format(Date(userReview.date))
        }
    }.also {
        SoramitsuMapLibraryConfig.logger.log("ReviewView", this.toString())
    }

    init {
        binding.reviewAndSummaryCommentsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.reviewAndSummaryCommentsRecyclerView.adapter = reviewsAdapter
    }

    /**
     * We will use sealed class to prevent inconsistent view state. It'll guarantee
     * that we won't show "user comment" over "proposal to rate this place"
     */
    private sealed class UserCommentSectionState {
        object Loading : UserCommentSectionState()
        data class PlaceReviewedByUser(val userReview: Review): UserCommentSectionState()
        object CanBeReviewed: UserCommentSectionState()
    }

    private companion object {
        private const val DATE_FORMAT = "dd MMM yyyy"
        private const val MAXIMAL_NUMBER_OF_COMMENTS = 3
    }
}