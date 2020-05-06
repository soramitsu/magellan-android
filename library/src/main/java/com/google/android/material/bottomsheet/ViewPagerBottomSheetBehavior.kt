package com.google.android.material.bottomsheet

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import java.lang.ref.WeakReference

/**
 * This is part of dirty hack to make recycler view scroll properly inside view pager
 * inside constraint layout inside coordinator layout.
 *
 * This key idea is to update [nestedScrollingChildRef] manually because internal
 * android implementation missed recycler view in such layouts. For more details
 * see [findScrollingChild]. The main problem is that when we have 2 or more views
 * which supports nested scrolling, only the first one will be tracked by host
 * BottomSheetBehavior
 *
 * To fix above behavior used this one implementation of BottomSheetBehavior. The main
 * idea, as already mentioned, is to manually update [nestedScrollingChildRef]. To
 * work properly [updateScrollingChild] mush be invoked when viewpager change its page.
 * Currently visible recycler view must be used as a parameter of [updateScrollingChild]
 *
 * NOTE:
 * if you wrap recycler view in some [VIEW_TYPE], you can manually call [onLayoutChild].
 * It also have [nestedScrollingChildRef] update. Here we can't use this trick because
 * we have 2 recycler views in a single [VIEW_TYPE] (in our case [VIEW_TYPE] is a
 * ConstraintLayout). In our case only the first one view will be always found
 */
internal class ViewPagerBottomSheetBehavior<VIEW_TYPE : View> : BottomSheetBehavior<VIEW_TYPE> {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun updateScrollingChild(view: View) {
        nestedScrollingChildRef = WeakReference<View>(view)
    }

    companion object {
        fun <VIEW_TYPE : View> from(view: VIEW_TYPE): ViewPagerBottomSheetBehavior<VIEW_TYPE> {
            return (view.layoutParams as CoordinatorLayout.LayoutParams).behavior as ViewPagerBottomSheetBehavior<VIEW_TYPE>
        }
    }
}