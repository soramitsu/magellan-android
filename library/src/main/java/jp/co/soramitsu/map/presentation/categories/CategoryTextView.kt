package jp.co.soramitsu.map.presentation.categories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.ext.getResourceIdForAttr
import jp.co.soramitsu.map.model.Category
import java.util.*

class CategoryTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleRes: Int = 0
) : AppCompatTextView(context, attributeSet, defStyleRes) {

    var category: Category? = null
        set(value) {
            field = value

            invalidateSelf()
        }

    init {
        invalidateSelf()
    }

    override fun onSaveInstanceState(): Parcelable {
        val savedState = CategoryTextViewSavedState(super.onSaveInstanceState()!!)
        savedState.category = category
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is CategoryTextViewSavedState) {
            super.onRestoreInstanceState(state.superState)
            category = state.category
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun invalidateSelf() {
        if (category == null) {
            setText(R.string.sm_choose_category)
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                this, R.drawable.sm_ic_choose_category_circle_44, 0, 0, 0
            )
        } else {
            text = category?.localisedName().orEmpty()
            val categoryIcon = wrapInCircle44dp(iconForCategory(context, category ?: Category.OTHER))
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                this, categoryIcon, null, null, null
            )
        }
    }

    private fun wrapInCircle44dp(@DrawableRes categoryIconRes: Int): Drawable {
        val size = resources.getDimension(R.dimen.sm_icon_size).toInt()
        val resultBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        val circleColor = ContextCompat.getColor(context, R.color.sm_icon_background_color)
        val circlePaint = Paint().apply {
            color = circleColor
            isAntiAlias = true
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, circlePaint)
        ContextCompat.getDrawable(context, categoryIconRes)?.let { iconDrawable ->
            val smallIconSize = resources.getDimension(R.dimen.sm_small_icon_size).toInt()
            val left = size / 2 - smallIconSize / 2
            val top = size / 2 - smallIconSize / 2
            val right = left + smallIconSize
            val bottom = top + smallIconSize
            iconDrawable.setBounds(left, top, right, bottom)
            iconDrawable.draw(canvas)
        }
        return BitmapDrawable(resources, resultBitmap)
    }

    @DrawableRes
    private fun iconForCategory(context: Context, category: Category): Int = when (category.localisedName(Locale.US)) {
        Category.BANK.localisedName(Locale.US) -> context.getResourceIdForAttr(R.attr.sm_categoryIconDeposit)
        Category.FOOD.localisedName(Locale.US) -> context.getResourceIdForAttr(R.attr.sm_categoryIconRestaurant)
        Category.SERVICES.localisedName(Locale.US) -> context.getResourceIdForAttr(R.attr.sm_categoryIconServices)
        Category.SUPERMARKETS.localisedName(Locale.US) -> context.getResourceIdForAttr(R.attr.sm_categoryIconSupermarket)
        Category.PHARMACY.localisedName(Locale.US) -> context.getResourceIdForAttr(R.attr.sm_categoryIconPharmacy)
        Category.ENTERTAINMENT.localisedName(Locale.US) -> context.getResourceIdForAttr(R.attr.sm_categoryIconEntertainment)
        Category.EDUCATION.localisedName(Locale.US) -> context.getResourceIdForAttr(R.attr.sm_categoryIconEducation)
        else -> context.getResourceIdForAttr(R.attr.sm_categoryIconOther)
    }

    internal class CategoryTextViewSavedState : BaseSavedState {

        var category: Category? = null

        internal constructor(superState: Parcelable) : super(superState)

        internal constructor(parcel: Parcel) : super(parcel) {
            category = parcel.readSerializable() as? Category
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)

            category?.let { category -> out.writeSerializable(category) }
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<CategoryTextViewSavedState> {
                override fun createFromParcel(source: Parcel): CategoryTextViewSavedState {
                    return CategoryTextViewSavedState(source)
                }

                override fun newArray(size: Int): Array<CategoryTextViewSavedState> = emptyArray()
            }
        }
    }
}
