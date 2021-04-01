package jp.co.soramitsu.map.presentation.places.add

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.databinding.SmFragmentSelectPlaceCategoryBinding
import jp.co.soramitsu.map.model.Category

internal class SelectPlaceCategoryFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: SelectPlaceViewModel

    private var _binding: SmFragmentSelectPlaceCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_fragment_select_place_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SmFragmentSelectPlaceCategoryBinding.bind(view)

        val factory = ViewModelProvider.NewInstanceFactory()
        viewModel = ViewModelProvider(this, factory)[SelectPlaceViewModel::class.java]

        view.doOnLayout { (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT) }
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(context)

        val categoriesAdapter = CategoriesAdapter()
        categoriesAdapter.setOnCategorySelectedListener { category ->
            (parentFragment as? OnCategorySelected)?.onCategorySelected(category)
            dismiss()
        }

        binding.categoriesRecyclerView.adapter = categoriesAdapter

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesAdapter.setItems(categories)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    internal interface OnCategorySelected {
        fun onCategorySelected(category: Category)
    }
}
