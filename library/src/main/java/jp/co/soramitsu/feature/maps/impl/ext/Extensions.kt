package jp.co.soramitsu.feature.maps.impl.ext

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import jp.co.soramitsu.feature.maps.impl.model.Category
import jp.co.soramitsu.feature.maps.impl.model.Time
import jp.co.soramitsu.feature_map_impl.R
import java.util.*

fun Time.toMinutesOfDay() = hour * 60 + minute

fun Calendar.withTime(time: Time) {
    this[Calendar.HOUR_OF_DAY] = time.hour
    this[Calendar.MINUTE] = time.minute
}

fun Calendar.asTime(): Time {
    val hour = this[Calendar.HOUR_OF_DAY]
    val minute = this[Calendar.MINUTE]
    return Time(hour = hour, minute = minute)
}

@StringRes
fun Category.asLocalizedString() = when (this) {
    Category.BANK -> R.string.sm_category_bank
    Category.FOOD -> R.string.sm_category_food
    Category.EDUCATION -> R.string.sm_category_education
    Category.ENTERTAINMENT -> R.string.sm_category_entertainment
    Category.PHARMACY -> R.string.sm_category_pharmacy
    Category.SERVICES -> R.string.sm_category_services
    Category.SUPERMARKETS -> R.string.sm_category_supermarkets
    Category.OTHER -> R.string.sm_category_other
}

fun View.selectableItemBackground() {
    val outValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
    setBackgroundResource(outValue.resourceId)
}

fun Context.getResourceIdForAttr(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.resourceId
}
