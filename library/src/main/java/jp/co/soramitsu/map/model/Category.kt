package jp.co.soramitsu.map.model

import java.util.*

data class Category(
    val id: Long,
    val name: String,
    val khmerName: String = ""
) {

    fun localisedName(): String {
        val useKhmerName = Locale.getDefault().language == "km" && khmerName.isNotBlank()
        return if (useKhmerName) khmerName else name
    }

    companion object {
        val BANK = Category(-1, "Bank")
        val FOOD = Category(-2, "Food")
        val SERVICES = Category(-3, "Services")
        val SUPERMARKETS = Category(-4, "Supermarkets")
        val PHARMACY = Category(-5, "Pharmacy")
        val ENTERTAINMENT = Category(-6, "Entertainment")
        val EDUCATION = Category(-7, "Education")
        val OTHER = Category(-8, "Other")
    }
}
