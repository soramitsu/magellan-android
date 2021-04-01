package jp.co.soramitsu.map.presentation.places.add.schedule

import android.animation.LayoutTransition
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat.is24HourFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import jp.co.soramitsu.map.databinding.SmAddScheduleSectionViewBinding
import jp.co.soramitsu.map.model.Time
import jp.co.soramitsu.map.model.WorkDay
import java.time.DayOfWeek
import java.time.temporal.WeekFields
import java.util.*

internal class ScheduleSectionView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleRes) {

    private val weekdaysChips by lazy {
        with(binding) {
            listOf(weekday1, weekday2, weekday3, weekday4, weekday5, weekday6, weekday7)
        }
    }

    private val binding = SmAddScheduleSectionViewBinding.inflate(LayoutInflater.from(context), this)

    private var onRemoveButtonClickListener: () -> Unit = {}
    private var onWorkingDaysSelected: () -> Unit = { }

    @Suppress("MagicNumber")
    val selectedDays: List<WorkDay>
        get() {
            val is24Hours = binding.open24HoursSwitch.isChecked
            val from = if (is24Hours) {
                Time(0, 0)
            } else {
                TimeUtils.parseTime(binding.openFromEditText.text.toString())
            }
            val to = if (is24Hours) {
                Time(23, 59)
            } else {
                TimeUtils.parseTime(binding.closeAtEditText.text.toString())
            }

            val lunchTimeFrom = TimeUtils.parseTime(binding.lunchFromEditText.text.toString())
            val lunchTimeTo = TimeUtils.parseTime(binding.lunchToEditText.text.toString())
            return weekdaysChips.filter { it.isChecked }.map { chip ->
                val dayOfWeek = chip.tag as DayOfWeek
                WorkDay(
                    weekDay = dayOfWeek.asJavaCalendarValue(),
                    from = from,
                    to = to,
                    lunchTimeFrom = lunchTimeFrom,
                    lunchTimeTo = lunchTimeTo,
                )
            }
        }

    var editable: Boolean = true
        set(value) {
            field = value

            weekdaysChips.forEach { it.isCheckable = value }
            binding.open24HoursSwitch.isEnabled = value
            binding.openFromTextInputLayout.isEnabled = value
            binding.closeAtTextInputLayout.isEnabled = value
            binding.lunchTimeSwitch.isEnabled = value
            binding.lunchFromTextInputLayout.isEnabled = value
            binding.lunchToTextInputLayout.isEnabled = value
        }

    var removeButtonVisibility: Boolean = false
        set(value) {
            field = value
            binding.deleteButton.visibility = if (value) View.VISIBLE else View.GONE
            binding.topSeparator.visibility = if (value) View.VISIBLE else View.GONE
        }

    init {
        layoutTransition = LayoutTransition()

        displayLocalisedWeekdays()

        binding.open24HoursSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.openHoursFlow.visibility = if (isChecked) View.GONE else View.VISIBLE
        }

        binding.lunchTimeSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.lunchHoursFlow.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        weekdaysChips.forEach { chip ->
            chip.setOnCheckedChangeListener { _, _ -> onWorkingDaysSelected.invoke() }
        }

        binding.deleteButton.setOnClickListener { onRemoveButtonClickListener.invoke() }

        initTimeField(binding.openFromEditText)
        initTimeField(binding.closeAtEditText)

        initTimeField(binding.lunchFromEditText)
        initTimeField(binding.lunchToEditText)
    }

    fun setOnRemoveButtonClickListener(onRemoveButtonClickListener: () -> Unit) {
        this.onRemoveButtonClickListener = onRemoveButtonClickListener
    }

    fun setOnWorkingDaysSelected(onWorkingDaysSelected: () -> Unit) {
        this.onWorkingDaysSelected = onWorkingDaysSelected
    }

    @Suppress("MagicNumber")
    fun getSectionData(): SectionData {
        val is24Hours = binding.open24HoursSwitch.isChecked
        val from = if (is24Hours) {
            Time(0, 0)
        } else {
            TimeUtils.parseTime(binding.openFromEditText.text.toString())
        }
        val to = if (is24Hours) {
            Time(23, 59)
        } else {
            TimeUtils.parseTime(binding.closeAtEditText.text.toString())
        }

        val lunchTimeFrom = TimeUtils.parseTime(binding.lunchFromEditText.text.toString())
        val lunchTimeTo = TimeUtils.parseTime(binding.lunchToEditText.text.toString())

        val daysMap = mutableMapOf<DayOfWeek, SelectionState>()
        weekdaysChips
            .filter { it.isChecked }
            .map { it.tag as DayOfWeek }
            .forEach { dayOfWeek -> daysMap[dayOfWeek] = SelectionState.Selected }

        weekdaysChips
            .filter { it.isEnabled && !it.isChecked }
            .map { it.tag as DayOfWeek }
            .forEach { dayOfWeek -> daysMap[dayOfWeek] = SelectionState.NotSelected }

        weekdaysChips
            .filter { !it.isEnabled }
            .map { it.tag as DayOfWeek }
            .forEach { dayOfWeek -> daysMap[dayOfWeek] = SelectionState.SelectedInOtherSection }

        return SectionData(
            id = tag as Int,
            daysMap = daysMap,
            fromTime = from,
            toTime = to,
            lunchFromTime = lunchTimeFrom,
            lunchToTime = lunchTimeTo
        )
    }

    fun bindWithSectionData(sectionData: SectionData) = with(sectionData) {
        tag = sectionData.id

        if (fromTime != Time.NOT_SET) {
            binding.openFromEditText.setText(TimeUtils.formatTime(fromTime.hour, fromTime.minute))
        }

        if (toTime != Time.NOT_SET) {
            binding.closeAtEditText.setText(TimeUtils.formatTime(toTime.hour, toTime.minute))
        }

        if (lunchFromTime != Time.NOT_SET) {
            binding.lunchFromEditText.setText(TimeUtils.formatTime(lunchFromTime.hour, lunchFromTime.minute))
        }

        if (lunchToTime != Time.NOT_SET) {
            binding.lunchToEditText.setText(TimeUtils.formatTime(lunchToTime.hour, lunchToTime.minute))
        }

        daysMap
            .filterValues { state -> state == SelectionState.Selected }
            .map { it.key }
            .forEach { dayOfWeek ->
                weekdaysChips.find { it.tag == dayOfWeek }?.let { chip ->
                    chip.isCheckable = true
                    chip.isEnabled = true
                    chip.isChecked = true
                }
            }

        daysMap
            .filterValues { state -> state == SelectionState.NotSelected }
            .map { it.key }
            .forEach { dayOfWeek ->
                weekdaysChips.find { it.tag == dayOfWeek }?.let { chip ->
                    chip.isCheckable = true
                    chip.isEnabled = true
                    chip.isChecked = false
                }
            }

        daysMap
            .filterValues { state -> state == SelectionState.SelectedInOtherSection }
            .map { it.key }
            .forEach { dayOfWeek ->
                weekdaysChips.find { it.tag == dayOfWeek }?.let { chip ->
                    chip.isCheckable = false
                    chip.isEnabled = false
                    chip.isChecked = false
                }
            }
    }

    @Suppress("MagicNumber")
    private fun displayLocalisedWeekdays() {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        IntRange(0, 6).map { firstDayOfWeek.plus(it.toLong()) }.forEachIndexed { index, dayOfWeek ->
            weekdaysChips[index].text = dayOfWeek.shortLocalisedName(resources)
            weekdaysChips[index].tag = dayOfWeek
        }
    }

    private fun initTimeField(editText: EditText) {
        editText.setOnFocusChangeListener { v, hasFocus ->
            v.clearFocus()
            if (hasFocus) {
                val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    editText.setText(TimeUtils.formatTime(hourOfDay, minute))
                }
                TimePickerDialog(context, onTimeSetListener, 0, 0, is24HourFormat(context)).show()
            }
        }
    }
}
