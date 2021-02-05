package jp.co.soramitsu.map.presentation.places.add

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.model.Schedule
import jp.co.soramitsu.map.presentation.places.DetailedScheduleAdapter
import jp.co.soramitsu.map.presentation.places.add.schedule.generateLaunchTimeFields
import jp.co.soramitsu.map.presentation.places.add.schedule.generateWorkingDaysFields
import kotlinx.android.synthetic.main.sm_set_working_hours_section_view.view.*

class ScheduleSectionView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defaultStyleResId: Int = 0
) : ConstraintLayout(context, attributeSet, defaultStyleResId) {

    private val logger = SoramitsuMapLibraryConfig.logger

    @ExperimentalStdlibApi
    var schedule: Schedule? = null
        set(value) {
            field = value

            val viewState = if (value != null && value.workingDays.isNotEmpty()) {
                SectionState.DisplaySchedule(value)
            } else {
                SectionState.NoScheduleYet
            }

            viewState.render()
        }

    private var onAddButtonClickListener: () -> Unit = {}
    private var onChangeScheduleButtonClickListener: () -> Unit = {}

    private val adapter = DetailedScheduleAdapter(DetailedScheduleAdapter.Orientation.VERTICAL)

    fun setOnAddButtonClickListener(onAddButtonClickListener: () -> Unit) {
        this.onAddButtonClickListener = onAddButtonClickListener
    }

    fun setOnChangeScheduleButtonClickListener(onChangeScheduleButtonClickListener: () -> Unit) {
        this.onChangeScheduleButtonClickListener = onChangeScheduleButtonClickListener
    }

    @ExperimentalStdlibApi
    private fun SectionState.render() = when (this) {
        is SectionState.DisplaySchedule -> {
            logger.log(TAG, "transition to display schedule")

            transitionTo(R.layout.sm_set_working_hours_section_display_schedule_state)

            val workingScheduleAsPairsList = schedule.generateWorkingDaysFields(context)
            val lunchScheduleAsPairsList = schedule.generateLaunchTimeFields(context)
            adapter.update(workingScheduleAsPairsList + lunchScheduleAsPairsList)
        }
        SectionState.NoScheduleYet -> {
            logger.log(TAG, "transition to schedule not set")

            transitionTo(R.layout.sm_set_working_hours_section_display_schedule_state)
        }
    }

    private fun transitionTo(@LayoutRes targetStateResId: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.load(context, targetStateResId)
        TransitionManager.beginDelayedTransition(this)
        constraintSet.applyTo(this)
    }

    init {
        View.inflate(context, R.layout.sm_set_working_hours_section_view, this)

        workingHoursRecyclerView.adapter = adapter

        addScheduleTextView.setOnClickListener { onAddButtonClickListener.invoke() }
        changeScheduleTextView.setOnClickListener { onChangeScheduleButtonClickListener.invoke() }
    }

    private sealed class SectionState {
        data class DisplaySchedule(val schedule: Schedule) : SectionState()
        object NoScheduleYet : SectionState()
    }

    private companion object {
        private const val TAG = "AddPlace"
    }
}