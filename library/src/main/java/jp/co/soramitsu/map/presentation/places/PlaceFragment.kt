package jp.co.soramitsu.map.presentation.places

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.color
import androidx.core.view.doOnLayout
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.ext.colorFromTheme
import jp.co.soramitsu.map.ext.dimenFromTheme
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.model.Schedule
import jp.co.soramitsu.map.model.Time
import jp.co.soramitsu.map.model.WorkDay
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import kotlinx.android.synthetic.main.sm_place_bottom_sheet.*
import java.text.SimpleDateFormat
import java.util.*

internal class PlaceFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: SoramitsuMapViewModel

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val detailedScheduleAdapter = DetailedScheduleAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sm_place_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnLayout {
            val parent = (view.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)
            dialog?.window?.setDimAmount(0f)
        }

        additionalInfoOpenHoursDetails.layoutManager = LinearLayoutManager(context)
        additionalInfoOpenHoursDetails.adapter = detailedScheduleAdapter

        parentFragmentManager.fragments.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            viewModel = ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)

            viewModel.placeSelected().value?.let { place -> bindBottomSheetWithPlace(place) }
            viewModel.placeSelected().observe(viewLifecycleOwner, Observer { place ->
                place?.let { bindBottomSheetWithPlace(place) }
            })
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val peekHeight = dialog.context.dimenFromTheme(R.attr.sm_placeBottomSheetPeekHeight)
            bottomSheetDialog.behavior.peekHeight = peekHeight
        }
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        viewModel.onPlaceSelected(null)
    }

    private fun bindBottomSheetWithPlace(place: Place) {
        // header info
        placeNameTextView.text = place.name

        val placeAddress = if (place.address.isNotBlank()) {
            resources.getString(R.string.sm_category_address, place.category.name, place.address)
        } else {
            place.category.name
        }
        placeAddressTextView.text = placeAddress

        placeRatingBar.rating = place.rating
        placeRatingTextView.text = place.rating.toString()
        placeReviewsTextView.text =
            context?.resources?.getQuantityString(
                R.plurals.sm_review,
                place.reviews.size,
                place.reviews.size
            )

        // additional info
        additionalInfoMobilePhone.visibility =
            if (place.phone.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoWebsite.visibility =
            if (place.website.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoFacebook.visibility =
            if (place.facebook.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoAddress.visibility =
            if (place.address.isEmpty()) View.GONE else View.VISIBLE
        placeIsWorkingNowTextView.visibility =
            if (place.schedule.workingDays.isNotEmpty() || place.schedule.open24) View.VISIBLE else View.GONE

        bindIsOpenNowField(place.schedule)
        bindFullScheduleField(place.schedule)

        additionalInfoMobilePhone.text = place.phone
        additionalInfoWebsite.text = place.website
        additionalInfoFacebook.text = "facebook.com/${place.facebook}"
        additionalInfoAddress.text = place.address

        additionalInfoMobilePhone.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${place.phone}")
            })
        }

        additionalInfoWebsite.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(place.website)
            })
        }

        additionalInfoFacebook.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("facebook://facebook.com/${place.facebook}")
            }
            if (facebookIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(facebookIntent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://facebook.com/${place.facebook}")
                })
            }
        }

        additionalInfoAddress.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:0,0?q=${Uri.encode(place.address)}")
                `package` = "com.google.android.apps.maps"
            })
        }

        closePlaceInfoButton.setOnClickListener {
            dismiss()
        }
    }

    private fun bindFullScheduleField(schedule: Schedule) {
        additionalInfoOpenHoursDetails.visibility = View.GONE
        additionalInfoOpenHours.visibility = View.GONE
        val todayDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val today = schedule.workingDays.find { workDay -> workDay.weekDay == todayDayOfWeek }
        if (!schedule.open24 && today == null) return

        additionalInfoOpenHours.visibility = View.VISIBLE

        if (schedule.open24) {
            val open24Hours = getString(R.string.sm_open) + " " + getString(R.string.sm_24_hours)
            additionalInfoOpenHours.text = open24Hours
            additionalInfoOpenHours.setOnClickListener(null)
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                additionalInfoOpenHours,
                R.drawable.sm_ic_clock, 0, 0, 0
            )
            return
        }
        if (schedule.workingDays.isNotEmpty()) {
            if (today != null) {
                val fromTime = dateFormat.format(Date(today.from.inMilliseconds()))
                val toTime = dateFormat.format(Date(today.to.inMilliseconds()))
                val scheduleAsString = resources.getString(R.string.sm_daily_interval, fromTime, toTime)

                val haveLaunchTime = schedule.workingDays[0].launchTimeFrom != null
                        && schedule.workingDays[0].launchTimeTo != null
                if (haveLaunchTime) {
                    val launchFromTime =
                        dateFormat.format(Date(today.launchTimeFrom!!.inMilliseconds()))
                    val launchToTime = dateFormat.format(Date(today.launchTimeTo!!.inMilliseconds()))
                    val launchScheduleAsString = getString(R.string.sm_launch_time) + " $launchFromTime– $launchToTime"
                    additionalInfoOpenHours.text = "$scheduleAsString\n$launchScheduleAsString"
                } else {
                    additionalInfoOpenHours.text = scheduleAsString
                }
            }
        }

        // temp solution. We will show detailed schedule in the next release
        if (true) return

        if (schedule.isStableDailySchedule()) {
            additionalInfoOpenHours.setOnClickListener(null)
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                additionalInfoOpenHours,
                R.drawable.sm_ic_clock, 0, 0, 0
            )
        } else {
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                additionalInfoOpenHours,
                R.drawable.sm_ic_clock, 0, R.drawable.sm_ic_keyboard_arrow_down_24, 0
            )
            additionalInfoOpenHours.setOnClickListener {
                if (additionalInfoOpenHoursDetails.visibility == View.VISIBLE) {
                    additionalInfoOpenHoursDetails.visibility = View.GONE
                } else {
                    val scheduleAsPairsList = schedule.workingDays.map { workDay ->
                        val fromTime = dateFormat.format(Date(workDay.from.inMilliseconds()))
                        val toTime = dateFormat.format(Date(workDay.to.inMilliseconds()))
                        val scheduleAsString = resources.getString(R.string.sm_daily_interval, fromTime, toTime)
                        Pair(
                            calendarDayAsString(workDay.weekDay),
                            scheduleAsString
                        )
                    }

                    val workingDays = schedule.workingDays.map { it.weekDay }
                    val dayOffs = (Calendar.SUNDAY..Calendar.SATURDAY)
                        .filter { it !in workingDays }
                        .map { dayOff ->
                            Pair(
                                calendarDayAsString(dayOff),
                                resources.getString(R.string.sm_closed)
                            )
                        }

                    val haveLaunchTime = schedule.workingDays[0].launchTimeFrom != null
                            && schedule.workingDays[0].launchTimeTo != null
                    val launchTime = if (haveLaunchTime) {
                        val fromTime =
                            dateFormat.format(Date(schedule.workingDays[0].launchTimeFrom!!.inMilliseconds()))
                        val toTime = dateFormat.format(Date(schedule.workingDays[0].launchTimeTo!!.inMilliseconds()))
                        val scheduleAsString = "$fromTime – $toTime"
                        listOf(Pair(getString(R.string.sm_launch_time), scheduleAsString))
                    } else {
                        emptyList()
                    }

                    additionalInfoOpenHoursDetails.visibility = View.VISIBLE
                    detailedScheduleAdapter.update(scheduleAsPairsList + dayOffs + launchTime)
                }
            }
        }
    }

    private fun calendarDayAsString(calendarDay: Int) = when (calendarDay) {
        Calendar.SUNDAY -> getString(R.string.sm_sun)
        Calendar.MONDAY -> getString(R.string.sm_mon)
        Calendar.TUESDAY -> getString(R.string.sm_tue)
        Calendar.WEDNESDAY -> getString(R.string.sm_wed)
        Calendar.THURSDAY -> getString(R.string.sm_thu)
        Calendar.FRIDAY -> getString(R.string.sm_fri)
        Calendar.SATURDAY -> getString(R.string.sm_sat)
        else -> ""
    }

    private fun Schedule.isStableDailySchedule() = workingDays.size == 7 && workingDays.all { workDay ->
        workDay.from == workingDays[0].from && workDay.to == workingDays[0].to
    }

    private fun bindIsOpenNowField(schedule: Schedule) {
        if (schedule.open24) {
            val open = resources.getString(R.string.sm_open)
            val colorAccent = requireContext().colorFromTheme(R.attr.colorAccent)
            val allDayLong = resources.getString(R.string.sm_24_hours)
            val spannableString = SpannableStringBuilder()
                .color(colorAccent) { append(open) }
                .append(' ')
                .append(allDayLong)
            placeIsWorkingNowTextView.text = spannableString
        } else {
            val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val workDay = schedule.workingDays.find { workDay -> workDay.weekDay == today }
            when {
                workDay == null -> placeIsWorkingNowTextView.visibility = View.GONE
                workDay.isLaunchTimeNow() -> {
                    val launchTime = resources.getString(R.string.sm_launch_time)
                    val launchEndTime = dateFormat.format(Date(workDay.launchTimeTo!!.inMilliseconds()))
                    val till = resources.getString(R.string.sm_till, launchEndTime)
                    val colorAccent = requireContext().colorFromTheme(R.attr.colorAccent)
                    val spannableString = SpannableStringBuilder()
                        .color(colorAccent) { append(launchTime) }
                        .append(' ')
                        .append(till)
                    placeIsWorkingNowTextView.text = spannableString
                }
                workDay.isWorkTime() -> {
                    val open = resources.getString(R.string.sm_open)
                    val closeTime = dateFormat.format(Date(workDay.to.inMilliseconds()))
                    val till = resources.getString(R.string.sm_till, closeTime)
                    val colorAccent = requireContext().colorFromTheme(R.attr.colorAccent)
                    val spannableString = SpannableStringBuilder()
                        .color(colorAccent) { append(open) }
                        .append(' ')
                        .append(till)
                    placeIsWorkingNowTextView.text = spannableString
                }
                else -> {
                    val closed = resources.getString(R.string.sm_closed)
                    val openTime = dateFormat.format(Date(workDay.from.inMilliseconds()))
                    val till = resources.getString(R.string.sm_till, openTime)
                    val colorAccent = requireContext().colorFromTheme(R.attr.colorAccent)
                    val spannableString = SpannableStringBuilder()
                        .color(colorAccent) { append(closed) }
                        .append(' ')
                        .append(till)
                    placeIsWorkingNowTextView.text = spannableString
                }
            }
        }
    }

    private fun WorkDay.isWorkTime(calendar: Calendar = Calendar.getInstance()): Boolean {
        val timeNow = Time(
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE)
        )
        return timeNow >= from && timeNow <= to
    }

    private fun WorkDay.isLaunchTimeNow(calendar: Calendar = Calendar.getInstance()): Boolean {
        if (launchTimeFrom == null || launchTimeTo == null) return false

        val timeNow = Time(
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE)
        )
        return timeNow >= launchTimeFrom && timeNow <= launchTimeTo
    }
}