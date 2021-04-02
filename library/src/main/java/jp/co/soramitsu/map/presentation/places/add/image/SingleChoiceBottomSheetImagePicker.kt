package jp.co.soramitsu.map.presentation.places.add.image

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.databinding.SmSingleChoiceImagePickerBinding
import jp.co.soramitsu.map.ext.images
import java.util.*

internal class SingleChoiceBottomSheetImagePicker : BottomSheetDialogFragment() {

    private lateinit var imagesAdapter: SelectableImagesAdapter

    private var _binding: SmSingleChoiceImagePickerBinding? = null
    private val binding get() = _binding!!

    private val imagePickerLauncher = registerForActivityResult(PickImageContract()) { imageUri ->
        setFragmentResult(REQUEST_KEY, bundleOf(EXTRA_IMAGES_URIS to arrayListOf(imageUri)))
        dismiss()
    }

    private val readExternalStoragePermissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) {
            displayImages()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_single_choice_image_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SmSingleChoiceImagePickerBinding.bind(view)

        view.doOnLayout { (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT) }

        imagesAdapter = SelectableImagesAdapter(Glide.with(this), false)
        binding.imagesRecyclerView.adapter = imagesAdapter
        imagesAdapter.setOnImageSelectedListener { uri ->
            val selectedImages = Collections.singletonList(uri)
            setFragmentResult(
                REQUEST_KEY, bundleOf(
                    EXTRA_IMAGES_URIS to ArrayList(selectedImages.map { it.imageUri })
                )
            )
            dismiss()
        }

        binding.chooseFromGallery.setOnClickListener { imagePickerLauncher.launch(Unit) }

        val padding = binding.imagesRecyclerView.resources
            .getDimension(R.dimen.sm_margin_padding_size_medium)
            .toInt()
        binding.imagesRecyclerView.addItemDecoration(SpanItemDecorator(padding))

        if (ActivityCompat.checkSelfPermission(requireActivity(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            displayImages()
        } else {
            readExternalStoragePermissionRequestLauncher.launch(READ_EXTERNAL_STORAGE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

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

    class PickImageContract : ActivityResultContract<Unit, Uri?>() {

        override fun createIntent(context: Context, input: Unit?): Intent {
            return Intent(Intent.ACTION_PICK).apply { type = "image/*" }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode != Activity.RESULT_OK || intent?.data == null) return null

            return intent.data
        }
    }

    companion object {
        const val REQUEST_KEY = "SingleChoiceBottomSheetImagePicker"
        const val EXTRA_IMAGES_URIS = "ImagesUris"
    }
}
