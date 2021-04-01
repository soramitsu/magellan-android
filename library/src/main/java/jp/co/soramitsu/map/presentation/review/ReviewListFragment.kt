package jp.co.soramitsu.map.presentation.review

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.databinding.SmFragmentReviewListBinding
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel

internal class ReviewListFragment : BottomSheetDialogFragment() {

    private val reviewsAdapter = ReviewsAdapter()
    private val sharedViewModel by lazy {
        requireFragmentManager().fragments.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)
        }
    }

    private var _binding: SmFragmentReviewListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ReviewListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_fragment_review_list, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            bottomSheetDialog.behavior.isFitToContents = false
            bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SmFragmentReviewListBinding.bind(view)

        view.doOnLayout {
            val parent = (view.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)
        }

        binding.reviewsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.reviewsRecyclerView.adapter = reviewsAdapter

        binding.toolbar.setNavigationOnClickListener { dismiss() }

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(ReviewListViewModel::class.java)
        viewModel.viewState.observe(viewLifecycleOwner) { renderViewState(it) }

        binding.retryButton.setOnClickListener {
            sharedViewModel?.placeSelected()?.value?.id?.let { placeId ->
                viewModel.reloadReviews(placeId)
            }
        }

        binding.ratePlaceButton.setOnClickListener {
            dismiss()
            sharedViewModel?.onEditReviewClicked()
        }

        reviewsAdapter.setOnEditCommentButtonClickListener {
            EditReviewFragment().show(requireFragmentManager(), "EditReviewMenu")
        }

        sharedViewModel?.placeSelected()?.observe(viewLifecycleOwner) { place ->
            place?.let { viewModel.reloadReviews(place.id) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun renderViewState(viewState: ReviewListViewModel.ReviewListViewState) =
        when (viewState) {
            ReviewListViewModel.ReviewListViewState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.reviewsRecyclerView.visibility = View.GONE
                binding.noReviewsYet.visibility = View.GONE
                binding.retryFlow.visibility = View.GONE
                binding.ratePlaceButton.visibility = View.GONE
            }

            is ReviewListViewModel.ReviewListViewState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.reviewsRecyclerView.visibility = View.GONE
                binding.noReviewsYet.visibility = View.GONE
                binding.retryFlow.visibility = View.VISIBLE
                binding.ratePlaceButton.visibility = View.GONE
            }

            is ReviewListViewModel.ReviewListViewState.ContentWithoutUserReview -> {
                binding.progressBar.visibility = View.GONE
                binding.reviewsRecyclerView.visibility = View.VISIBLE
                binding.noReviewsYet.visibility = View.GONE
                binding.retryFlow.visibility = View.GONE
                binding.ratePlaceButton.visibility = View.VISIBLE

                reviewsAdapter.setItems(viewState.reviews)
            }

            is ReviewListViewModel.ReviewListViewState.ContentWithUserReview -> {
                binding.progressBar.visibility = View.GONE
                binding.reviewsRecyclerView.visibility = View.VISIBLE
                binding.noReviewsYet.visibility = View.GONE
                binding.retryFlow.visibility = View.GONE
                binding.ratePlaceButton.visibility = View.GONE

                reviewsAdapter.setItems(viewState.reviews)
            }

            ReviewListViewModel.ReviewListViewState.NoReviewsYet -> {
                binding.progressBar.visibility = View.GONE
                binding.reviewsRecyclerView.visibility = View.GONE
                binding.noReviewsYet.visibility = View.VISIBLE
                binding.retryFlow.visibility = View.GONE
                binding.ratePlaceButton.visibility = View.VISIBLE
            }
        }
}
