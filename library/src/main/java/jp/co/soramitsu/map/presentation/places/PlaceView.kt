package jp.co.soramitsu.map.presentation.places

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Place
import kotlinx.android.synthetic.main.sm_place_view.view.*

internal class PlaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    fun bind(place: Place) {
        placeNameTextView.text = place.name

        val placeAddress = if (place.address.isNotBlank()) {
            resources.getString(R.string.sm_category_address, place.category.name, place.address)
        } else {
            place.category.name
        }
        placeAddressTextView.text = placeAddress

        placeRatingBar.rating = place.rating
        placeRatingTextView.text = place.rating.toString()
        placeReviewsTextView.text =
            context?.resources?.getQuantityString(
                R.plurals.sm_review,
                place.reviews.size,
                place.reviews.size
            )
    }

    init {
        View.inflate(context, R.layout.sm_place_view, this)
    }
}