package jp.co.soramitsu.map.presentation.places.add.schedule

import jp.co.soramitsu.map.model.Time
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.FormatStyle

object TimeUtils {

    fun formatTime(hour: Int, minute: Int): String {
        return LocalTime
            .of(hour, minute)
            .format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
    }

    fun parseTime(localTimeString: String): Time =
        try {
            val localDateTime = LocalTime.parse(
                localTimeString,
                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            )
            Time(hour = localDateTime.hour, minute = localDateTime.minute)
        } catch (exception: DateTimeParseException) {
            Time.NOT_SET
        }
}
