package jp.co.soramitsu.map.presentation.places

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Review
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

    private val reviewsAdapter: ReviewsAdapter = ReviewsAdapter()

    fun setOnUserChangeRatingListener(onUserChangeRatingListener: (Int) -> Unit) {
        this.onUserChangeRatingListener = onUserChangeRatingListener
    }

    fun setOnShowAllReviewsButtonClickListener(onShowAllReviewsButtonClickListener: () -> Unit) {
        this.onShowAllReviewsButtonClickListener = onShowAllReviewsButtonClickListener
    }

    fun bind(rating: Float, reviews: List<Review>) {
        reviewAndSummaryPlaceRatingBar.rating = rating
        reviewAndSummaryPlaceTotalRating.text = "%.1f".format(rating)
        reviewAndSummaryTotalReviews.text = resources
            .getQuantityString(R.plurals.sm_reviews_format, reviews.size, reviews.size)

        val userReview = reviews.find { review -> review.author.user }
        reviewAndSummaryUserCommentGroup.visibility =
            if (userReview != null) View.VISIBLE else View.GONE
        reviewAndSummaryRateThisPlaceProposalGroup.visibility =
            if (userReview == null) View.VISIBLE else View.GONE

        userReview?.let {
            commentViewAuthorName.text = userReview.author.name
            commentViewRating.rating = userReview.rating
            commentViewCommentTextView.text = userReview.text
            commentViewInitialsTextView.text = InitialsExtractor.extract(userReview.author.name)

            val simpleDateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            commentViewCommentDate.text = simpleDateFormat.format(Date(userReview.date))
        }

        if (reviews.isEmpty()) {
            reviewAndSummaryReviewSection.visibility = View.GONE
        } else {
            reviewAndSummaryReviewSection.visibility = View.VISIBLE
            reviewsAdapter.setItems(reviews.take(MAXIMAL_NUMBER_OF_COMMENTS))
        }

        reviewAndSummaryRateThisPlaceRatingBar.setOnRatingBarChangeListener { _, newRating, fromUser ->
            if (fromUser) {
                onUserChangeRatingListener.invoke(newRating.toInt())
            }
        }

        reviewAndSummaryShowAllReviewsButton.setOnClickListener {
            this.onShowAllReviewsButtonClickListener.invoke()
        }
    }

    init {
        View.inflate(context, R.layout.sm_review_view, this)

        reviewAndSummaryCommentsRecyclerView.layoutManager = LinearLayoutManager(context)
        reviewAndSummaryCommentsRecyclerView.adapter = reviewsAdapter
    }

    private companion object {
        private const val DATE_FORMAT = "dd MMM yyyy"
        private const val MAXIMAL_NUMBER_OF_COMMENTS = 3
    }
}