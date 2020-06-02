package jp.co.soramitsu.map.presentation.categories

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import kotlinx.android.synthetic.main.sm_categories_fragment.*

internal class CategoriesFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: SoramitsuMapViewModel

    private val categoriesAdapter: CategoriesAdapter = CategoriesAdapter().apply {
        onCategoryClick = { category -> viewModel.onCategorySelected(category) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_categories_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            val parent = (view?.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)
        }, 50)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        categoriesRecyclerView.adapter = categoriesAdapter

        parentFragmentManager.fragments.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            viewModel = ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)

            viewModel.viewState().observe(viewLifecycleOwner, Observer { viewState ->
                categoriesAdapter.update(viewState.categories)
                resetFiltersButton.isEnabled = viewState.enableResetButton
            })

            resetFiltersButton.setOnClickListener {
                viewModel.onResetCategoriesFilterButtonClicked()
            }
        }
    }
}