package jp.co.soramitsu.map.presentation.categories

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.databinding.SmCategoriesFragmentBinding
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel

internal class CategoriesFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: SoramitsuMapViewModel

    private val categoriesAdapter: CategoriesAdapter = CategoriesAdapter().apply {
        onCategoryClick = { category -> viewModel.onCategorySelected(category) }
    }

    private var _binding: SmCategoriesFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_categories_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        _binding = SmCategoriesFragmentBinding.bind(view)

        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.categoriesRecyclerView.adapter = categoriesAdapter

        fragmentManager?.fragments?.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            viewModel = ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)

            viewModel.viewState().observe(viewLifecycleOwner) { viewState ->
                categoriesAdapter.update(viewState.categories)
                binding.resetFiltersButton.isEnabled = viewState.enableResetButton
            }

            binding.resetFiltersButton.setOnClickListener {
                viewModel.onResetCategoriesFilterButtonClicked()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        viewModel.applyNewFilters()
    }
}
