package jp.co.soramitsu.map.presentation.places.add.image

import android.Manifest
import android.app.Activity
import android.content.Intent
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
import kotlinx.android.synthetic.main.sm_single_choice_image_picker.*
import java.util.*

class SingleChoiceBottomSheetImagePicker : BottomSheetDialogFragment() {

    private lateinit var imagesAdapter: SelectableImagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_single_choice_image_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnLayout { (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT) }

        imagesAdapter = SelectableImagesAdapter(Glide.with(this), false)
        imagesRecyclerView.adapter = imagesAdapter
        imagesAdapter.setOnImageSelectedListener { uri ->
            val listener = parentFragment as? ImagesSelectionListener
            val selectedImages = Collections.singletonList(uri)
            listener?.onImagesSelected(
                selectedImages.map { it.imageUri },
                ImagePickerCode.SINGLE_CHOICE
            )
            dismiss()
        }

        chooseFromGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.setType("image/*")
            startActivityForResult(galleryIntent, PICK_PHOTO_FROM_GALLERY_REQUEST_CODE);

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_PHOTO_FROM_GALLERY_REQUEST_CODE) {
            val selectedImageUri = data?.data ?: return
            val listener = parentFragment as? ImagesSelectionListener
            listener?.onImagesSelected(
                Collections.singletonList(selectedImageUri),
                ImagePickerCode.SINGLE_CHOICE
            )
            dismiss()
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

    private companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1
        private const val PICK_PHOTO_FROM_GALLERY_REQUEST_CODE = 2
    }
}
