package jp.co.soramitsu.feature.maps.impl.presentation.category_with_places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.co.soramitsu.feature.maps.impl.ext.getResourceIdForAttr
import jp.co.soramitsu.feature.maps.impl.ext.selectableItemBackground
import jp.co.soramitsu.feature.maps.impl.model.Category
import jp.co.soramitsu.feature.maps.impl.model.Place
import jp.co.soramitsu.feature.maps.impl.presentation.places.PlaceView
import jp.co.soramitsu.feature_map_impl.R

class CategoriesWithPlacesAdapter(
    private val onCategorySelected: (Category) -> Unit,
    private val onPlaceSelected: (Place) -> Unit
) : RecyclerView.Adapter<BaseViewHolder<ListItem>>() {

    private val items: MutableList<ListItem> = mutableListOf()

    fun update(category: Category?, places: List<Place>) {
        val placeListItems = places.map { PlaceListItem(it) }
        val newItems: List<ListItem> =
            mutableListOf<ListItem>(Categories(category)).apply { addAll(placeListItems) }

        val result = DiffUtil.calculateDiff(DiffUtilCallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ListItem> {
        if (viewType == 1) {
            val inflater = LayoutInflater.from(parent.context)
            return CategoriesViewHolder(
                inflater.inflate(R.layout.categories_panel, parent, false),
                onCategorySelected
            ) as BaseViewHolder<ListItem>
        } else if (viewType == 2) {
            val view = PlaceView(parent.context, null, 0).apply {
                layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                )
            }
            val viewHolder = PlaceViewHolder(view) as BaseViewHolder<ListItem>
            view.setOnClickListener {
                (items[viewHolder.adapterPosition] as? PlaceListItem)?.place?.apply(onPlaceSelected)
            }
            return viewHolder
        }

        throw IllegalArgumentException()
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder<ListItem>, position: Int) =
        holder.bind(items[position])
}

class DiffUtilCallback(
    private val listBefore: List<ListItem>,
    private val listAfter: List<ListItem>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return listBefore[oldItemPosition] == listAfter[newItemPosition]
    }

    override fun getOldListSize(): Int = listBefore.size

    override fun getNewListSize(): Int = listAfter.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return listBefore[oldItemPosition] == listAfter[newItemPosition]
    }
}

// region list items
interface ListItem {
    val viewType: Int
}

data class Categories(val selectedCategory: Category? = null) : ListItem {
    override val viewType = 1
}

data class PlaceListItem(
    val place: Place,
    override val viewType: Int = 2
) : ListItem
// endregion

// region viewHolders
abstract class BaseViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(payload: T)
}

class PlaceViewHolder(private val placeView: PlaceView) : BaseViewHolder<PlaceListItem>(placeView) {
    override fun bind(payload: PlaceListItem) = placeView.bind(payload.place)
}

class CategoriesViewHolder(
    itemView: View,
    private val onCategorySelected: (Category) -> Unit = {}
) : BaseViewHolder<Categories>(itemView) {

    private val categoryBank: View = itemView.findViewById(R.id.categoryBank)
    private val categoryServices: View = itemView.findViewById(R.id.categoryServices)
    private val categoryFood: View = itemView.findViewById(R.id.categoryFood)
    private val categorySupermarkets: View = itemView.findViewById(R.id.categorySupermarkets)
    private val categoryPharmacy: View = itemView.findViewById(R.id.categoryPharmacy)
    private val categoryEntertainment: View = itemView.findViewById(R.id.categoryEntertainment)
    private val categoryEducation: View = itemView.findViewById(R.id.categoryEducation)
    private val categoryOther: View = itemView.findViewById(R.id.categoryOther)

    override fun bind(payload: Categories) {
        categoryBank.selectableItemBackground()
        categoryServices.selectableItemBackground()
        categoryFood.selectableItemBackground()
        categorySupermarkets.selectableItemBackground()
        categoryPharmacy.selectableItemBackground()
        categoryEntertainment.selectableItemBackground()
        categoryEducation.selectableItemBackground()
        categoryOther.selectableItemBackground()

        val categoryView: View? = when (payload.selectedCategory) {
            Category.BANK -> categoryBank
            Category.SERVICES -> categoryServices
            Category.FOOD -> categoryFood
            Category.SUPERMARKETS -> categorySupermarkets
            Category.PHARMACY -> categoryPharmacy
            Category.ENTERTAINMENT -> categoryEntertainment
            Category.EDUCATION -> categoryEducation
            Category.OTHER -> categoryOther
            else -> null
        }

        val selectedItemBackgroundResourceId = categoryBank.context
            .getResourceIdForAttr(R.attr.soramitsuMapCategorySelectedItemBackground)
        categoryView?.setBackgroundResource(selectedItemBackgroundResourceId)
    }

    init {
        categoryBank.setOnClickListener { onCategorySelected.invoke(Category.BANK) }
        categoryServices.setOnClickListener { onCategorySelected.invoke(Category.SERVICES) }
        categoryFood.setOnClickListener { onCategorySelected.invoke(Category.FOOD) }
        categorySupermarkets.setOnClickListener { onCategorySelected.invoke(Category.SUPERMARKETS) }
        categoryPharmacy.setOnClickListener { onCategorySelected.invoke(Category.PHARMACY) }
        categoryEntertainment.setOnClickListener { onCategorySelected.invoke(Category.ENTERTAINMENT) }
        categoryEducation.setOnClickListener { onCategorySelected.invoke(Category.EDUCATION) }
        categoryOther.setOnClickListener { onCategorySelected.invoke(Category.OTHER) }
    }
}

// endregion
