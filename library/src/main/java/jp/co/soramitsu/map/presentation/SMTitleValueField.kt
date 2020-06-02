package jp.co.soramitsu.map.presentation

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.TextViewCompat
import jp.co.soramitsu.map.R
import kotlinx.android.synthetic.main.sm_title_value_field_horizontal.view.keyTextView
import kotlinx.android.synthetic.main.sm_title_value_field_horizontal.view.valueTextView
import kotlinx.android.synthetic.main.sm_title_value_field_vertical.view.*


/**
 * Commonly used key value field. Was added to prevent copy-pasting two TextView all over
 * the app
 */
class SMTitleValueField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val orientation: Int

    /**
     * CharSequence of the text from "Value" TextView
     */
    val value: CharSequence
        get() = valueTextView.text

    fun setValue(value: String) {
        valueTextView.text = value
    }

    private fun retrieveOrientationFromAttrs(attrs: AttributeSet?): Int {
        attrs ?: return HORIZONTAL

        val orientationAttribute = context
            .obtainStyledAttributes(attrs, intArrayOf(android.R.attr.orientation))
        val orientation = orientationAttribute.getInt(0, HORIZONTAL)
        orientationAttribute.recycle()
        return orientation
    }

    private fun setIcon(@DrawableRes iconRes: Int) {
        if (orientation == HORIZONTAL) {
            TextViewCompat
                .setCompoundDrawablesRelativeWithIntrinsicBounds(valueTextView, 0, 0, iconRes, 0)
        } else {
            iconImageView.setImageResource(iconRes)
        }
    }

    init {
        orientation = retrieveOrientationFromAttrs(attrs)
        val layoutId = if (orientation == VERTICAL) {
            R.layout.sm_title_value_field_vertical
        } else {
            R.layout.sm_title_value_field_horizontal
        }

        inflate(context, layoutId, this)

//        android:paddingLeft="@dimen/soramitsu_map_margin_padding_size_xmedium"
//        android:paddingRight="@dimen/soramitsu_map_margin_padding_size_xmedium"
        val marginXMedium =
            resources.getDimension(R.dimen.sm_margin_padding_size_xmedium).toInt()
        setPadding(marginXMedium, 0, marginXMedium, 0)

        // android:background="?selectableItemBackground"
        val outValue = TypedValue()
        getContext().theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)

        // app:key, app:value
        attrs?.let { attributes ->
            val typedArray = context
                .obtainStyledAttributes(attributes, R.styleable.SMTitleValueField, defStyleAttr, 0)

            val title = typedArray.getString(R.styleable.SMTitleValueField_sm_title) ?: ""
            val value = typedArray.getString(R.styleable.SMTitleValueField_sm_value) ?: ""
            val icon = typedArray.getResourceId(R.styleable.SMTitleValueField_sm_icon, 0)

            typedArray.recycle()

            keyTextView.text = title
            valueTextView.text = value
            setIcon(icon)
        }
    }

    companion object {
        const val HORIZONTAL: Int = 0
        const val VERTICAL: Int = 1
    }
}