package jp.co.soramitsu.map.presentation.places.add.image

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class SpanItemDecorator(private val padding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        outRect.left = padding / 2
        outRect.right = padding / 2
        outRect.top = padding / 2
        outRect.bottom = padding / 2
    }
}
