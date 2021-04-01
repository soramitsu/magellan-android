package jp.co.soramitsu.map.presentation.places.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.presentation.categories.CategoryTextView

internal class CategoriesAdapter : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    private val items = mutableListOf<Category>()
    private var onCategorySelected: (Category) -> Unit = {}

    fun setOnCategorySelectedListener(onCategorySelectedListener: (Category) -> Unit) {
        onCategorySelected = onCategorySelectedListener
    }

    fun setItems(categories: List<Category>) {
        val diffResult = DiffUtil.calculateDiff(CategoriesDiffUtilCallback(items, categories))
        items.clear()
        items.addAll(categories)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.sm_category_list_item, parent, false)
        return CategoryViewHolder(view).also { viewHolder ->
            view.setOnClickListener {
                onCategorySelected.invoke(items[viewHolder.adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    internal class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTextView: CategoryTextView = itemView.findViewById(R.id.categoryTextView)
        fun bind(category: Category) {
            categoryTextView.category = category
        }
    }

    private class CategoriesDiffUtilCallback(
        private val beforeList: List<Category>,
        private val afterList: List<Category>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = beforeList.size
        override fun getNewListSize(): Int = afterList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return beforeList[oldItemPosition].id == afterList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return beforeList[oldItemPosition] == afterList[newItemPosition]
        }
    }
}
