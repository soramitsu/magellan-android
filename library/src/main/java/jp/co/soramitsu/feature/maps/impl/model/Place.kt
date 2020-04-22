package jp.co.soramitsu.feature.maps.impl.model

import java.util.*

data class Place(
    val name: String,
    val category: Category,
    val position: Position,
    val startWorkingAtTime: Time = Time(0, 0),
    val finishWorkingAtTime: Time = Time(0, 0),
    val workingDays: List<Int> = listOf(
        Calendar.SUNDAY,
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY
    ),
    val rating: Float = 0f,
    val phone: String = "",
    val website: String = "",
    val facebook: String = "",
    val address: String = "",
    val reviews: List<Review> = emptyList()
)
