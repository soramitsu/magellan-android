package jp.co.soramitsu.map.presentation.places

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.model.Place
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.presentation.SoramitsuMapViewModel
import kotlinx.android.synthetic.main.sm_place_bottom_sheet.*

internal class PlaceFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: SoramitsuMapViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.sm_place_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.fragments.find { it is SoramitsuMapFragment }?.let { hostFragment ->
            viewModel = ViewModelProvider(hostFragment, ViewModelProvider.NewInstanceFactory())
                .get(SoramitsuMapViewModel::class.java)

            viewModel.placeSelected().value?.let { place -> bindBottomSheetWithPlace(place) }
            viewModel.placeSelected().observe(viewLifecycleOwner, Observer { place ->
                place?.let { bindBottomSheetWithPlace(place) }
            })
        }
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            val parent = (view?.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)

            dialog?.window?.setDimAmount(0f)
        }, 50)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        viewModel.onPlaceSelected(null)
    }

    private fun bindBottomSheetWithPlace(place: Place) {
        // header info
        placeNameTextView.text = place.name

        val placeAddress = if (place.address.isNotBlank()) {
            resources.getString(R.string.sm_category_address, place.category.name, place.address)
        } else {
            place.category.name
        }
        placeAddressTextView.text = placeAddress

        placeRatingBar.rating = place.rating
        placeRatingTextView.text = place.rating.toString()
        placeReviewsTextView.text =
            context?.resources?.getQuantityString(
                R.plurals.sm_review,
                place.reviews.size,
                place.reviews.size
            )

        // additional info
        additionalInfoMobilePhone.visibility =
            if (place.phone.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoWebsite.visibility =
            if (place.website.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoFacebook.visibility =
            if (place.facebook.isEmpty()) View.GONE else View.VISIBLE
        additionalInfoAddress.visibility =
            if (place.address.isEmpty()) View.GONE else View.VISIBLE

        additionalInfoMobilePhone.text = place.phone
        additionalInfoWebsite.text = place.website
        additionalInfoFacebook.text = "facebook.com/${place.facebook}"
        additionalInfoAddress.text = place.address

        additionalInfoMobilePhone.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${place.phone}")
            })
        }

        additionalInfoWebsite.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(place.website)
            })
        }

        additionalInfoFacebook.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("facebook://facebook.com/${place.facebook}")
            }
            if (facebookIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(facebookIntent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://facebook.com/${place.facebook}")
                })
            }
        }

        additionalInfoAddress.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:0,0?q=${Uri.encode(place.address)}")
                `package` = "com.google.android.apps.maps"
            })
        }

        closePlaceInfoButton.setOnClickListener {
            dismiss()
        }
    }
}