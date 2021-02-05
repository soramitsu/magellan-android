package jp.co.soramitsu.map.presentation.places.add.schedule

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import jp.co.soramitsu.map.model.Time
import jp.co.soramitsu.map.model.WorkDay
import org.junit.Rule
import org.junit.Test
import java.time.DayOfWeek
import java.util.*

class PlaceProposalViewModelTest {

    private val viewModel = PlaceProposalViewModel(logFun = {})

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun `initialized view model has one default section`() {
        assertThat(sections()).isNotNull()
        assertThat(sections()).isNotEmpty()
    }

    @Test
    fun `when nothing selected, all days are available for selection in default section`() {
        assertThat(sections().first().daysMap.values).contains(SelectionState.NotSelected)
        assertThat(sections().first().daysMap.values).containsNoneOf(
            SelectionState.Selected,
            SelectionState.SelectedInOtherSection
        )
    }


    @Test
    fun `when add section, all selected elements of current section marked as SelectedInOtherSection in a new section`() {
        val sectionsBeforeUpdate = sections()
        val updatedDaysMap = sectionsBeforeUpdate[0].daysMap.toMutableMap()
        updatedDaysMap[DayOfWeek.MONDAY] = SelectionState.Selected
        updatedDaysMap[DayOfWeek.TUESDAY] = SelectionState.Selected
        updatedDaysMap[DayOfWeek.WEDNESDAY] = SelectionState.Selected

        val updatedFirstSection = sectionsBeforeUpdate[0].copy(daysMap = updatedDaysMap)
        viewModel.addSection(updatedFirstSection)

        val sectionsAfterUpdate = sections()
        assertThat(sectionsAfterUpdate.first().daysMap[DayOfWeek.MONDAY]).isEqualTo(SelectionState.Selected)
        assertThat(sectionsAfterUpdate.first().daysMap[DayOfWeek.TUESDAY]).isEqualTo(SelectionState.Selected)
        assertThat(sectionsAfterUpdate.first().daysMap[DayOfWeek.WEDNESDAY]).isEqualTo(
            SelectionState.Selected
        )
        assertThat(sectionsAfterUpdate.last().daysMap[DayOfWeek.MONDAY]).isEqualTo(SelectionState.SelectedInOtherSection)
        assertThat(sectionsAfterUpdate.last().daysMap[DayOfWeek.TUESDAY]).isEqualTo(SelectionState.SelectedInOtherSection)
        assertThat(sectionsAfterUpdate.last().daysMap[DayOfWeek.WEDNESDAY]).isEqualTo(SelectionState.SelectedInOtherSection)
    }

    @Test
    fun `when remove section, all selected items of removed section become available for selection in other sections`() {
        // fill first section
        val updatedDaysMapOfFirstSection = sections()[0].daysMap.toMutableMap()
        updatedDaysMapOfFirstSection[DayOfWeek.MONDAY] = SelectionState.Selected
        updatedDaysMapOfFirstSection[DayOfWeek.TUESDAY] = SelectionState.Selected
        updatedDaysMapOfFirstSection[DayOfWeek.WEDNESDAY] = SelectionState.Selected
        val updatedFirstSection = sections()[0].copy(daysMap = updatedDaysMapOfFirstSection)
        viewModel.addSection(updatedFirstSection)

        // fill second section
        val updatedDaysMapOfSecondSection = sections()[1].daysMap.toMutableMap()
        updatedDaysMapOfSecondSection[DayOfWeek.THURSDAY] = SelectionState.Selected
        updatedDaysMapOfSecondSection[DayOfWeek.FRIDAY] = SelectionState.Selected
        val updatedSecondSection = sections()[1].copy(daysMap = updatedDaysMapOfSecondSection)
        viewModel.addSection(updatedSecondSection)

        // check that we can't select already selected days in the third section
        val sectionsAfterAdd = sections()
        assertThat(sectionsAfterAdd.last().daysMap[DayOfWeek.MONDAY]).isEqualTo(SelectionState.SelectedInOtherSection)
        assertThat(sectionsAfterAdd.last().daysMap[DayOfWeek.TUESDAY]).isEqualTo(SelectionState.SelectedInOtherSection)
        assertThat(sectionsAfterAdd.last().daysMap[DayOfWeek.WEDNESDAY]).isEqualTo(SelectionState.SelectedInOtherSection)
        assertThat(sectionsAfterAdd.last().daysMap[DayOfWeek.THURSDAY]).isEqualTo(SelectionState.SelectedInOtherSection)
        assertThat(sectionsAfterAdd.last().daysMap[DayOfWeek.FRIDAY]).isEqualTo(SelectionState.SelectedInOtherSection)

        // remove second section
        viewModel.removeSectionWithId(sectionsAfterAdd[1].id)

        // assert that selected in second section days are available for selection after the second section removal
        val sectionsAfterRemove = sections()
        assertThat(sectionsAfterRemove.last().daysMap[DayOfWeek.THURSDAY]).isEqualTo(SelectionState.NotSelected)
        assertThat(sectionsAfterRemove.last().daysMap[DayOfWeek.FRIDAY]).isEqualTo(SelectionState.NotSelected)
    }

    @Test
    fun `get schedule when all days set`() {
        val updatedDaysMap = sections().first().daysMap.toMutableMap()
        updatedDaysMap[DayOfWeek.SUNDAY] = SelectionState.Selected
        updatedDaysMap[DayOfWeek.MONDAY] = SelectionState.Selected
        updatedDaysMap[DayOfWeek.TUESDAY] = SelectionState.Selected
        updatedDaysMap[DayOfWeek.WEDNESDAY] = SelectionState.Selected
        updatedDaysMap[DayOfWeek.THURSDAY] = SelectionState.Selected
        updatedDaysMap[DayOfWeek.FRIDAY] = SelectionState.Selected
        updatedDaysMap[DayOfWeek.SATURDAY] = SelectionState.Selected

        viewModel.onSaveButtonClick(
            sections().first().copy(
                daysMap = updatedDaysMap,
                fromTime = Time(9, 0),
                toTime = Time(18, 0),
                lunchFromTime = Time(12, 0),
                lunchToTime = Time(13, 0),
            )
        )

        val workDay: (Int) -> WorkDay = { weekDay ->
            WorkDay(
                weekDay = weekDay,
                from = Time(9, 0),
                to = Time(18, 0),
                lunchTimeFrom = Time(12, 0),
                lunchTimeTo = Time(13, 0),
            )
        }

        val schedule = viewModel.schedule.value
        assertThat(schedule?.open24).isFalse()
        assertThat(schedule?.workingDays).isEqualTo(
            listOf(
                workDay(Calendar.SUNDAY),
                workDay(Calendar.MONDAY),
                workDay(Calendar.TUESDAY),
                workDay(Calendar.WEDNESDAY),
                workDay(Calendar.THURSDAY),
                workDay(Calendar.FRIDAY),
                workDay(Calendar.SATURDAY),
            )
        )
    }

    private fun sections() = viewModel.sections.value
        ?: throw AssertionError("There is no sections provided by view model")
}