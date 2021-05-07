package jp.co.soramitsu.map.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.*

class CategoryTest {

    @Test
    fun `non default locale`() {
        val category = Category(
            1, mapOf(
                Locale.US.isO3Language to "Category name",
                Locale("ru").isO3Language to "Имя категории"
            )
        )

        assertThat(category.localisedName(Locale("ru"))).isEqualTo("Имя категории")
    }

    @Test
    fun `different locales with the same language (US, Eng)`() {
        val category = Category(
            1, mapOf(
                Locale.US.isO3Language to "Category name",
                Locale("ru").isO3Language to "Имя категории"
            )
        )

        assertThat(category.localisedName(Locale.ENGLISH)).isEqualTo("Category name")
    }

    @Test
    fun `in unknown locale used eng`() {
        val category = Category(
            1, mapOf(
                Locale.US.isO3Language to "Category name",
                Locale("ru").isO3Language to "Имя категории"
            )
        )

        assertThat(category.localisedName(Locale.KOREA)).isEqualTo("Category name")
    }
}
