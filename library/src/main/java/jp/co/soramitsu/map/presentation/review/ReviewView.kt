package jp.co.soramitsu.map.presentation.review

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Review
import jp.co.soramitsu.map.presentation.InitialsExtractor
import kotlinx.android.synthetic.main.sm_comment_view.view.*
import kotlinx.android.synthetic.main.sm_review_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var onUserChangeRatingListener: (Int) -> Unit = {}
    private var onShowAllReviewsButtonClickListener: () -> Unit = {}
    private var onEditUserCommentClickListener: () -> Unit = {}

    private val reviewsAdapter: ReviewsAdapter = ReviewsAdapter()

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
        reviewAndSummaryPlaceRatingBar.rating = rating
        reviewAndSummaryPlaceTotalRating.text = "%.1f".format(rating)
        reviewAndSummaryTotalReviews.text = resources
            .getQuantityString(R.plurals.sm_reviews_format, reviews.size, reviews.size)

        val userReview = reviews.find { review -> review.author.user }
        val viewState = if (userReview == null) {
            UserCommentSectionState.CanBeReviewed
        } else {
            UserCommentSectionState.PlaceReviewedByUser(userReview)
        }
        viewState.renderCommentViewState()

        if (reviews.isEmpty()) {
            reviewAndSummaryReviewSection.visibility = View.GONE
        } else {
            reviewAndSummaryReviewSection.visibility = View.VISIBLE
            val nonUserReviews = reviews
                .take(MAXIMAL_NUMBER_OF_COMMENTS)
                .toMutableList()
                .apply { remove(userReview) }
                .toList()
            reviewsAdapter.setItems(nonUserReviews)
        }

        reviewAndSummaryRateThisPlaceRatingBar.setOnRatingBarChangeListener { _, newRating, fromUser ->
            if (fromUser) {
                onUserChangeRatingListener.invoke(newRating.toInt())
                reviewAndSummaryRateThisPlaceRatingBar.rating = 0f
            }
        }

        reviewAndSummaryShowAllReviewsButton.setOnClickListener {
            onShowAllReviewsButtonClickListener.invoke()
        }

        reviewAndSummaryEditCommentButton.setOnClickListener {
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
            reviewAndSummaryUploadUserCommentProgressBar.visibility = View.VISIBLE
            reviewAndSummaryUserCommentGroup.visibility = View.GONE
            reviewAndSummaryRateThisPlaceProposalGroup.visibility = View.GONE
        }

        UserCommentSectionState.CanBeReviewed -> {
            reviewAndSummaryUploadUserCommentProgressBar.visibility = View.GONE
            reviewAndSummaryUserCommentGroup.visibility = View.GONE
            reviewAndSummaryRateThisPlaceProposalGroup.visibility = View.VISIBLE
            reviewAndSummaryRateThisPlaceRatingBar.rating = 0f
        }

        is UserCommentSectionState.PlaceReviewedByUser -> {
            reviewAndSummaryUploadUserCommentProgressBar.visibility = View.GONE
            reviewAndSummaryUserCommentGroup.visibility = View.VISIBLE
            reviewAndSummaryRateThisPlaceProposalGroup.visibility = View.GONE

            commentViewAuthorName.text = userReview.author.name
            commentViewRating.rating = userReview.rating
            commentViewCommentTextView.text = userReview.text
            commentViewInitialsTextView.text = InitialsExtractor.extract(userReview.author.name)

            val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            commentViewCommentDate.text = simpleDateFormat.format(Date(userReview.date))
        }
    }

    init {
        View.inflate(context, R.layout.sm_review_view, this)

        reviewAndSummaryCommentsRecyclerView.layoutManager = LinearLayoutManager(context)
        reviewAndSummaryCommentsRecyclerView.adapter = reviewsAdapter
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