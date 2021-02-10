package jp.co.soramitsu.map.presentation.review

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import kotlinx.android.synthetic.main.sm_edit_review_menu_bottom_sheet.*

internal class EditReviewFragment : BottomSheetDialogFragment() {

    private val viewModel: SoramitsuMapViewModel? by lazy {
        parentFragmentManager.fragments.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_edit_review_menu_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnLayout {
            (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
        }

        editReview.setOnClickListener {
            viewModel?.onEditReviewClicked()
            dismiss()
        }

        deleteReview.setOnClickListener {
            viewModel?.onDeleteReviewClicked()
            dismiss()
        }
    }
}
