package jp.co.soramitsu.map.model

class Schedule(
    val workingDays: List<WorkDay> = emptyList(),
    val open24: Boolean = false
)

data class WorkDay(
    val weekDay: Int,
    val from: Time,
    val to: Time,

    val launchTimeFrom: Time? = null,
    val launchTimeTo: Time? = null
)