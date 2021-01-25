package jp.co.soramitsu.map.presentation.review

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import kotlinx.android.synthetic.main.sm_fragment_review_list.*

internal class ReviewListFragment : BottomSheetDialogFragment() {

    private val reviewsAdapter = ReviewsAdapter()
    private val sharedViewModel by lazy {
        parentFragmentManager.fragments.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)
        }
    }

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

        view.doOnLayout {
            val parent = (view.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)
        }

        reviewsRecyclerView.layoutManager = LinearLayoutManager(context)
        reviewsRecyclerView.adapter = reviewsAdapter

        toolbar.setNavigationOnClickListener { dismiss() }

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(ReviewListViewModel::class.java)
        viewModel.viewState.observe(viewLifecycleOwner, Observer { renderViewState(it) })

        retryButton.setOnClickListener {
            sharedViewModel?.placeSelected()?.value?.id?.let { placeId ->
                viewModel.reloadReviews(placeId)
            }
        }

        ratePlaceButton.setOnClickListener {
            dismiss()
            sharedViewModel?.onEditReviewClicked()
        }

        reviewsAdapter.setOnEditCommentButtonClickListener {
            EditReviewFragment().show(parentFragmentManager, "EditReviewMenu")
        }

        sharedViewModel?.placeSelected()?.observe(viewLifecycleOwner, Observer { place ->
            place?.let { viewModel.reloadReviews(place.id) }
        })
    }

    private fun renderViewState(viewState: ReviewListViewModel.ReviewListViewState) =
        when (viewState) {
            ReviewListViewModel.ReviewListViewState.Loading -> {
                progressBar.visibility = View.VISIBLE
                reviewsRecyclerView.visibility = View.GONE
                noReviewsYet.visibility = View.GONE
                retryFlow.visibility = View.GONE
                ratePlaceButton.visibility = View.GONE
            }

            is ReviewListViewModel.ReviewListViewState.Error -> {
                progressBar.visibility = View.GONE
                reviewsRecyclerView.visibility = View.GONE
                noReviewsYet.visibility = View.GONE
                retryFlow.visibility = View.VISIBLE
                ratePlaceButton.visibility = View.GONE
            }

            is ReviewListViewModel.ReviewListViewState.ContentWithoutUserReview -> {
                progressBar.visibility = View.GONE
                reviewsRecyclerView.visibility = View.VISIBLE
                noReviewsYet.visibility = View.GONE
                retryFlow.visibility = View.GONE
                ratePlaceButton.visibility = View.VISIBLE

                reviewsAdapter.setItems(viewState.reviews)
            }

            is ReviewListViewModel.ReviewListViewState.ContentWithUserReview -> {
                progressBar.visibility = View.GONE
                reviewsRecyclerView.visibility = View.VISIBLE
                noReviewsYet.visibility = View.GONE
                retryFlow.visibility = View.GONE
                ratePlaceButton.visibility = View.GONE

                reviewsAdapter.setItems(viewState.reviews)
            }

            ReviewListViewModel.ReviewListViewState.NoReviewsYet -> {
                progressBar.visibility = View.GONE
                reviewsRecyclerView.visibility = View.GONE
                noReviewsYet.visibility = View.VISIBLE
                retryFlow.visibility = View.GONE
                ratePlaceButton.visibility = View.VISIBLE
            }
        }
}