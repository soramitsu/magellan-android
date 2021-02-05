package jp.co.soramitsu.map.presentation.review

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import kotlinx.android.synthetic.main.sm_add_review_fragment.*

internal class ReviewFragment : BottomSheetDialogFragment() {

    private val placeId: String get() = requireArguments().getString(EXTRA_PLACE_ID).orEmpty()
    private val placeName: String get() = requireArguments().getString(EXTRA_PLACE_NAME).orEmpty()
    private val initialRating: Int get() = requireArguments().getInt(EXTRA_INITIAL_RATING, 0)
    private val comment: String get() = requireArguments().getString(EXTRA_COMMENT, "").orEmpty()

    private val viewModel: ReviewViewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[ReviewViewModel::class.java]
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet: View? = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return bottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_add_review_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnLayout {
            val parent = (view.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)
        }

        toolbar.setNavigationOnClickListener {
            activity?.onUserInteraction()
            dismiss()
        }

        placeNameTextView.text = placeName
        placeRatingBar.rating = initialRating.toFloat()
        commentEditText.setText(comment)
        postButton.setOnClickListener {
            activity?.onUserInteraction()
            if (comment.isEmpty()) {
                viewModel.addReview(
                    placeId = placeId,
                    rating = placeRatingBar.rating.toInt(),
                    comment = commentEditText.text.toString()
                )
            } else {
                viewModel.updateReview(
                    placeId = placeId,
                    rating = placeRatingBar.rating.toInt(),
                    comment = commentEditText.text.toString()
                )
            }
        }

        commentEditText.doOnTextChanged { text, start, before, count ->
            activity?.onUserInteraction()
        }

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            when (viewState) {
                ReviewViewModel.ViewState.InputComment, ReviewViewModel.ViewState.Error -> {
                    progressBar.visibility = View.GONE
                    postButton.visibility = View.VISIBLE
                    placeRatingBar.setIsIndicator(false)
                    commentInputLayout.isEnabled = true
                }
                ReviewViewModel.ViewState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    postButton.visibility = View.GONE
                    placeRatingBar.setIsIndicator(true)
                    commentInputLayout.isEnabled = false
                }
                ReviewViewModel.ViewState.Submitted -> {
                    // todo: show notification
                    parentFragmentManager.fragments.find { it is SoramitsuMapFragment }?.let { hostFragment ->
                        ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                            .get(SoramitsuMapViewModel::class.java)
                            .onPlaceReviewAdded()
                    }
                    dismiss()
                }
            }.run {
                // just to make sure that all cases was handled
            }
        })
    }

    fun withArguments(placeId: String, placeName: String, initialRating: Int, comment: String = ""): ReviewFragment {
        arguments = bundleOf(
            EXTRA_PLACE_ID to placeId,
            EXTRA_PLACE_NAME to placeName,
            EXTRA_INITIAL_RATING to initialRating,
            EXTRA_COMMENT to comment
        )

        return this
    }

    private companion object {
        private const val EXTRA_PLACE_ID = "PlaceId"
        private const val EXTRA_PLACE_NAME = "PlaceName"
        private const val EXTRA_INITIAL_RATING = "InitialRating"
        private const val EXTRA_COMMENT = "Comment"
    }
}