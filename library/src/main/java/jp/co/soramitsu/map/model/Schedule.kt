package jp.co.soramitsu.map.model

data class Schedule(
    val workingDays: List<WorkDay> = emptyList(),
    val open24: Boolean = false
)

data class WorkDay(
    val weekDay: Int,
    val from: Time,
    val to: Time,

    val lunchTimeFrom: Time? = null,
    val lunchTimeTo: Time? = null
)
