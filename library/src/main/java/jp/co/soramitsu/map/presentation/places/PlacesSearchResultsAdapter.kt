package jp.co.soramitsu.map.presentation.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Place

internal class PlacesSearchResultsAdapter(
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
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.sm_place_search_result, parent, false)

        val viewHolder = PlaceViewHolder(view)
        view.setOnClickListener {
            items[viewHolder.adapterPosition].apply(onPlaceSelected)
        }
        return viewHolder
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

internal class PlaceViewHolder(placeView: View) : RecyclerView.ViewHolder(placeView) {

    private val title: TextView = placeView.findViewById(R.id.searchResultTitle)
    private val subtite: TextView = placeView.findViewById(R.id.searchResultSubtitle)

    fun bind(place: Place) {
        title.text = place.name
        subtite.text = "${place.category.name} ${place.address}"
    }
}

internal class DiffUtilCallback(
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
