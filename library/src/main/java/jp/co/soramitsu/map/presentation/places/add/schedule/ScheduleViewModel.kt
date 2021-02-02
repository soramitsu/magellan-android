package jp.co.soramitsu.map.presentation.places.add.schedule

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.co.soramitsu.map.BuildConfig
import jp.co.soramitsu.map.model.Schedule
import jp.co.soramitsu.map.model.Time
import jp.co.soramitsu.map.model.WorkDay
import java.time.DayOfWeek

class ScheduleViewModel(
    private val logFun: (String) -> Unit = { if (BuildConfig.DEBUG) Log.d("Schedule", it) }
) : ViewModel() {

    val schedule: Schedule
        get() {
            val sections = _sections.value
            val open24Hours = sections?.all {
                // all working days start from 00:00 and finishes at 23:59
                it.fromTime == Time.NOT_SET && it.toTime == Time.NOT_SET ||
                        it.fromTime == Time(0, 0) && it.toTime == Time(23, 59)
            } ?: false
            return Schedule(
                open24 = open24Hours,
                workingDays = sections?.map { it.toWorkDays() }?.flatten().orEmpty()
            )
        }

    private val _sections = MutableLiveData(listOf(SectionData(SectionData.nextId++)))
    val sections: LiveData<List<SectionData>> = _sections

    fun onSectionChanged(sectionData: SectionData) {
        logFun.invoke("onSectionChanged")
        logSections()

        applySectionChanges(sectionData)

        logFun.invoke("After apply changes")
        logSections()
    }

    fun onSaveButtonClick(lastSectionData: SectionData) {
        logFun.invoke("Save button click")
        logSections()

        applySectionChanges(lastSectionData)

        logFun.invoke("After add")
        logSections()
    }

    fun addSection(currentSectionData: SectionData) {
        logFun.invoke("Add section")
        logFun.invoke("Current state")
        logSections()

        applySectionChanges(currentSectionData)

        val sections = _sections.value.orEmpty().toMutableList()
        sections.add(generateNewSection())

        _sections.value = sections

        logFun.invoke("After add")
        logSections()
    }

    fun removeSectionWithId(sectionId: Int) {
        logFun.invoke("Remove section $sectionId")
        logFun.invoke("Current state")
        logSections()

        _sections.value?.let { value ->
            val sections = value.toMutableList()
            sections.find { it.id == sectionId }?.let { sectionToRemove ->
                sections.remove(sectionToRemove)

                val selectedInThisSection = sectionToRemove.daysMap
                    .filter { it.value == SelectionState.Selected }
                    .map { it.key }

                sections.forEachIndexed { index, sectionData ->
                    val newDaysMap = sectionData.daysMap.toMutableMap()
                    selectedInThisSection.forEach { newDaysMap[it] = SelectionState.NotSelected }
                    sections[index] = sectionData.copy(daysMap = newDaysMap)
                }
            }

            _sections.value = sections
        }

        logFun.invoke("After remove")
        logSections()
    }

    private fun SectionData.toWorkDays(): List<WorkDay> = daysMap
        .filterValues { it == SelectionState.Selected }
        .keys
        .map { dayOfWeek ->
            WorkDay(
                weekDay = dayOfWeek.asJavaCalendarValue(),
                from = fromTime,
                to = toTime,
                lunchTimeFrom = lunchFromTime,
                lunchTimeTo = lunchToTime
            )
        }

    private fun logSections() {
        _sections.value?.forEach {
            logFun.invoke("Section ${it.id}")
            logFun.invoke(it.daysMap.entries.sortedBy { it.key }.map { it.value }
                .joinToString(separator = " "))
        }
    }

    private fun applySectionChanges(updateSectionData: SectionData) {
        val sections = _sections.value.orEmpty().toMutableList()

        val idx = sections.indexOfFirst { it.id == updateSectionData.id }
        if (idx >= 0) sections[idx] = updateSectionData

        val selectedInThisSection = updateSectionData.daysMap
            .filter { it.value == SelectionState.Selected }
            .map { it.key }

        sections.forEachIndexed { index, sectionData ->
            if (sectionData != updateSectionData) {
                val updatedDaysMap = sectionData.daysMap.mapValues { entry ->
                    if (entry.key in selectedInThisSection) {
                        SelectionState.SelectedInOtherSection
                    } else {
                        entry.value
                    }
                }
                sections[index] = sectionData.copy(daysMap = updatedDaysMap)
            }
        }

        _sections.value = sections
    }

    private fun generateNewSection(): SectionData {
        val sections = _sections.value.orEmpty().toMutableList()
        val daysMap = sections.last().daysMap.mapValues { entry ->
            if (entry.value == SelectionState.NotSelected) {
                SelectionState.NotSelected
            } else {
                SelectionState.SelectedInOtherSection
            }
        }
        return SectionData(SectionData.nextId++, daysMap = daysMap)
    }
}

sealed class SelectionState {
    object Selected : SelectionState() {
        override fun toString() = "+"
    }

    object NotSelected : SelectionState() {
        override fun toString() = "-"
    }

    object SelectedInOtherSection : SelectionState() {
        override fun toString() = "x"
    }
}

data class SectionData(
    val id: Int,
    val daysMap: Map<DayOfWeek, SelectionState> = mapOf<DayOfWeek, SelectionState>(
        DayOfWeek.SUNDAY to SelectionState.NotSelected,
        DayOfWeek.MONDAY to SelectionState.NotSelected,
        DayOfWeek.TUESDAY to SelectionState.NotSelected,
        DayOfWeek.WEDNESDAY to SelectionState.NotSelected,
        DayOfWeek.THURSDAY to SelectionState.NotSelected,
        DayOfWeek.FRIDAY to SelectionState.NotSelected,
        DayOfWeek.SATURDAY to SelectionState.NotSelected,
    ),
    val fromTime: Time = Time.NOT_SET,
    val toTime: Time = Time.NOT_SET,
    val lunchFromTime: Time = Time.NOT_SET,
    val lunchToTime: Time = Time.NOT_SET
) {

    companion object {
        var nextId: Int = 1
    }
}
