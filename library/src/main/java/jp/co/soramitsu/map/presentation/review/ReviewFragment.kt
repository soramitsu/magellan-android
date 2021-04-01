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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.databinding.SmAddReviewFragmentBinding
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel

internal class ReviewFragment : BottomSheetDialogFragment() {

    private val placeId: String get() = requireArguments().getString(EXTRA_PLACE_ID).orEmpty()
    private val placeName: String get() = requireArguments().getString(EXTRA_PLACE_NAME).orEmpty()
    private val initialRating: Int get() = requireArguments().getInt(EXTRA_INITIAL_RATING, 0)
    private val comment: String get() = requireArguments().getString(EXTRA_COMMENT, "").orEmpty()
    private val isEdit: Boolean get() = requireArguments().getBoolean(EXTRA_EDIT)

    private var _binding: SmAddReviewFragmentBinding? = null
    private val binding get() = _binding!!

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

        _binding = SmAddReviewFragmentBinding.bind(view)

        view.doOnLayout {
            val parent = (view.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)
        }

        binding.toolbar.setNavigationOnClickListener {
            activity?.onUserInteraction()
            dismiss()
        }

        binding.placeNameTextView.text = placeName
        binding.placeRatingBar.rating = initialRating.toFloat()
        binding.commentEditText.setText(comment)
        binding.postButton.setOnClickListener {
            activity?.onUserInteraction()
            if (isEdit) {
                viewModel.updateReview(
                    placeId = placeId,
                    rating = binding.placeRatingBar.rating.toInt(),
                    comment = binding.commentEditText.text.toString()
                )
            } else {
                viewModel.addReview(
                    placeId = placeId,
                    rating = binding.placeRatingBar.rating.toInt(),
                    comment = binding.commentEditText.text.toString()
                )
            }
        }

        binding.commentEditText.doOnTextChanged { text, start, before, count ->
            activity?.onUserInteraction()
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                ReviewViewModel.ViewState.InputComment, ReviewViewModel.ViewState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.postButton.visibility = View.VISIBLE
                    binding.placeRatingBar.setIsIndicator(false)
                    binding.commentInputLayout.isEnabled = true
                }
                ReviewViewModel.ViewState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.postButton.visibility = View.GONE
                    binding.placeRatingBar.setIsIndicator(true)
                    binding.commentInputLayout.isEnabled = false
                }
                ReviewViewModel.ViewState.Submitted -> {
                    // todo: show notification
                    fragmentManager?.fragments?.find { it is SoramitsuMapFragment }?.let { hostFragment ->
                        ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                            .get(SoramitsuMapViewModel::class.java)
                            .onPlaceReviewAdded()
                    }
                    dismiss()
                }
            }.run {
                // just to make sure that all cases was handled
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    fun withArguments(placeId: String, placeName: String, initialRating: Int, edit: Boolean, comment: String = ""): ReviewFragment {
        arguments = bundleOf(
            EXTRA_PLACE_ID to placeId,
            EXTRA_PLACE_NAME to placeName,
            EXTRA_INITIAL_RATING to initialRating,
            EXTRA_COMMENT to comment,
            EXTRA_EDIT to edit
        )

        return this
    }

    private companion object {
        private const val EXTRA_PLACE_ID = "PlaceId"
        private const val EXTRA_PLACE_NAME = "PlaceName"
        private const val EXTRA_INITIAL_RATING = "InitialRating"
        private const val EXTRA_COMMENT = "Comment"
        private const val EXTRA_EDIT = "Edit"
    }
}
