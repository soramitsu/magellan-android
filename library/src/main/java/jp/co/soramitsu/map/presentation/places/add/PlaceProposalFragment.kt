package jp.co.soramitsu.map.presentation.places.add

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.gms.maps.model.LatLng
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.presentation.places.add.image.*
import jp.co.soramitsu.map.presentation.places.add.schedule.ScheduleFragmentHost
import jp.co.soramitsu.map.presentation.places.add.schedule.ScheduleViewModel
import kotlinx.android.synthetic.main.sm_fragment_place_proposal.*

class PlaceProposalFragment : Fragment(R.layout.sm_fragment_place_proposal),
    SelectPlaceCategoryFragment.OnCategorySelected, ImagesSelectionListener {

    private lateinit var logoAdapter: RemovableImagesAdapter
    private lateinit var photosAdapter: RemovableImagesAdapter

    // shared between PlaceProposalFragment and AddScheduleFragment to
    // apply schedule changes and display them to user
    private lateinit var scheduleViewModel: ScheduleViewModel

    private val requestManager: RequestManager?
        get() = try {
            Glide.with(this)
        } catch (exception: Exception) {
            null
        }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduleViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.NewInstanceFactory()
        )[ScheduleViewModel::class.java]

        scheduleViewModel.schedule.observe(viewLifecycleOwner) { schedule ->
            scheduleSection.schedule = schedule
        }

        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        addressTextView.text = requireArguments().getString(EXTRA_ADDRESS)

        scheduleSection.setOnAddButtonClickListener {
            (activity as? ScheduleFragmentHost)?.showScheduleFragment()
        }

        scheduleSection.setOnChangeScheduleButtonClickListener {
            (activity as? ScheduleFragmentHost)?.showScheduleFragment()
        }

        categoryTextView.setOnClickListener {
            SelectPlaceCategoryFragment().show(childFragmentManager, "SelectCategoryFragment")
        }

        addLogoTextView.setOnClickListener {
            SingleChoiceBottomSheetImagePicker().show(childFragmentManager, "LogoPicker")
        }

        addPhotoTextView.setOnClickListener {
            MultichoiceBottomSheetImagePicker().show(childFragmentManager, "PhotosPicker")
        }

        requestManager?.let { glideRequestManager ->
            logoAdapter = RemovableImagesAdapter(glideRequestManager)
            logoAdapter.setOnButtonClickListener {
                SingleChoiceBottomSheetImagePicker().show(childFragmentManager, "LogoPicker")
            }
            logoAdapter.setOnImageSelectedListener { imageUri ->
                val selectedItemPosition = logoAdapter.items
                    .indexOfFirst { it is RemovableImagesAdapter.RemovableImageListItem.Image && it.imageUri == imageUri }
                val viewHolder = logoRecyclerView.findViewHolderForAdapterPosition(selectedItemPosition)
                viewHolder as RemovableImagesAdapter.BaseViewHolder.ImageViewHolder
                showPhoto(viewHolder.imageView, imageUri)
            }
            logoAdapter.setOnRemoveImageClickListener {
                logoRecyclerView.visibility = View.GONE
                addLogoTextView.visibility = View.VISIBLE
            }
            logoRecyclerView.adapter = logoAdapter

            photosAdapter = RemovableImagesAdapter(glideRequestManager)
            photosAdapter.setOnButtonClickListener {
                MultichoiceBottomSheetImagePicker().show(childFragmentManager, "PhotosPicker")
            }
            photosAdapter.setOnImageSelectedListener { imageUri ->
                val selectedItemPosition = photosAdapter.items
                    .indexOfFirst { it is RemovableImagesAdapter.RemovableImageListItem.Image && it.imageUri == imageUri }
                val viewHolder = photosRecyclerView.findViewHolderForAdapterPosition(selectedItemPosition)
                viewHolder as RemovableImagesAdapter.BaseViewHolder.ImageViewHolder
                showPhoto(viewHolder.imageView, imageUri)
            }
            photosAdapter.setOnRemoveImageClickListener { uri ->
                val imageItems = photosAdapter.items
                    .filterIsInstance(RemovableImagesAdapter.RemovableImageListItem.Image::class.java)
                val imageToRemove = imageItems.find { it.imageUri == uri }
                imageToRemove?.let {
                    photosAdapter.update(photosAdapter.items - imageToRemove)

                    val removedLastImage = imageItems.size == 1
                    if (removedLastImage) {
                        photosRecyclerView.visibility = View.GONE
                        addPhotoTextView.visibility = View.VISIBLE
                    }
                }
            }
            photosRecyclerView.adapter = photosAdapter
        } ?: activity?.onBackPressed()
    }

    override fun onImagesSelected(selectedImages: List<Uri>, imagePickerCode: ImagePickerCode) =
        when (imagePickerCode) {
            ImagePickerCode.SINGLE_CHOICE -> onLogoSelected(selectedImages.first())
            ImagePickerCode.MULTICHOICE -> onPhotosSelected(selectedImages)
        }

    fun withParams(position: LatLng, address: String) = this.apply {
        arguments = bundleOf(
            EXTRA_POSITION to position,
            EXTRA_ADDRESS to address
        )
    }

    override fun onCategorySelected(category: Category) {
        categoryTextView.category = category
    }

    private fun onLogoSelected(logoUri: Uri) {
        logoRecyclerView.visibility = View.VISIBLE
        addLogoTextView.visibility = View.GONE

        val items = listOf(
            RemovableImagesAdapter.RemovableImageListItem.Button(
                UPDATE_LOGO_BUTTON, R.drawable.sm_ic_autorenew_24
            ),
            RemovableImagesAdapter.RemovableImageListItem.Image(logoUri)
        )
        logoAdapter.update(items)
    }

    private fun onPhotosSelected(selectedImages: List<Uri>) {
        photosRecyclerView.visibility = View.VISIBLE
        addPhotoTextView.visibility = View.GONE

        val buttons = listOf<RemovableImagesAdapter.RemovableImageListItem>(
            RemovableImagesAdapter.RemovableImageListItem.Button(
                ADD_PHOTO_BUTTON, R.drawable.sm_ic_add_24dp
            )
        )
        val images = selectedImages.map { uri ->
            RemovableImagesAdapter.RemovableImageListItem.Image(uri)
        }
        photosAdapter.update(buttons + images)
    }

    private fun showPhoto(sharedView: View, photoUri: Uri) {
        (activity as? PhotoShower)?.showPhoto(photoUri, sharedView)
    }

    private companion object {
        private const val EXTRA_POSITION = "jp.co.soramitsu.map.presentation.places.add.Position"
        private const val EXTRA_ADDRESS = "jp.co.soramitsu.map.presentation.places.add.Address"

        private const val ADD_PHOTO_BUTTON = 1
        private const val UPDATE_LOGO_BUTTON = 2
    }
}