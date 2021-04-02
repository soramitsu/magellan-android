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
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.SoramitsuMapLibraryConfig
import jp.co.soramitsu.map.databinding.SmMultichoiceImagePickerBinding
import jp.co.soramitsu.map.ext.images

internal class MultichoiceBottomSheetImagePicker : BottomSheetDialogFragment() {

    private lateinit var imagesAdapter: SelectableImagesAdapter

    private var _binding: SmMultichoiceImagePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_multichoice_image_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SmMultichoiceImagePickerBinding.bind(view)

        view.doOnLayout { (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT) }

        imagesAdapter = SelectableImagesAdapter(Glide.with(this), SoramitsuMapLibraryConfig.enableMultiplePlacePhotos)
        binding.imagesRecyclerView.adapter = imagesAdapter
        imagesAdapter.setOnImageSelectedListener { selectedItem ->
            if (SoramitsuMapLibraryConfig.enableMultiplePlacePhotos) {
                val updatedItems = imagesAdapter.items.toMutableList()
                val updateIdx = updatedItems.indexOf(selectedItem)
                if (updateIdx >= 0) {
                    updatedItems[updateIdx] = selectedItem.copy(selected = selectedItem.selected.not())
                    imagesAdapter.update(updatedItems)
                }

                updateConfirmButtonTitle(updatedItems.count { it.selected })
            } else {
                setFragmentResult(REQUEST_KEY, bundleOf(EXTRA_IMAGES_URIS to arrayListOf(selectedItem.imageUri)))
                dismiss()
            }
        }

        updateConfirmButtonTitle(0)
        binding.confirmSelectedPhotosButton.setOnClickListener {
            val selectedUris = imagesAdapter.items.filter { it.selected }.map { it.imageUri }
            setFragmentResult(REQUEST_KEY, bundleOf(EXTRA_IMAGES_URIS to ArrayList(selectedUris)))
            dismiss()
        }

        val padding = binding.imagesRecyclerView.resources
            .getDimension(R.dimen.sm_margin_padding_size_medium)
            .toInt()
        binding.imagesRecyclerView.addItemDecoration(SpanItemDecorator(padding))

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

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE &&
            ActivityCompat.checkSelfPermission(
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
        binding.confirmSelectedPhotosButton.text = confirmButtonTitle
    }

    companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1

        const val REQUEST_KEY = "MultichoiceBottomSheetImagePicker"
        const val EXTRA_IMAGES_URIS = "ImagesUris"
    }
}
