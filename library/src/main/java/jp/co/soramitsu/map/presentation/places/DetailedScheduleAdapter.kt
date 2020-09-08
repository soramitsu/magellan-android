package jp.co.soramitsu.map.presentation.places

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import jp.co.soramitsu.map.R

class DetailedScheduleAdapter : RecyclerView.Adapter<DetailedScheduleAdapter.KeyValueViewHolder>() {

    private val items: MutableList<Pair<String, String>> = mutableListOf()

    fun update(newItems: List<Pair<String, String>>) {
        val result = DiffUtil.calculateDiff(DiffUtilsCallback(items, newItems))
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeyValueViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sm_simple_key_value_list_item, parent, false)
        return KeyValueViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: KeyValueViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class KeyValueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val keyTextView: TextView = itemView.findViewById(R.id.keyTextView)
        private val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)

        fun bind(item: Pair<String, String>) {
            keyTextView.text = item.first
            valueTextView.text = item.second
        }
    }

    class DiffUtilsCallback(
        private val itemsBefore: List<Pair<String, String>>,
        private val itemsAfter: List<Pair<String, String>>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = itemsBefore.size

        override fun getNewListSize(): Int = itemsAfter.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            itemsBefore[oldItemPosition] == itemsAfter[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            itemsBefore[oldItemPosition] == itemsAfter[newItemPosition]

    }
}
