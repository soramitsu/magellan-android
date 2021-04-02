package jp.co.soramitsu.map.presentation

import android.content.Context
import android.graphics.*
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import jp.co.soramitsu.map.R

/**
 * Custom icon generator that will be used to create a bitmap for cluster. Each bitmap
 * will be created inside [createClusterDrawable] method.
 *
 * You must invoke [initWithContext] method to initialize color and text configuration
 */
class IconGenerator(context: Context) {

    private var clusterIconSize: Float = 0f

    @ColorInt
    private var clusterIconColor: Int = Color.BLACK

    @ColorInt
    private var textColor: Int = Color.WHITE

    private var textSize: Float = 0f

    private val paint: Paint = Paint()

    init {
        clusterIconSize = context.resources.getDimension(R.dimen.sm_cluster_icon_size)
        clusterIconColor = ContextCompat.getColor(context, R.color.sm_cluster_background)
        textSize = context.resources.getDimension(R.dimen.sm_cluster_text_size)
    }

    fun createClusterDrawable(text: String): Bitmap {
        val bitmap = Bitmap.createBitmap(
            clusterIconSize.toInt(),
            clusterIconSize.toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        // circular background
        paint.color = clusterIconColor
        val cx = clusterIconSize / 2
        val cy = clusterIconSize / 2
        val r = clusterIconSize / 2
        canvas.drawCircle(cx, cy, r, paint)

        // text with cluster size
        paint.color = textColor
        paint.textSize = textSize
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        val startX = cx - textBounds.width() / 2
        val startY = cy + textBounds.height() / 2
        canvas.drawText(text, startX, startY, paint)

        return bitmap
    }
}
