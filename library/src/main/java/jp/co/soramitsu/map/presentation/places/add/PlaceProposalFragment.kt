package jp.co.soramitsu.map.presentation.places.add

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Category
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.model.Schedule
import jp.co.soramitsu.map.presentation.places.add.image.*
import jp.co.soramitsu.map.presentation.places.add.schedule.PlaceProposalViewModel
import kotlinx.android.synthetic.main.sm_fragment_place_proposal.*

class PlaceProposalFragment :
    Fragment(R.layout.sm_fragment_place_proposal),
    SelectPlaceCategoryFragment.OnCategorySelected,
    ImagesSelectionListener {

    private lateinit var logoAdapter: RemovableImagesAdapter
    private lateinit var photosAdapter: RemovableImagesAdapter

    // shared between PlaceProposalFragment and AddScheduleFragment to
    // apply schedule changes and display them to user
    private lateinit var addPlaceViewModel: AddPlaceViewModel
    private lateinit var placeProposalViewModel: PlaceProposalViewModel

    private val requestManager: RequestManager?
        get() = try {
            Glide.with(this)
        } catch (exception: Exception) {
            null
        }

    @ExperimentalStdlibApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addPlaceViewModel = ViewModelProvider(
            this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val converter = DefaultImageUriToByteArrayConverter(requireContext().applicationContext)
                    return AddPlaceViewModel(converter) as T
                }
            }
        )[AddPlaceViewModel::class.java]
        placeProposalViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.NewInstanceFactory()
        )[PlaceProposalViewModel::class.java]

        addPlaceViewModel.viewState.observe(viewLifecycleOwner) { viewState -> viewState.render() }

        placeProposalViewModel.schedule.observe(viewLifecycleOwner) { schedule ->
            scheduleSection.schedule = schedule
        }

        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        addressTextView.text = placeProposalViewModel.address

        scheduleSection.setOnAddButtonClickListener {
            placeProposalViewModel.onAddScheduleButtonClicked()
        }

        scheduleSection.setOnChangeScheduleButtonClickListener {
            placeProposalViewModel.onChangeScheduleButtonClicked()
        }

        addressTextView.setOnClickListener {
            placeProposalViewModel.onChangeAddressButtonClicked()
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

            createAndSendForReviewButton.setOnClickListener {
                val position = placeProposalViewModel.position ?: return@setOnClickListener
                val category = categoryTextView.category ?: return@setOnClickListener
                val place = Place(
                    name = placeNameEditText.text.toString(),
                    address = addressTextView.text.toString(),
                    category = category,
                    position = position,
                    phone = placePhoneNumberEditText.text.toString(),
                    website = websiteEditText.text.toString(),
                    facebook = facebookEditText.text.toString(),
                    schedule = scheduleSection.schedule ?: Schedule()
                )
                addPlaceViewModel.addPlace(place)
            }
        } ?: activity?.onBackPressed()
    }

    override fun onImagesSelected(selectedImages: List<Uri>, imagePickerCode: ImagePickerCode) = when (imagePickerCode) {
        ImagePickerCode.SINGLE_CHOICE -> onLogoSelected(selectedImages.first())
        ImagePickerCode.MULTICHOICE -> onPhotosSelected(selectedImages)
    }

    override fun onCategorySelected(category: Category) {
        categoryTextView.category = category
    }

    private fun onLogoSelected(logoUri: Uri) {
        logoRecyclerView.visibility = View.VISIBLE
        addLogoTextView.visibility = View.GONE

        val items = listOf(
            RemovableImagesAdapter.RemovableImageListItem.Button(UPDATE_LOGO_BUTTON, R.drawable.sm_ic_autorenew_24),
            RemovableImagesAdapter.RemovableImageListItem.Image(logoUri)
        )
        logoAdapter.update(items)

        addPlaceViewModel.logoUriString = logoUri.toString()
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

        addPlaceViewModel.promoImageUriString = selectedImages.firstOrNull()?.toString().orEmpty()
    }

    private fun showPhoto(sharedView: View, photoUri: Uri) {
        (activity as? PhotoShower)?.showPhoto(photoUri, sharedView)
    }

    private fun AddPlaceViewState.render() = when (this) {
        AddPlaceViewState.Loading -> {
            progressBar.visibility = View.VISIBLE
            toggleUiAvailability(false)

            placeNameTextInputLayout.isErrorEnabled = false
        }

        AddPlaceViewState.Success -> {
            progressBar.visibility = View.GONE
            toggleUiAvailability(true)

            placeNameTextInputLayout.isErrorEnabled = false

            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }

        is AddPlaceViewState.Error -> {
            progressBar.visibility = View.GONE
            toggleUiAvailability(true)

            placeNameTextInputLayout.isErrorEnabled = false

            Toast.makeText(
                requireContext(),
                getString(R.string.sm_failed_to_upload_place_info),
                Toast.LENGTH_LONG
            ).show()
        }

        is AddPlaceViewState.ValidationFailed -> {
            progressBar.visibility = View.GONE
            toggleUiAvailability(true)

            when (this.invalidField) {
                AddPlaceViewState.ValidationFailed.Field.NAME -> {
                    placeNameTextInputLayout.isErrorEnabled = true
                    placeNameTextInputLayout.error = getString(R.string.sm_invalid_field_value)
                }
            }
        }
    }

    private fun toggleUiAvailability(enabled: Boolean) {
        scheduleSection.isEnabled = enabled
        categoryTextView.isEnabled = enabled
        addLogoTextView.isEnabled = enabled
        addPhotoTextView.isEnabled = enabled

        logoAdapter.enabled = enabled
        photosAdapter.enabled = enabled

        placeNameTextInputLayout.isEnabled = enabled
        placePhoneNumberTextInputLayout.isEnabled = enabled
        facebookTextInputLayout.isEnabled = enabled
        websiteTextInputLayout.isEnabled = enabled
    }

    private companion object {
        private const val ADD_PHOTO_BUTTON = 1
        private const val UPDATE_LOGO_BUTTON = 2
    }
}