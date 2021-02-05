package jp.co.soramitsu.map.presentation.places.add.image

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import jp.co.soramitsu.map.R

class SelectableImagesAdapter(
    private val requestManager: RequestManager,
    private val selectable: Boolean = false
) : RecyclerView.Adapter<SelectableImagesAdapter.ImageViewHolder>() {

    private val _items = mutableListOf<SelectableImage>()
    private var imageSelectionListener: (SelectableImage) -> Unit = {}

    val items: List<SelectableImage> = _items

    fun setOnImageSelectedListener(imageSelectionListener: (SelectableImage) -> Unit) {
        this.imageSelectionListener = imageSelectionListener
    }

    fun update(newItems: List<SelectableImage>) {
        val diffResult = DiffUtil.calculateDiff(SelectableImagesDiffUtilCallback(items, newItems))
        _items.clear()
        _items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.sm_image_list_item, parent, false)
        return ImageViewHolder(requestManager, view).also { viewHolder ->
            view.setOnClickListener {
                imageSelectionListener.invoke(items[viewHolder.adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(selectable, items[position])
    }

    override fun getItemCount(): Int = items.size

    class ImageViewHolder(
        private val requestManager: RequestManager,
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.image)
        private val checkIndicator: ImageView = itemView.findViewById(R.id.checkIndicator)

        fun bind(selectable: Boolean, imageData: SelectableImage) {
            requestManager.load(imageData.imageUri).into(imageView)

            checkIndicator.visibility = if (selectable) View.VISIBLE else View.GONE

            @DrawableRes val imageRes = if (imageData.selected) {
                R.drawable.sm_white_circle_checked
            } else {
                R.drawable.sm_white_circle
            }
            checkIndicator.setImageResource(imageRes)
        }
    }

    data class SelectableImage(
        val imageUri: Uri,
        val selected: Boolean
    )

    private class SelectableImagesDiffUtilCallback(
        private val beforeList: List<SelectableImage>,
        private val afterList: List<SelectableImage>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = beforeList.size
        override fun getNewListSize(): Int = afterList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return beforeList[oldItemPosition].imageUri == afterList[newItemPosition].imageUri
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return beforeList[oldItemPosition] == afterList[newItemPosition]
        }
    }
}