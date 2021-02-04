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
import com.google.android.material.button.MaterialButton
import jp.co.soramitsu.map.R
import kotlinx.android.synthetic.main.sm_fragment_photo.*

class RemovableImagesAdapter(private val requestManager: RequestManager) :
    RecyclerView.Adapter<RemovableImagesAdapter.BaseViewHolder>() {

    private val _items = mutableListOf<RemovableImageListItem>()
    private var imageSelectionListener: (Uri) -> Unit = {}
    private var removeImageClickListener: (Uri) -> Unit = {}
    private var buttonClickListener: (Int) -> Unit = {}

    var enabled: Boolean = true
    val items: List<RemovableImageListItem> = _items

    fun setOnImageSelectedListener(imageSelectionListener: (Uri) -> Unit) {
        this.imageSelectionListener = imageSelectionListener
    }

    fun setOnRemoveImageClickListener(removeImageClickListener: (Uri) -> Unit) {
        this.removeImageClickListener = removeImageClickListener
    }

    fun setOnButtonClickListener(buttonClickListener: (Int) -> Unit) {
        this.buttonClickListener = buttonClickListener
    }

    fun update(newItems: List<RemovableImageListItem>) {
        val diffResult = DiffUtil.calculateDiff(RemovableImageListItemDiffCallback(items, newItems))
        _items.clear()
        _items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is RemovableImageListItem.Button -> R.layout.sm_image_button_list_item_56
        is RemovableImageListItem.Image -> R.layout.sm_removable_image_list_item_56
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutResId = viewType
        val view = LayoutInflater
            .from(parent.context)
            .inflate(layoutResId, parent, false)
        return when (layoutResId) {
            R.layout.sm_removable_image_list_item_56 ->
                BaseViewHolder.ImageViewHolder(requestManager, view).also { viewHolder ->
                    view.setOnClickListener {
                        if (enabled) {
                            val idx = viewHolder.adapterPosition
                            val imageData = items[idx] as RemovableImageListItem.Image
                            imageSelectionListener.invoke(imageData.imageUri)
                        }
                    }
                    viewHolder.removeButton.setOnClickListener {
                        if (enabled) {
                            val idx = viewHolder.adapterPosition
                            val imageData = items[idx] as RemovableImageListItem.Image
                            removeImageClickListener.invoke(imageData.imageUri)
                        }
                    }
                }
            R.layout.sm_image_button_list_item_56 ->
                BaseViewHolder.ButtonViewHolder(view).also { viewHolder ->
                    view.setOnClickListener {
                        val idx = viewHolder.adapterPosition
                        val buttonData = items[idx] as RemovableImageListItem.Button
                        buttonClickListener.invoke(buttonData.buttonId)
                    }
                }
            else -> throw IllegalArgumentException("Unsupported view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    sealed class RemovableImageListItem {
        data class Button(
            val buttonId: Int,
            @DrawableRes val iconResId: Int
        ) : RemovableImageListItem()

        data class Image(val imageUri: Uri) : RemovableImageListItem()
    }

    sealed class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun bind(item: RemovableImageListItem)

        class ImageViewHolder(
            private val requestManager: RequestManager,
            itemView: View
        ) : BaseViewHolder(itemView) {

            internal val imageView: ImageView = itemView.findViewById(R.id.image)
            internal val removeButton: View = itemView.findViewById(R.id.removeImage)

            override fun bind(item: RemovableImageListItem) {
                val imageData = item as RemovableImageListItem.Image
                requestManager.load(imageData.imageUri).into(imageView)

                imageView.transitionName = generateShareImageTransitionName(imageData.imageUri)
            }
        }

        class ButtonViewHolder(itemView: View) : BaseViewHolder(itemView) {
            private val imageButton: MaterialButton = itemView.findViewById(R.id.actionButton)

            override fun bind(item: RemovableImageListItem) {
                val buttonData = item as RemovableImageListItem.Button
                imageButton.setIconResource(buttonData.iconResId)
            }
        }
    }

    private class RemovableImageListItemDiffCallback(
        private val beforeItems: List<RemovableImageListItem>,
        private val afterItems: List<RemovableImageListItem>,
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = beforeItems.size
        override fun getNewListSize(): Int = afterItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return beforeItems[oldItemPosition] == afterItems[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return beforeItems[oldItemPosition] == afterItems[newItemPosition]
        }
    }
}