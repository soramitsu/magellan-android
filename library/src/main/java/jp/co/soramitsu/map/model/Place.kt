package jp.co.soramitsu.map.model

data class Place(
    val name: String,
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
)
