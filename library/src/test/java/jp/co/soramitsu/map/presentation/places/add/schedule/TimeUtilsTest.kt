package jp.co.soramitsu.map.presentation.places.add.schedule

import com.google.common.truth.Truth.assertThat
import jp.co.soramitsu.map.model.Time
import org.junit.Ignore
import org.junit.Test
import java.util.*

class TimeUtilsTest {

    @Test
    @Ignore("Passed local, failed on CI. Need to fix: https://bakong.atlassian.net/browse/BP-1027")
    fun `time to string`() {
        Locale.setDefault(Locale("RU"))
        assertThat(TimeUtils.formatTime(4, 20)).isEqualTo("04:20")
        assertThat(TimeUtils.formatTime(16, 20)).isEqualTo("16:20")

        Locale.setDefault(Locale.US)
        assertThat(TimeUtils.formatTime(4, 20)).isEqualTo("4:20 AM")
        assertThat(TimeUtils.formatTime(16, 20)).isEqualTo("4:20 PM")
    }

    @Test
    @Ignore("Passed local, failed on CI. Need to fix: https://bakong.atlassian.net/browse/BP-1027")
    fun `parse time`() {
        Locale.setDefault(Locale("RU"))
        assertThat(TimeUtils.parseTime("04:20")).isEqualTo(Time(4, 20))
        assertThat(TimeUtils.parseTime("16:20")).isEqualTo(Time(16, 20))

        Locale.setDefault(Locale.US)
        assertThat(TimeUtils.parseTime("4:20 AM")).isEqualTo(Time(4, 20))
        assertThat(TimeUtils.parseTime("4:20 PM")).isEqualTo(Time(16, 20))
    }
}