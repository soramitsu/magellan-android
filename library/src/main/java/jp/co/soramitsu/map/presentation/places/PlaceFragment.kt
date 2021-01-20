package jp.co.soramitsu.map.presentation.places

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.view.doOnLayout
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.ext.asIntervals
import jp.co.soramitsu.map.ext.colorFromTheme
import jp.co.soramitsu.map.ext.dimenFromTheme
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.model.Schedule
import jp.co.soramitsu.map.model.Time
import jp.co.soramitsu.map.model.WorkDay
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import jp.co.soramitsu.map.presentation.review.EditReviewFragment
import jp.co.soramitsu.map.presentation.review.ReviewFragment
import kotlinx.android.synthetic.main.sm_place_bottom_sheet.*
import java.text.SimpleDateFormat
import java.util.*

internal class PlaceFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: SoramitsuMapViewModel

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val detailedScheduleAdapter = DetailedScheduleAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_place_bottom_sheet, container, false)
    }

    @ExperimentalStdlibApi
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
            viewModel.uploadReviewInProgress().observe(viewLifecycleOwner, Observer { placeUpdateInProgress ->
                reviewView.showUploadingReviewIndicator(placeUpdateInProgress)
            })
            viewModel.editPlaceReviewClicked().observe(viewLifecycleOwner, Observer { place ->
                val userReview = place.reviews.find { review -> review.author.user }
                val initialRating = userReview?.rating ?: place.rating
                ReviewFragment().withArguments(
                    placeId = place.id,
                    placeName = place.name,
                    comment = userReview?.text.orEmpty(),
                    initialRating = initialRating.toInt()
                ).show(parentFragmentManager, "AddPlaceReviewFragment")
            })
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val peekHeight = dialog.context.dimenFromTheme(R.attr.sm_placeBottomSheetPeekHeight)
            bottomSheetDialog.behavior.peekHeight = peekHeight

            val touchOutside = dialog.window?.decorView?.findViewById<View>(R.id.touch_outside)
            touchOutside?.setOnTouchListener { v, event ->
                activity?.dispatchTouchEvent(event)
                false
            }

            dialog.setCanceledOnTouchOutside(false)
        }
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        viewModel.onPlaceSelected(null)
    }

    @ExperimentalStdlibApi
    private fun bindBottomSheetWithPlace(place: Place) {
        // header info
        placeNameTextView.text = place.localisedName()

        val placeAddress = if (place.address.isNotBlank()) {
            resources.getString(
                R.string.sm_category_address,
                place.category.localisedName(),
                place.address
            )
        } else {
            place.category.localisedName()
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
        bindRating(place)

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

    private fun bindRating(place: Place) {
        placeRatingBar.rating = place.rating
        placeRatingTextView.text = "%.1f".format(place.rating)
        placeReviewsTextView.text = resources.getQuantityString(
            R.plurals.sm_reviews_format, place.reviews.size, place.reviews.size
        )

        reviewView.bind(place.rating, place.reviews)
        reviewView.setOnShowAllReviewsButtonClickListener {
            Log.d("Review", "Show all button clicked")
            activity?.onUserInteraction()
        }

        reviewView.setOnEditUserCommentClickListener {
            activity?.onUserInteraction()
            EditReviewFragment().show(parentFragmentManager, "EditReviewMenu")
        }

        reviewView.setOnUserChangeRatingListener { newRating ->
            activity?.onUserInteraction()
            viewModel.placeSelected().value?.let { place ->
                ReviewFragment().withArguments(
                    placeId = place.id,
                    placeName = place.name,
                    initialRating = newRating
                ).show(parentFragmentManager, "AddPlaceReviewFragment")
            }
        }
    }

    @ExperimentalStdlibApi
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
                val scheduleAsString =
                    resources.getString(R.string.sm_daily_interval, fromTime, toTime)

                val haveLunchTime = today.lunchTimeFrom != null && today.lunchTimeTo != null
                if (haveLunchTime) {
                    val lunchFromTime =
                        dateFormat.format(Date(today.lunchTimeFrom!!.inMilliseconds()))
                    val lunchToTime = dateFormat.format(Date(today.lunchTimeTo!!.inMilliseconds()))
                    val lunchScheduleAsString =
                        getString(R.string.sm_lunch_time) + " $lunchFromTime–$lunchToTime"
                    additionalInfoOpenHours.text = "$scheduleAsString\n$lunchScheduleAsString"
                } else {
                    additionalInfoOpenHours.text = scheduleAsString
                }
            }
        }

        if (SoramitsuMapLibraryConfig.enableDetailedSchedule) {
            displayDetailedScheduleInformation(schedule)
        }
    }

    @ExperimentalStdlibApi
    private fun displayDetailedScheduleInformation(schedule: Schedule) {
        if (schedule.isStableDailySchedule()) {
            additionalInfoOpenHours.setOnClickListener(null)
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                additionalInfoOpenHours, R.drawable.sm_ic_clock, 0, 0, 0
            )
        } else {
            val animatedArrowDownToUp = ContextCompat.getDrawable(
                requireContext(), R.drawable.sm_ic_keyboard_arrow_down_24_animated
            ) as AnimatedVectorDrawable
            val animatedArrowUpToDown = ContextCompat.getDrawable(
                requireContext(), R.drawable.sm_ic_keyboard_arrow_up_24_animated
            ) as AnimatedVectorDrawable
            val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.sm_ic_clock)
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                additionalInfoOpenHours, iconDrawable, null, animatedArrowDownToUp, null
            )
            additionalInfoOpenHours.setOnClickListener {
                if (additionalInfoOpenHoursDetails.visibility == View.VISIBLE) {
                    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        additionalInfoOpenHours, iconDrawable, null, animatedArrowUpToDown, null
                    )
                    animatedArrowUpToDown.start()
                    additionalInfoOpenHoursDetails.visibility = View.GONE
                } else {
                    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        additionalInfoOpenHours, iconDrawable, null, animatedArrowDownToUp, null
                    )
                    animatedArrowDownToUp.start()
                    additionalInfoOpenHoursDetails.visibility = View.VISIBLE

                    val workingScheduleAsPairsList = generateWorkingDaysFields(schedule)
                    val lunchScheduleAsPairsList = generateLaunchTimeFields(schedule)
                    detailedScheduleAdapter.update(workingScheduleAsPairsList + lunchScheduleAsPairsList)
                }
            }
        }
    }

    @ExperimentalStdlibApi
    private fun generateWorkingDaysFields(schedule: Schedule): List<Pair<String, String>> {
        return schedule.asIntervals().map { interval ->
            val workDay = interval.first
            val workingInterval = workDay.from != Time.NOT_SET
                    && workDay.to != Time.NOT_SET

            // 6am - 9pm
            val workingTimeInterval = if (workingInterval) {
                val fromTime = dateFormat.format(Date(workDay.from.inMilliseconds()))
                val toTime = dateFormat.format(Date(workDay.to.inMilliseconds()))
                resources.getString(R.string.sm_working_time_interval, fromTime, toTime)
            } else {
                resources.getString(R.string.sm_closed)
            }

            val singleDayInterval = interval.first == interval.second
            if (singleDayInterval) {
                // sun
                Pair(calendarDayAsString(workDay.weekDay), workingTimeInterval)
            } else {
                // sun - fri
                val fromDay = calendarDayAsString(interval.first.weekDay)
                val toDay = calendarDayAsString(interval.second.weekDay)
                val workingDaysInterval = resources.getString(
                    R.string.sm_working_days_interval, fromDay, toDay
                )
                Pair(workingDaysInterval, workingTimeInterval)
            }
        }
    }

    @ExperimentalStdlibApi
    private fun generateLaunchTimeFields(schedule: Schedule): List<Pair<String, String>> = schedule
        .asIntervals { workDay1, workDay2 ->
            workDay1.lunchTimeFrom == workDay2.lunchTimeFrom &&
                    workDay1.lunchTimeTo == workDay2.lunchTimeTo
        }
        .filter { interval ->
            interval.first.lunchTimeFrom != null
                    && interval.first.lunchTimeFrom != Time.NOT_SET
                    && interval.first.lunchTimeTo != null
                    && interval.first.lunchTimeTo != Time.NOT_SET
                    && interval.second.lunchTimeFrom != null
                    && interval.second.lunchTimeFrom != Time.NOT_SET
                    && interval.second.lunchTimeTo != null
                    && interval.second.lunchTimeTo != Time.NOT_SET
        }
        .map { interval ->
            val firstDay = interval.first
            val lastDay = interval.second
            val fromTime =
                dateFormat.format(Date(firstDay.lunchTimeFrom!!.inMilliseconds()))
            val toTime =
                dateFormat.format(Date(firstDay.lunchTimeTo!!.inMilliseconds()))
            // 6am - 9pm
            val launchTimeString = resources.getString(
                R.string.sm_lunch_time_interval,
                fromTime,
                toTime
            )
            if (firstDay == lastDay) {
                // lunch time (mon)
                val lunchTimeSingleDay = resources.getString(
                    R.string.sm_lunch_time_single_day, firstDay
                )
                Pair(lunchTimeSingleDay, launchTimeString)
            } else {
                // lunch time (mon-fri)
                val lunchDaysInterval = resources.getString(
                    R.string.sm_lunch_days_interval,
                    calendarDayAsString(firstDay.weekDay),
                    calendarDayAsString(lastDay.weekDay)
                )
                Pair(lunchDaysInterval, launchTimeString)
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

    private fun Schedule.isStableDailySchedule(): Boolean {
        return workingDays.size == 7 && workingDays.all { workDay ->
            workDay.from == workingDays[0].from && workDay.to == workingDays[0].to
        }
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
                workDay.isLunchTimeNow() -> {
                    val lunchTime = resources.getString(R.string.sm_lunch_time)
                    val lunchEndTime =
                        dateFormat.format(Date(workDay.lunchTimeTo!!.inMilliseconds()))
                    val till = resources.getString(R.string.sm_till, lunchEndTime)
                    val colorAccent = requireContext().colorFromTheme(R.attr.colorAccent)
                    val spannableString = SpannableStringBuilder()
                        .color(colorAccent) { append(lunchTime) }
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

    private fun WorkDay.isLunchTimeNow(calendar: Calendar = Calendar.getInstance()): Boolean {
        if (lunchTimeFrom == null || lunchTimeTo == null) return false

        val timeNow = Time(
            hour = calendar.get(Calendar.HOUR_OF_DAY),
            minute = calendar.get(Calendar.MINUTE)
        )
        return timeNow >= lunchTimeFrom && timeNow <= lunchTimeTo
    }
}