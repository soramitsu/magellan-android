package jp.co.soramitsu.map.presentation.places

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class InitialsExtractorTest {

    @Test
    fun `more than two words`() {
        assertThat(InitialsExtractor.extract("Ivan Petrovich Sidorov")).isEqualTo("IP")
    }

    @Test
    fun `two words`() {
        assertThat(InitialsExtractor.extract("Ivan Petrovich")).isEqualTo("IP")
    }

    @Test
    fun `one word`() {
        assertThat(InitialsExtractor.extract("Ivan")).isEqualTo("I")
    }

    @Test
    fun `empty input`() {
        assertThat(InitialsExtractor.extract("")).isEqualTo("")
        assertThat(InitialsExtractor.extract(" ")).isEqualTo("")
    }
}