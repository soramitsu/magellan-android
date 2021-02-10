package jp.co.soramitsu.map.presentation.places.add

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.ext.addressString
import jp.co.soramitsu.map.ext.toPosition
import jp.co.soramitsu.map.model.Position
import kotlinx.android.synthetic.main.sm_add_place_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPlaceFragment : BottomSheetDialogFragment() {

    interface Listener {
        fun onAddPlaceButtonClick(position: LatLng, address: String)
    }

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

        lifecycleScope.launch {
            addPlaceButton.isEnabled = false
            val address =  position.toPosition().addressString(requireContext())
            placeAddressTextView.text = address
            addressTextView.text = address
            addressTextView.visibility = if (address.isEmpty()) View.GONE else View.VISIBLE

            addPlaceButton.isEnabled = true
            addPlaceButton.setOnClickListener {
                (targetFragment as? Listener)?.onAddPlaceButtonClick(position, address)
                dismiss()
            }
        }
    }

    fun withPosition(position: LatLng) = this.apply {
        arguments = bundleOf(EXTRA_POSITION to position)
    }

    private companion object {
        private const val EXTRA_POSITION = "ExtraPosition"
    }
}