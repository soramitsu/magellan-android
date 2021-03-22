package jp.co.soramitsu.map.presentation.places

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.core.view.doOnLayout
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.databinding.SmPlaceBottomSheetBinding
import jp.co.soramitsu.map.ext.colorFromTheme
import jp.co.soramitsu.map.ext.dimenFromTheme
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.model.Schedule
import jp.co.soramitsu.map.model.Time
import jp.co.soramitsu.map.model.WorkDay
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import jp.co.soramitsu.map.presentation.places.add.schedule.generateLaunchTimeFields
import jp.co.soramitsu.map.presentation.places.add.schedule.generateWorkingDaysFields
import jp.co.soramitsu.map.presentation.review.EditReviewFragment
import jp.co.soramitsu.map.presentation.review.ReviewFragment
import jp.co.soramitsu.map.presentation.review.ReviewListFragment
import java.text.SimpleDateFormat
import java.util.*

internal class PlaceFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: SoramitsuMapViewModel

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val detailedScheduleAdapter = DetailedScheduleAdapter()

    private var _binding: SmPlaceBottomSheetBinding? = null
    private val binding get() = _binding!!

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

        _binding = SmPlaceBottomSheetBinding.bind(view)

        view.doOnLayout {
            val parent = (view.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)
            dialog?.window?.setDimAmount(0f)
        }

        binding.additionalInfoOpenHoursDetails.layoutManager = LinearLayoutManager(context)
        binding.additionalInfoOpenHoursDetails.adapter = detailedScheduleAdapter

        parentFragmentManager.fragments.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            viewModel = ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)

            viewModel.placeSelected().value?.let { place -> bindBottomSheetWithPlace(place) }
            viewModel.placeSelected().observe(viewLifecycleOwner) { place ->
                place?.let { bindBottomSheetWithPlace(place) }
            }
            viewModel.uploadReviewInProgress().observe(viewLifecycleOwner) { placeUpdateInProgress ->
                binding.reviewView.showUploadingReviewIndicator(placeUpdateInProgress)
            }
            viewModel.editPlaceReviewClicked().observe(viewLifecycleOwner) { place ->
                val initialRating = place.userReview?.rating ?: place.rating
                ReviewFragment().withArguments(
                    placeId = place.id,
                    placeName = place.name,
                    comment = place.userReview?.text.orEmpty(),
                    edit = place.userReview != null,
                    initialRating = initialRating.toInt()
                ).show(parentFragmentManager, "AddPlaceReviewFragment")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
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
        binding.placeNameTextView.text = place.localisedName()

        val placeAddress = if (place.address.isNotBlank()) {
            resources.getString(
                R.string.sm_category_address,
                place.category.localisedName(),
                place.address
            )
        } else {
            place.category.localisedName()
        }
        binding.placeAddressTextView.text = placeAddress

        binding.placeRatingBar.rating = place.rating
        binding.placeRatingTextView.text = place.rating.toString()
        val allReviews = (listOf(place.userReview) + place.otherReviews).filterNotNull()
        binding.placeReviewsTextView.text = context?.resources?.getQuantityString(
            R.plurals.sm_review, allReviews.size, allReviews.size
        )

        // additional info
        binding.additionalInfoMobilePhone.visibility =
            if (place.phone.isEmpty()) View.GONE else View.VISIBLE
        binding.additionalInfoWebsite.visibility =
            if (place.website.isEmpty()) View.GONE else View.VISIBLE
        binding.additionalInfoFacebook.visibility =
            if (place.facebook.isEmpty()) View.GONE else View.VISIBLE
        binding.additionalInfoAddress.visibility =
            if (place.address.isEmpty()) View.GONE else View.VISIBLE
        binding.placeIsWorkingNowTextView.visibility =
            if (place.schedule.workingDays.isNotEmpty() || place.schedule.open24) View.VISIBLE else View.GONE

        bindIsOpenNowField(place.schedule)
        bindFullScheduleField(place.schedule)
        bindRating(place)

        binding.additionalInfoMobilePhone.text = place.phone
        binding.additionalInfoWebsite.text = place.website
        binding.additionalInfoFacebook.text = "facebook.com/${place.facebook}"
        binding.additionalInfoAddress.text = place.address

        binding.additionalInfoMobilePhone.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${place.phone}")
            })
        }

        binding.additionalInfoWebsite.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(place.website)
            })
        }

        binding.additionalInfoFacebook.setOnClickListener {
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

        binding.additionalInfoAddress.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:0,0?q=${Uri.encode(place.address)}")
                `package` = "com.google.android.apps.maps"
            })
        }

        binding.closePlaceInfoButton.setOnClickListener { dismiss() }
    }

    private fun bindRating(place: Place) {
        binding.placeRatingBar.rating = place.rating
        binding.placeRatingTextView.text = "%.1f".format(place.rating)
        val allReviews = (listOf(place.userReview) + place.otherReviews).filterNotNull()
        binding.placeReviewsTextView.text = resources.getQuantityString(
            R.plurals.sm_reviews_format, allReviews.size, allReviews.size
        )

        binding.reviewView.bind(place.rating, allReviews)
        binding.reviewView.setOnShowAllReviewsButtonClickListener {
            activity?.onUserInteraction()
            ReviewListFragment().show(parentFragmentManager, "ReviewListBottomSheetFragment")
        }

        binding.reviewView.setOnEditUserCommentClickListener {
            activity?.onUserInteraction()
            EditReviewFragment().show(parentFragmentManager, "EditReviewMenu")
        }

        binding.reviewView.setOnUserChangeRatingListener { newRating ->
            activity?.onUserInteraction()
            viewModel.placeSelected().value?.let { place ->
                ReviewFragment().withArguments(
                    placeId = place.id,
                    placeName = place.name,
                    initialRating = newRating,
                    edit = place.userReview != null
                ).show(parentFragmentManager, "AddPlaceReviewFragment")
            }
        }
    }

    @ExperimentalStdlibApi
    private fun bindFullScheduleField(schedule: Schedule) {
        binding.additionalInfoOpenHoursDetails.visibility = View.GONE
        binding.additionalInfoOpenHours.visibility = View.GONE
        val todayDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val today = schedule.workingDays.find { workDay -> workDay.weekDay == todayDayOfWeek }
        if (!schedule.open24 && today == null) return

        binding.additionalInfoOpenHours.visibility = View.VISIBLE

        if (schedule.open24) {
            val open24Hours = getString(R.string.sm_open) + " " + getString(R.string.sm_24_hours)
            binding.additionalInfoOpenHours.text = open24Hours
            binding.additionalInfoOpenHours.setOnClickListener(null)
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                binding.additionalInfoOpenHours,
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
                    binding.additionalInfoOpenHours.text = "$scheduleAsString\n$lunchScheduleAsString"
                } else {
                    binding.additionalInfoOpenHours.text = scheduleAsString
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
            binding.additionalInfoOpenHours.setOnClickListener(null)
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                binding.additionalInfoOpenHours, R.drawable.sm_ic_clock, 0, 0, 0
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
                binding.additionalInfoOpenHours, iconDrawable, null, animatedArrowDownToUp, null
            )
            binding.additionalInfoOpenHours.setOnClickListener {
                if (binding.additionalInfoOpenHoursDetails.visibility == View.VISIBLE) {
                    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        binding.additionalInfoOpenHours, iconDrawable, null, animatedArrowUpToDown, null
                    )
                    animatedArrowUpToDown.start()
                    binding.additionalInfoOpenHoursDetails.visibility = View.GONE
                } else {
                    TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        binding.additionalInfoOpenHours, iconDrawable, null, animatedArrowDownToUp, null
                    )
                    animatedArrowDownToUp.start()
                    binding.additionalInfoOpenHoursDetails.visibility = View.VISIBLE

                    val workingScheduleAsPairsList = schedule.generateWorkingDaysFields(requireContext())
                    val lunchScheduleAsPairsList = schedule.generateLaunchTimeFields(requireContext())
                    detailedScheduleAdapter.update(workingScheduleAsPairsList + lunchScheduleAsPairsList)
                }
            }
        }
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
            binding.placeIsWorkingNowTextView.text = spannableString
        } else {
            val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val workDay = schedule.workingDays.find { workDay -> workDay.weekDay == today }
            when {
                workDay == null -> binding.placeIsWorkingNowTextView.visibility = View.GONE
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
                    binding.placeIsWorkingNowTextView.text = spannableString
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
                    binding.placeIsWorkingNowTextView.text = spannableString
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
                    binding.placeIsWorkingNowTextView.text = spannableString
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