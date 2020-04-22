package jp.co.soramitsu.map.presentation.places

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.co.soramitsu.map.model.Place

class PlacesAdapter(
    private val onPlaceSelected: (Place) -> Unit = {}
) : RecyclerView.Adapter<PlaceViewHolder>() {

    private val items: MutableList<Place> = mutableListOf()

    fun update(places: List<Place>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(items, places))
        items.clear()
        items.addAll(places)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = PlaceView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }
        val viewHolder = PlaceViewHolder(view)
        view.setOnClickListener {
            items[viewHolder.adapterPosition].apply(onPlaceSelected)
        }
        return viewHolder
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.placeView.bind(items[position])
    }
}

class PlaceViewHolder(val placeView: PlaceView) : RecyclerView.ViewHolder(placeView)

class DiffUtilCallback(
    private val beforeList: List<Place>,
    private val afterList: List<Place>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return beforeList[oldItemPosition] == afterList[newItemPosition]
    }

    override fun getOldListSize(): Int = beforeList.size

    override fun getNewListSize(): Int = afterList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return beforeList[oldItemPosition] == afterList[newItemPosition]
    }
}
