package jp.co.soramitsu.map.presentation.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.ext.asLocalizedString
import jp.co.soramitsu.map.ext.getResourceIdForAttr
import jp.co.soramitsu.map.model.Category

internal class CategoriesAdapter : RecyclerView.Adapter<CategoryViewHolder>() {

    private val items = mutableListOf<CategoryListItem>()

    var onCategoryClick: (Category) -> Unit = {}

    fun update(categories: List<CategoryListItem>) {
        val result = DiffUtil.calculateDiff(CategoryDiffUtilCallback(items, categories))
        items.clear()
        items.addAll(categories)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.sm_category_list_item, parent, false)
        val viewHolder = CategoryViewHolder(view)
        view.setOnClickListener {
            onCategoryClick.invoke(items[viewHolder.adapterPosition].category)
        }
        return viewHolder
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

internal class CategoryDiffUtilCallback(
    private val beforeList: List<CategoryListItem>,
    private val afterList: List<CategoryListItem>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return beforeList[oldItemPosition].category.name == afterList[newItemPosition].category.name
    }

    override fun getOldListSize(): Int = beforeList.size

    override fun getNewListSize(): Int = afterList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return beforeList[oldItemPosition] == afterList[newItemPosition]
    }
}

internal class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val categoryTextView =
        itemView.findViewById<AppCompatTextView>(R.id.categoryNameTextView)

    fun bind(categoryListItem: CategoryListItem) {
        categoryTextView.setText(categoryListItem.category.asLocalizedString())
        categoryTextView.isSelected = categoryListItem.selected
        val checkIconId = if (categoryListItem.selected) R.drawable.sm_ic_check_24dp else 0
        categoryTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(
            iconForCategory(categoryTextView.context, categoryListItem.category), 0, checkIconId, 0
        )
    }

    private fun iconForCategory(context: Context, category: Category): Int = when (category) {
        Category.BANK -> context.getResourceIdForAttr(R.attr.sm_categoryIconDeposit)
        Category.FOOD -> context.getResourceIdForAttr(R.attr.sm_categoryIconRestaurant)
        Category.SERVICES -> context.getResourceIdForAttr(R.attr.sm_categoryIconServices)
        Category.SUPERMARKETS -> context.getResourceIdForAttr(R.attr.sm_categoryIconSupermarket)
        Category.PHARMACY -> context.getResourceIdForAttr(R.attr.sm_categoryIconPharmacy)
        Category.ENTERTAINMENT -> context.getResourceIdForAttr(R.attr.sm_categoryIconEntertainment)
        Category.EDUCATION -> context.getResourceIdForAttr(R.attr.sm_categoryIconEducation)
        Category.OTHER -> context.getResourceIdForAttr(R.attr.sm_categoryIconOther)
    }

}