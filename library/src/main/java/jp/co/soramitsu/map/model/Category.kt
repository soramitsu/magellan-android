package jp.co.soramitsu.map.model

import java.io.Serializable
import java.util.*

data class Category(
    val id: Long,
    val names: Map<String, String>
) : Serializable {

    fun localisedName(locale: Locale = Locale.getDefault()): String {
        return names.getOrElse(locale.isO3Language) {
            names[Locale.US.isO3Language].orEmpty()
        }
    }

    companion object {
        val BANK = Category(-1, mapOf(Locale.US.isO3Language to "Bank"))
        val FOOD = Category(-2, mapOf(Locale.US.isO3Language to "Food"))
        val SERVICES = Category(-3, mapOf(Locale.US.isO3Language to "Services"))
        val SUPERMARKETS = Category(-4, mapOf(Locale.US.isO3Language to "Supermarkets"))
        val PHARMACY = Category(-5, mapOf(Locale.US.isO3Language to "Pharmacy"))
        val ENTERTAINMENT = Category(-6, mapOf(Locale.US.isO3Language to "Entertainment"))
        val EDUCATION = Category(-7, mapOf(Locale.US.isO3Language to "Education"))
        val OTHER = Category(-8, mapOf(Locale.US.isO3Language to "Other"))
    }
}
