package jp.co.soramitsu.map.presentation.places.add.image

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.view.doOnLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.ext.images
import kotlinx.android.synthetic.main.sm_multichoice_image_picker.*
import kotlinx.android.synthetic.main.sm_single_choice_image_picker.imagesRecyclerView

class MultichoiceBottomSheetImagePicker : BottomSheetDialogFragment() {

    private lateinit var imagesAdapter: SelectableImagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_multichoice_image_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnLayout { (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT) }

        imagesAdapter = SelectableImagesAdapter(Glide.with(this), true)
        imagesRecyclerView.adapter = imagesAdapter
        imagesAdapter.setOnImageSelectedListener { selectedItem ->
            val updatedItems = imagesAdapter.items.toMutableList()
            val updateIdx = updatedItems.indexOf(selectedItem)
            if (updateIdx >= 0) {
                updatedItems[updateIdx] = selectedItem.copy(selected = selectedItem.selected.not())
                imagesAdapter.update(updatedItems)
            }

            updateConfirmButtonTitle(updatedItems.count { it.selected })
        }

        updateConfirmButtonTitle(0)
        confirmSelectedPhotosButton.setOnClickListener {
            val listener = parentFragment as? ImagesSelectionListener
            val selectedUris = imagesAdapter.items.filter { it.selected }.map { it.imageUri }
            listener?.onImagesSelected(selectedUris, ImagePickerCode.MULTICHOICE)
            dismiss()
        }

        val padding = imagesRecyclerView.resources
            .getDimension(R.dimen.sm_margin_padding_size_medium)
            .toInt()
        imagesRecyclerView.addItemDecoration(SpanItemDecorator(padding))

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            displayImages()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            displayImages()
        }
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    private fun displayImages() {
        val externalGallery =
            context?.contentResolver?.images(MediaStore.Images.Media.EXTERNAL_CONTENT_URI).orEmpty()
        val internalGallery =
            context?.contentResolver?.images(MediaStore.Images.Media.INTERNAL_CONTENT_URI).orEmpty()
        val allPublicImagesOnDevice = (externalGallery + internalGallery).sortedBy { it.second }
        val images = allPublicImagesOnDevice
            .map { SelectableImagesAdapter.SelectableImage(it.first, false) }
        imagesAdapter.update(images)
    }

    private fun updateConfirmButtonTitle(numberOfSelectedPhotos: Int) {
        val confirmButtonTitle = resources.getQuantityString(
            R.plurals.sm_select_number_of_photos,
            numberOfSelectedPhotos,
            numberOfSelectedPhotos
        )
        confirmSelectedPhotosButton.text = confirmButtonTitle
    }

    private companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1
    }
}
