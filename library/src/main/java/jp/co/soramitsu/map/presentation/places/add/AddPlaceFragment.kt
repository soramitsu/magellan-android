package jp.co.soramitsu.map.presentation.places.add

import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import kotlinx.android.synthetic.main.sm_add_place_fragment.*

class AddPlaceFragment : BottomSheetDialogFragment() {

    private val position: LatLng get() = requireArguments().getParcelable<LatLng>(EXTRA_POSITION) as LatLng

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_add_place_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.doOnLayout {
            val parent = (view.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)
            dialog?.window?.setDimAmount(0f)
        }

        closeButton.setOnClickListener { dismiss() }

        val address = addressString()
        placeAddressTextView.text = address
        addressTextView.text = address
        addressTextView.visibility = if (address.isEmpty()) View.GONE else View.VISIBLE

        addPlaceButton.setOnClickListener {
            context?.let { context ->
                val intent = ProposePlaceActivity.createLaunchIntent(context, position, address)
                context.startActivity(intent)
            }
        }
    }

    fun withPosition(position: LatLng) = this.apply {
        arguments = bundleOf(EXTRA_POSITION to position)
    }

    private fun addressString(): String {
        val geocoder = Geocoder(requireContext())
        val addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1)
        val haveAddress = addresses.isNotEmpty() && addresses[0].maxAddressLineIndex >= 0
        return if (haveAddress) addresses[0].getAddressLine(0) else ""
    }

    private companion object {
        private const val EXTRA_POSITION = "ExtraPosition"
    }
}