package jp.co.soramitsu.map.presentation.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import kotlinx.android.synthetic.main.categories_fragment.*

class CategoriesFragment : Fragment(R.layout.categories_fragment) {

    private lateinit var viewModel: SoramitsuMapViewModel

    private val categoriesAdapter: CategoriesAdapter = CategoriesAdapter().apply {
        onCategoryClick = { category -> viewModel.onCategorySelected(category) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        categoriesRecyclerView.adapter = categoriesAdapter

        parentFragmentManager.fragments.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            viewModel = ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)

            viewModel.viewState().observe(viewLifecycleOwner, Observer { viewState ->
                categoriesAdapter.update(Category.values().map { category ->
                    CategoryListItem(category, viewState.category == category)
                })
            })
        }
    }
}