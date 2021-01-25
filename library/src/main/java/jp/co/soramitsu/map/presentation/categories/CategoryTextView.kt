package jp.co.soramitsu.map.presentation.categories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.ext.getResourceIdForAttr
import jp.co.soramitsu.map.model.Category

class CategoryTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleRes: Int = 0
) : AppCompatTextView(context, attributeSet, defStyleRes) {

    var category: Category = Category.OTHER
        set(value) {
            field = value
            invalidateSelf()
        }

    private fun invalidateSelf() {
        text = category.localisedName()
        val categoryIcon = wrapInCircle44dp(iconForCategory(context, category))
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
            this, categoryIcon, null, null, null
        )
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
    private fun iconForCategory(context: Context, category: Category): Int = when (category.name) {
        Category.BANK.name -> context.getResourceIdForAttr(R.attr.sm_categoryIconDeposit)
        Category.FOOD.name -> context.getResourceIdForAttr(R.attr.sm_categoryIconRestaurant)
        Category.SERVICES.name -> context.getResourceIdForAttr(R.attr.sm_categoryIconServices)
        Category.SUPERMARKETS.name -> context.getResourceIdForAttr(R.attr.sm_categoryIconSupermarket)
        Category.PHARMACY.name -> context.getResourceIdForAttr(R.attr.sm_categoryIconPharmacy)
        Category.ENTERTAINMENT.name -> context.getResourceIdForAttr(R.attr.sm_categoryIconEntertainment)
        Category.EDUCATION.name -> context.getResourceIdForAttr(R.attr.sm_categoryIconEducation)
        else -> context.getResourceIdForAttr(R.attr.sm_categoryIconOther)
    }

    init {
        invalidateSelf()
    }
}