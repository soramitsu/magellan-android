package jp.co.soramitsu.map.presentation.places.add.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import jp.co.soramitsu.map.Logger
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import java.io.ByteArrayOutputStream

internal interface ImageUriToByteArrayConverter {
    fun convert(fileUriString: String): ByteArray
}

internal class DefaultImageUriToByteArrayConverter(
    private val context: Context,
    private val logger: Logger = SoramitsuMapLibraryConfig.logger
) : ImageUriToByteArrayConverter {

    override fun convert(fileUriString: String): ByteArray {
        return kotlin.runCatching {
            val uri = Uri.parse(fileUriString)
            context.contentResolver.openInputStream(uri)?.let { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, bos)
                bitmap.recycle()
                bos.toByteArray()
            } ?: ByteArray(0)
        }.onFailure { logger.log("UriConverter", it) }.getOrDefault(ByteArray(0))
    }

    private companion object {
        private const val IMAGE_QUALITY = 70
    }
}
