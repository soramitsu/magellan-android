package jp.co.soramitsu.map.presentation.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Review
import java.text.SimpleDateFormat
import java.util.*

internal class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    private val items: MutableList<Review> = mutableListOf()

    fun setItems(newItems: List<Review>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilCallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.sm_comment_view, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    internal class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarImageView: ImageView = itemView.findViewById(R.id.commentViewAvatar)
        private val initialsTextView: TextView = itemView.findViewById(R.id.commentViewInitialsTextView)
        private val reviewerNameTextView: TextView = itemView.findViewById(R.id.commentViewAuthorName)
        private val ratingView: RatingBar = itemView.findViewById(R.id.commentViewRating)
        private val reviewDateTextView: TextView = itemView.findViewById(R.id.commentViewCommentDate)
        private val reviewTextView: TextView = itemView.findViewById(R.id.commentViewCommentTextView)

        private val simpleDataFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

        fun bind(review: Review) {
            reviewerNameTextView.text = review.author.name
            ratingView.rating = review.rating
            reviewDateTextView.text = simpleDataFormat.format(Date(review.date))
            reviewTextView.text = review.text

            initialsTextView.text = InitialsExtractor.extract(review.author.name)
        }
    }

    private class DiffUtilCallback(
        private val beforeList: List<Review>,
        private val afterList: List<Review>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = beforeList.size
        override fun getNewListSize(): Int = afterList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return beforeList[oldItemPosition] == afterList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return beforeList[oldItemPosition] == afterList[newItemPosition]
        }
    }

    private companion object {
        private const val DATE_FORMAT = "dd MMM yyyy"
    }
}