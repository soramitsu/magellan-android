package jp.co.soramitsu.map.presentation.places.add.schedule

import android.content.res.Resources
import jp.co.soramitsu.map.R
import java.time.DayOfWeek
import java.util.*

fun DayOfWeek.asJavaCalendarValue() = when (this) {
    DayOfWeek.SUNDAY -> Calendar.SUNDAY
    DayOfWeek.MONDAY -> Calendar.MONDAY
    DayOfWeek.TUESDAY -> Calendar.TUESDAY
    DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
    DayOfWeek.THURSDAY -> Calendar.THURSDAY
    DayOfWeek.FRIDAY -> Calendar.FRIDAY
    DayOfWeek.SATURDAY -> Calendar.SATURDAY
}

fun DayOfWeek.shortLocalisedName(resources: Resources): String = when (this) {
    DayOfWeek.SUNDAY -> resources.getString(R.string.sm_sun_short)
    DayOfWeek.MONDAY -> resources.getString(R.string.sm_mon_short)
    DayOfWeek.TUESDAY -> resources.getString(R.string.sm_tue_short)
    DayOfWeek.WEDNESDAY -> resources.getString(R.string.sm_wed_short)
    DayOfWeek.THURSDAY -> resources.getString(R.string.sm_thu_short)
    DayOfWeek.FRIDAY -> resources.getString(R.string.sm_fri_short)
    DayOfWeek.SATURDAY -> resources.getString(R.string.sm_sat_short)
}

fun calendarDayToDayOfWeek(calendarDay: Int) = when (calendarDay) {
    Calendar.SUNDAY -> DayOfWeek.SUNDAY
    Calendar.MONDAY -> DayOfWeek.MONDAY
    Calendar.TUESDAY -> DayOfWeek.TUESDAY
    Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
    Calendar.THURSDAY -> DayOfWeek.THURSDAY
    Calendar.FRIDAY -> DayOfWeek.FRIDAY
    Calendar.SATURDAY -> DayOfWeek.SATURDAY
    else -> throw IllegalArgumentException()
}
