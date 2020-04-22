package jp.co.soramitsu.feature.maps.impl.presentation.places

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.TextViewCompat
import jp.co.soramitsu.feature.maps.impl.ext.asLocalizedString
import jp.co.soramitsu.feature.maps.impl.ext.asTime
import jp.co.soramitsu.feature.maps.impl.ext.getResourceIdForAttr
import jp.co.soramitsu.feature.maps.impl.ext.toMinutesOfDay
import jp.co.soramitsu.feature.maps.impl.model.Place
import jp.co.soramitsu.feature_map_impl.R
import kotlinx.android.synthetic.main.place_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class PlaceView @JvmOverloads constructor(
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

        // we don't have working schedule in demo dataset
//        bindWorkingTime(place)
    }

    private fun bindWorkingTime(place: Place) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val minutesSinceMidnightNow = calendar.asTime().toMinutesOfDay()
        val minutesSinceMidnightOpen = place.startWorkingAtTime.toMinutesOfDay()
        val minutesSinceMidnightClose = place.finishWorkingAtTime.toMinutesOfDay()
        val openNow = minutesSinceMidnightNow in minutesSinceMidnightOpen..minutesSinceMidnightClose
        if (openNow) {
            placeWorkTimeTextView.text =
                resources.getString(R.string.sm_open_till, timeFormat.format(calendar.time))

            @StyleRes val textAppearance = context
                .getResourceIdForAttr(R.attr.soramitsuMapOpenNowTextAppearance)

            TextViewCompat.setTextAppearance(placeWorkTimeTextView, textAppearance)
        } else {
            placeWorkTimeTextView.text =
                resources.getString(R.string.sm_closed_till, timeFormat.format(calendar.time))

            @StyleRes val textAppearance = context
                .getResourceIdForAttr(R.attr.soramitsuMapCloseNowTextAppearance)

            TextViewCompat.setTextAppearance(placeWorkTimeTextView, textAppearance)
        }
    }

    init {
        View.inflate(context, R.layout.place_view, this)
    }
}