package jp.co.soramitsu.map.presentation.places.add.schedule

import android.content.Context
import android.content.res.Resources
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.ext.asIntervals
import jp.co.soramitsu.map.model.Schedule
import jp.co.soramitsu.map.model.Time
import java.text.SimpleDateFormat
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

private fun DayOfWeek.localisedName(resources: Resources): String = when (this) {
    DayOfWeek.SUNDAY -> resources.getString(R.string.sm_sun)
    DayOfWeek.MONDAY -> resources.getString(R.string.sm_mon)
    DayOfWeek.TUESDAY -> resources.getString(R.string.sm_tue)
    DayOfWeek.WEDNESDAY -> resources.getString(R.string.sm_wed)
    DayOfWeek.THURSDAY -> resources.getString(R.string.sm_thu)
    DayOfWeek.FRIDAY -> resources.getString(R.string.sm_fri)
    DayOfWeek.SATURDAY -> resources.getString(R.string.sm_sat)
}

fun calendarDayToDayOfWeek(calendarDay: Int) = when (calendarDay) {
    Calendar.SUNDAY -> DayOfWeek.SUNDAY
    Calendar.MONDAY -> DayOfWeek.MONDAY
    Calendar.TUESDAY -> DayOfWeek.TUESDAY
    Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
    Calendar.THURSDAY -> DayOfWeek.THURSDAY
    Calendar.FRIDAY -> DayOfWeek.FRIDAY
    Calendar.SATURDAY -> DayOfWeek.SATURDAY
    else -> throw IllegalArgumentException("Invalid calendar day value: $calendarDay . Must be one of Calendar constants")
}

@ExperimentalStdlibApi
fun Schedule.generateWorkingDaysFields(context: Context): List<Pair<String, String>> {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return asIntervals().map { interval ->
        val workDay = interval.first
        val workingInterval = workDay.from != Time.NOT_SET &&
                workDay.to != Time.NOT_SET

        // 6am - 9pm
        val workingTimeInterval = if (workingInterval) {
            val fromTime = dateFormat.format(Date(workDay.from.inMilliseconds()))
            val toTime = dateFormat.format(Date(workDay.to.inMilliseconds()))
            context.getString(R.string.sm_working_time_interval, fromTime, toTime)
        } else {
            context.getString(R.string.sm_closed)
        }

        val singleDayInterval = interval.first == interval.second
        if (singleDayInterval) {
            // sun
            Pair(
                calendarDayToDayOfWeek(workDay.weekDay).localisedName(context.resources),
                workingTimeInterval
            )
        } else {
            // sun - fri
            val workingDaysInterval = context.getString(
                R.string.sm_working_days_interval,
                calendarDayToDayOfWeek(interval.first.weekDay).localisedName(context.resources),
                calendarDayToDayOfWeek(interval.second.weekDay).localisedName(context.resources)
            )
            Pair(workingDaysInterval, workingTimeInterval)
        }
    }
}

@ExperimentalStdlibApi
fun Schedule.generateLaunchTimeFields(context: Context): List<Pair<String, String>> {
    return asIntervals { workDay1, workDay2 ->
        workDay1.lunchTimeFrom == workDay2.lunchTimeFrom &&
                workDay1.lunchTimeTo == workDay2.lunchTimeTo
    }.filter { interval ->
        interval.first.lunchTimeFrom != null &&
                interval.first.lunchTimeFrom != Time.NOT_SET &&
                interval.first.lunchTimeTo != null &&
                interval.first.lunchTimeTo != Time.NOT_SET &&
                interval.second.lunchTimeFrom != null &&
                interval.second.lunchTimeFrom != Time.NOT_SET &&
                interval.second.lunchTimeTo != null &&
                interval.second.lunchTimeTo != Time.NOT_SET
    }.map { interval ->
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val firstDay = interval.first
        val lastDay = interval.second
        val fromTime =
            dateFormat.format(Date(firstDay.lunchTimeFrom!!.inMilliseconds()))
        val toTime =
            dateFormat.format(Date(firstDay.lunchTimeTo!!.inMilliseconds()))
        // 6am - 9pm
        val launchTimeString = context.getString(R.string.sm_lunch_time_interval, fromTime, toTime)
        if (firstDay == lastDay) {
            // lunch time (mon)
            val lunchTimeSingleDay = context.getString(R.string.sm_lunch_time_single_day, firstDay)
            Pair(lunchTimeSingleDay, launchTimeString)
        } else {
            // lunch time (mon-fri)
            val lunchDaysInterval = context.getString(
                R.string.sm_lunch_days_interval,
                calendarDayToDayOfWeek(firstDay.weekDay).localisedName(context.resources),
                calendarDayToDayOfWeek(lastDay.weekDay).localisedName(context.resources),
            )
            Pair(lunchDaysInterval, launchTimeString)
        }
    }
}
