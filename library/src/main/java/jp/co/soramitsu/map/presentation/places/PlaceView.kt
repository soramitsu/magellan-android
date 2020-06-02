package jp.co.soramitsu.map.presentation.places

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.TextViewCompat
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.ext.asLocalizedString
import jp.co.soramitsu.map.ext.asTime
import jp.co.soramitsu.map.ext.getResourceIdForAttr
import jp.co.soramitsu.map.ext.toMinutesOfDay
import jp.co.soramitsu.map.model.Place
import kotlinx.android.synthetic.main.sm_place_view.view.*
import java.text.SimpleDateFormat
import java.util.*

internal class PlaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    fun bind(place: Place) {
        placeNameTextView.text = place.name
        placeTypeTextView.setText(place.category.asLocalizedString())
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