package jp.co.soramitsu.map.presentation.places.add.image

import android.net.Uri

interface ImagesSelectionListener {
    fun onImagesSelected(selectedImages: List<Uri>, imagePickerCode: ImagePickerCode)
}
