package jp.co.soramitsu.map.ext

import com.google.common.truth.Truth.assertThat
import jp.co.soramitsu.map.model.Schedule
import jp.co.soramitsu.map.model.Time
import jp.co.soramitsu.map.model.WorkDay
import org.junit.Test
import java.time.DayOfWeek
import java.util.*

@ExperimentalStdlibApi
class ExtensionsKtTest {

    @Test
    fun `start from monday (UK)`() {
        val sunday = WorkDay(
            weekDay = Calendar.SUNDAY,
            from = Time.NOT_SET,
            to = Time.NOT_SET
        )
        val saturday = WorkDay(
            weekDay = Calendar.SATURDAY,
            from = Time.NOT_SET,
            to = Time.NOT_SET
        )
        val monday = WorkDay(
            weekDay = Calendar.MONDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val tuesday = WorkDay(
            weekDay = Calendar.TUESDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val wednesday = WorkDay(
            weekDay = Calendar.WEDNESDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val thursday = WorkDay(
            weekDay = Calendar.THURSDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val friday = WorkDay(
            weekDay = Calendar.FRIDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val intervals = Schedule(
            workingDays = listOf(
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday,
                sunday
            )
        ).asIntervals(Locale.UK)

        assertThat(intervals).isEqualTo(listOf(monday to friday, saturday to sunday))
    }

    @Test
    fun `start from sunday (US)`() {
        val sunday = WorkDay(
            weekDay = Calendar.SUNDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val saturday = WorkDay(
            weekDay = Calendar.SATURDAY,
            from = Time.NOT_SET,
            to = Time.NOT_SET
        )
        val monday = WorkDay(
            weekDay = Calendar.MONDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val tuesday = WorkDay(
            weekDay = Calendar.TUESDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val wednesday = WorkDay(
            weekDay = Calendar.WEDNESDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val thursday = WorkDay(
            weekDay = Calendar.THURSDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val friday = WorkDay(
            weekDay = Calendar.FRIDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val intervals = Schedule(
            workingDays = listOf(
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday,
                sunday
            )
        ).asIntervals(Locale.US)

        assertThat(intervals).isEqualTo(listOf(sunday to friday, saturday to saturday))
    }

    @Test
    fun `start from saturday (Egypt)`() {
        val sunday = WorkDay(
            weekDay = Calendar.SUNDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val saturday = WorkDay(
            weekDay = Calendar.SATURDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val monday = WorkDay(
            weekDay = Calendar.MONDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val tuesday = WorkDay(
            weekDay = Calendar.TUESDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val wednesday = WorkDay(
            weekDay = Calendar.WEDNESDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val thursday = WorkDay(
            weekDay = Calendar.THURSDAY,
            from = Time.NOT_SET,
            to = Time.NOT_SET
        )
        val friday = WorkDay(
            weekDay = Calendar.FRIDAY,
            from = Time.NOT_SET,
            to = Time.NOT_SET
        )
        val intervals = Schedule(
            workingDays = listOf(
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday,
                sunday
            )
        ).asIntervals(Locale.forLanguageTag("ar-EG"))

        assertThat(intervals).isEqualTo(listOf(saturday to wednesday, thursday to friday))
    }

    @Test
    fun `from monday to friday, short working day at friday`() {
        val sunday = WorkDay(
            weekDay = Calendar.SUNDAY,
            from = Time.NOT_SET,
            to = Time.NOT_SET
        )
        val saturday = WorkDay(
            weekDay = Calendar.SATURDAY,
            from = Time.NOT_SET,
            to = Time.NOT_SET
        )
        val monday = WorkDay(
            weekDay = Calendar.MONDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val tuesday = WorkDay(
            weekDay = Calendar.TUESDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val wednesday = WorkDay(
            weekDay = Calendar.WEDNESDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val thursday = WorkDay(
            weekDay = Calendar.THURSDAY,
            from = Time(5, 0),
            to = Time(20, 0)
        )
        val friday = WorkDay(
            weekDay = Calendar.FRIDAY,
            from = Time(5, 0),
            to = Time(18, 0)
        )
        val intervals = Schedule(
            workingDays = listOf(
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday,
                sunday
            )
        ).asIntervals(Locale.UK)

        assertThat(intervals).isEqualTo(
            listOf(
                monday to thursday,
                friday to friday,
                saturday to sunday
            )
        )
    }

    @Test
    fun `from monday to friday, same lunch time`() {
        val monday = WorkDay(
            weekDay = Calendar.MONDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(8, 0),
            lunchTimeTo = Time(9, 0)
        )
        val tuesday = WorkDay(
            weekDay = Calendar.TUESDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(8, 0),
            lunchTimeTo = Time(9, 0)
        )
        val wednesday = WorkDay(
            weekDay = Calendar.WEDNESDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(8, 0),
            lunchTimeTo = Time(9, 0)
        )
        val thursday = WorkDay(
            weekDay = Calendar.THURSDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(8, 0),
            lunchTimeTo = Time(9, 0)
        )
        val friday = WorkDay(
            weekDay = Calendar.FRIDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(8, 0),
            lunchTimeTo = Time(9, 0)
        )
        val saturday = WorkDay(
            weekDay = Calendar.SATURDAY,
            from = Time.NOT_SET,
            to = Time.NOT_SET
        )
        val sunday = WorkDay(
            weekDay = Calendar.SUNDAY,
            from = Time.NOT_SET,
            to = Time.NOT_SET
        )
        val intervals = Schedule(
            workingDays = listOf(
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday,
                sunday
            )
        ).asIntervals(Locale.UK) { d1, d2 -> d1.lunchTimeFrom == d2.lunchTimeFrom && d1.lunchTimeTo == d2.lunchTimeTo }

        assertThat(intervals).isEqualTo(
            listOf(
                monday to friday,
                saturday to sunday
            )
        )
    }

    @Test
    fun `from monday to friday, different lunch time`() {
        val monday = WorkDay(
            weekDay = Calendar.MONDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(8, 0),
            lunchTimeTo = Time(9, 0)
        )
        val tuesday = WorkDay(
            weekDay = Calendar.TUESDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(8, 0),
            lunchTimeTo = Time(9, 0)
        )
        val wednesday = WorkDay(
            weekDay = Calendar.WEDNESDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(8, 0),
            lunchTimeTo = Time(9, 0)
        )
        val thursday = WorkDay(
            weekDay = Calendar.THURSDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(11, 0),
            lunchTimeTo = Time(12, 0)
        )
        val friday = WorkDay(
            weekDay = Calendar.FRIDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(11, 0),
            lunchTimeTo = Time(12, 0)
        )
        val saturday = WorkDay(
            weekDay = Calendar.SATURDAY,
            from = Time(5, 0),
            to = Time(20, 0),
            lunchTimeFrom = Time(8, 0),
            lunchTimeTo = Time(9, 0)
        )
        val sunday = WorkDay(
            weekDay = Calendar.SUNDAY,
            from = Time.NOT_SET,
            to = Time.NOT_SET
        )
        val intervals = Schedule(
            workingDays = listOf(
                monday,
                tuesday,
                wednesday,
                thursday,
                friday,
                saturday,
                sunday
            )
        ).asIntervals(Locale.UK) { d1, d2 -> d1.lunchTimeFrom == d2.lunchTimeFrom && d1.lunchTimeTo == d2.lunchTimeTo }

        assertThat(intervals).isEqualTo(
            listOf(
                monday to wednesday,
                thursday to friday,
                saturday to saturday,
                sunday to sunday
            )
        )
    }

    @Test
    fun `from monday to friday`() {
        val intervals = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        ).intervals(Locale.UK)

        assertThat(intervals).isEqualTo(listOf(DayOfWeek.MONDAY to DayOfWeek.FRIDAY))
    }

    @Test
    fun `from sunday to friday (US)`() {
        val intervals = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        ).intervals(Locale.US)

        assertThat(intervals).isEqualTo(listOf(DayOfWeek.SUNDAY to DayOfWeek.FRIDAY))
    }

    @Test
    fun `from sunday to friday (UK)`() {
        val intervals = listOf(
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        ).intervals(Locale.UK)

        assertThat(intervals).isEqualTo(
            listOf(
                DayOfWeek.MONDAY to DayOfWeek.FRIDAY,
                DayOfWeek.SUNDAY to DayOfWeek.SUNDAY
            )
        )
    }

    @Test
    fun `empty interval`() {
        val intervals = listOf<DayOfWeek>().intervals(Locale.UK)
        assertThat(intervals).isEmpty()
    }

    @Test
    fun `multiple intervals`() {
        val intervals = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        ).intervals(Locale.UK)

        assertThat(intervals).isEqualTo(listOf(
            DayOfWeek.MONDAY to DayOfWeek.MONDAY,
            DayOfWeek.WEDNESDAY to DayOfWeek.FRIDAY
        ))
    }

    @Test
    fun `wrong order`() {
        val intervals = listOf(
            DayOfWeek.THURSDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.FRIDAY
        ).intervals(Locale.UK)

        assertThat(intervals).isEqualTo(listOf(
            DayOfWeek.MONDAY to DayOfWeek.MONDAY,
            DayOfWeek.WEDNESDAY to DayOfWeek.FRIDAY
        ))
    }
}