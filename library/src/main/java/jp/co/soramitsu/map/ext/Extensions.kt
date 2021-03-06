package jp.co.soramitsu.map.ext

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.google.android.gms.maps.model.LatLng
import jp.co.soramitsu.map.BuildConfig
import jp.co.soramitsu.map.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*

@ColorInt
internal fun Context.colorFromTheme(@AttrRes attributeRes: Int): Int {
    val outValue = TypedValue()
    theme.resolveAttribute(attributeRes, outValue, true)
    return outValue.data
}

internal fun Context.dimenFromTheme(@AttrRes attributeRes: Int): Int {
    val outValue = TypedValue()
    theme.resolveAttribute(attributeRes, outValue, true)
    return outValue.getDimension(resources.displayMetrics).toInt()
}

internal fun View.selectableItemBackground() {
    val outValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
    setBackgroundResource(outValue.resourceId)
}

internal fun Context.getResourceIdForAttr(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.resourceId
}

internal fun Position.asLatLng(): LatLng = LatLng(latitude, longitude)

internal fun Cluster.asLatLng(): LatLng = LatLng(position.latitude, position.longitude)

/**
 *
 * Assume we have a mapping matrix
 * ```
 * _____________________________
 * |Sun|Mon|Tue|Wed|Thu|Fri|Sat|
 * -----------------------------
 * | 1 | 2 | 3 | 4 | 5 | 6 | 7 | -- for countries where week start from Sunday
 * | 7 | 1 | 2 | 3 | 4 | 5 | 6 | -- for countries where week start from Monday
 * | 2 | 3 | 4 | 5 | 6 | 7 | 1 | -- for countries where week start from Saturday
 * | 3 | 4 | 5 | 6 | 7 | 1 | 2 | -- for countries where week start from Friday
 * -----------------------------
 * ```
 *
 * &nbsp;
 *
 * Based on matrix above we can create an offset to avoid storing each row of mtx
 * ```
 * Sunday   -> offset +0
 * Monday   -> offset -1
 * Saturday -> offset +1
 * Friday   -> offset +2
 * ```
 * Plus operations are done in modular arithmetic
 *
 * &nbsp;
 *
 * Index of day is Calendar.DAY_INDEX (for instance Calendar.SUNDAY = 1).
 * Index in array will be idx = Calendar.DAY_INDEX + offset (modular plus)
 * Also we know that Sunday have index = 1. It's not that comfortable for
 * modular operations. We will do DAY_INDEX-1 to start counting from 0
 */
@ExperimentalStdlibApi
@Suppress("MagicNumber", "UseCheckOrError")
internal fun Schedule.asIntervals(
    locale: Locale = Locale.getDefault(),
    mergeDaysPredicate: (WorkDay, WorkDay) -> (Boolean) = { d1, d2 -> d1.from == d2.from && d1.to == d2.to }
): List<Pair<WorkDay, WorkDay>> {
    val offset = when (WeekFields.of(locale).firstDayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> -1
        DayOfWeek.SATURDAY -> 1
        DayOfWeek.FRIDAY -> 2
        else -> throw IllegalStateException("Failed to determinate the first day of week in current locale")
    }

    val workingCalendarDays = workingDays.map { it.weekDay }
    val nonWorkingDays = (Calendar.SUNDAY..Calendar.SATURDAY)
        .toMutableList()
        .apply { removeAll { it in workingCalendarDays } }
        .map { nonWorkingCalendarDay ->
            WorkDay(
                weekDay = nonWorkingCalendarDay,
                from = Time.NOT_SET,
                to = Time.NOT_SET,
                lunchTimeFrom = Time.NOT_SET,
                lunchTimeTo = Time.NOT_SET
            )
        }

    // day week at index 0 depends of locale
    val placeholder = WorkDay(Calendar.SUNDAY, Time.NOT_SET, Time.NOT_SET)
    val localeOrderedWeek = MutableList(7) { placeholder }
    (workingDays + nonWorkingDays).forEach { workDay ->
        val idx = ((workDay.weekDay - 1) + 7 + offset) % 7
        localeOrderedWeek[idx] = workDay
    }

    // Collapse week. Current day will be added to the last interval when
    // current day have the same working time as the start of the interval.
    // When current day will have different working time, we will start
    // a new interval with start and finish at current day
    val result = mutableListOf<Pair<WorkDay, WorkDay>>()
    result.add(localeOrderedWeek[0] to localeOrderedWeek[0])
    localeOrderedWeek.forEach { workDay ->
        val currentInterval = result.removeLast()
        val from = currentInterval.first
        val canMergeDaysIntoSameInterval = mergeDaysPredicate.invoke(from, workDay)
        if (canMergeDaysIntoSameInterval) {
            result.add(currentInterval.copy(second = workDay))
        } else {
            result.add(currentInterval)
            result.add(workDay to workDay)
        }
    }
    return result
}

fun ContentResolver.images(baseUri: Uri): List<Pair<Uri, Long>> {
    val galleryImageUrls = mutableListOf<Pair<Uri, Long>>()
    val columns = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED)
    val orderBy = MediaStore.Images.Media.DATE_ADDED
    query(baseUri, columns, null, null, "$orderBy DESC")?.use { cursor ->
        val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
        val dateAddedColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
        while (cursor.moveToNext()) {
            val imageFullUri = ContentUris.withAppendedId(baseUri, cursor.getLong(idColumn))
            val dateAdded = cursor.getLong(dateAddedColumn)
            galleryImageUrls.add(Pair(imageFullUri, dateAdded))
        }
    }
    return galleryImageUrls
}

/**
 * Will convert list of days into list of days intervals. Ordered days from D_start to D_finish
 * will be packed into interval ```[D_start, D_finish]```.
 *
 *
 * For example:
 * ```
 * Sunday, Monday, Tuesday, Friday -> ([Sunday, Tuesday], [Friday-Friday])
 * ```
 *
 * @param locale -- used to determinate a first day of the week
 */
@SuppressWarnings("MagicNumber")
fun List<DayOfWeek>.intervals(locale: Locale = Locale.getDefault()): List<Pair<DayOfWeek, DayOfWeek>> {
    if (this.isEmpty()) return emptyList()

    val firstDayOfWeek = WeekFields.of(locale).firstDayOfWeek
    val weekdaysOrderedInLocale = IntRange(0, 6)
        .map { firstDayOfWeek.plus(it.toLong()) }
        .filter { it in this }

    var firstIntervalStart = firstDayOfWeek
    while (firstIntervalStart !in this) firstIntervalStart = firstIntervalStart.plus(1)

    val intervals = mutableListOf<Pair<DayOfWeek, DayOfWeek>>()
    intervals.add(firstIntervalStart to firstIntervalStart)
    weekdaysOrderedInLocale.drop(1).forEach { dayOfWeek ->
        if (intervals.last().second.plus(1) == dayOfWeek) {
            val currentInterval = intervals.removeLast()
            intervals.add(currentInterval.copy(second = dayOfWeek))
        } else {
            intervals.add(dayOfWeek to dayOfWeek)
        }
    }

    return intervals
}

suspend fun Position.addressString(
    context: Context,
    logFun: (Throwable) -> Unit = { if (BuildConfig.DEBUG) Log.w("Geocoder", it) }
): String = withContext(Dispatchers.IO) {
    kotlin.runCatching {
        val geocoder = Geocoder(context)
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val haveAddress = addresses.isNotEmpty() && addresses[0].maxAddressLineIndex >= 0
        if (haveAddress) addresses[0].getAddressLine(0) else ""
    }.onFailure { logFun.invoke(it) }.getOrDefault("")
}

fun LatLng.toPosition(): Position = Position(latitude, longitude)
