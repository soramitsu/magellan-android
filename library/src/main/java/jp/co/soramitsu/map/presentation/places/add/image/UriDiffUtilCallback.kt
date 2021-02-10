package jp.co.soramitsu.map.presentation.places.add.image

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil

internal class UriDiffUtilCallback(
    private val beforeList: List<Uri>,
    private val afterList: List<Uri>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = beforeList.size
    override fun getNewListSize(): Int = afterList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return beforeList[oldItemPosition] == afterList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return beforeList[oldItemPosition] == afterList[newItemPosition]
    }
}
