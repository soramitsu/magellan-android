package jp.co.soramitsu.map.model

import java.util.*

data class Place(
    val name: String,
    val khmerName: String = "",
    val category: Category,
    val position: Position,
    val schedule: Schedule = Schedule(),
    val rating: Float = 0f,
    val phone: String = "",
    val website: String = "",
    val facebook: String = "",
    val address: String = "",
    val reviews: List<Review> = emptyList(),
    val id: String = ""
) {

    fun localisedName(): String {
        val useKhmerName = Locale.getDefault().language == "km" && khmerName.isNotBlank()
        return if (useKhmerName) khmerName else name
    }
}
