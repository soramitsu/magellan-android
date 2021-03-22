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
import jp.co.soramitsu.map.databinding.SmAddPlaceFragmentBinding
import jp.co.soramitsu.map.ext.addressString
import jp.co.soramitsu.map.ext.toPosition
import kotlinx.coroutines.launch

internal class AddPlaceFragment : BottomSheetDialogFragment() {

    interface Listener {
        fun onAddPlaceButtonClick(position: LatLng, address: String)
    }

    private val position: LatLng get() = requireArguments().getParcelable<LatLng>(EXTRA_POSITION) as LatLng

    private var _binding: SmAddPlaceFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sm_add_place_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SmAddPlaceFragmentBinding.bind(view)

        view.doOnLayout {
            val parent = (view.parent as? View)
            parent?.setBackgroundColor(Color.TRANSPARENT)
            dialog?.window?.setDimAmount(0f)
        }

        binding.closeButton.setOnClickListener { dismiss() }

        lifecycleScope.launch {
            binding.addPlaceButton.isEnabled = false
            val address =  position.toPosition().addressString(requireContext())
            binding.placeAddressTextView.text = address
            binding.addressTextView.text = address
            binding.addressTextView.visibility = if (address.isEmpty()) View.GONE else View.VISIBLE

            binding.addPlaceButton.isEnabled = true
            binding.addPlaceButton.setOnClickListener {
                (targetFragment as? Listener)?.onAddPlaceButtonClick(position, address)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    fun withPosition(position: LatLng) = this.apply {
        arguments = bundleOf(EXTRA_POSITION to position)
    }

    private companion object {
        private const val EXTRA_POSITION = "ExtraPosition"
    }
}