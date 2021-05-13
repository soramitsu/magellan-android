package jp.co.soramitsu.map.presentation.places.add

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.databinding.SmAddPlaceFragmentBinding
import jp.co.soramitsu.map.ext.addressString
import jp.co.soramitsu.map.ext.toPosition
import kotlinx.coroutines.launch

internal class AddPlaceFragment : BottomSheetDialogFragment() {

    private val position: LatLng get() = requireArguments().getParcelable<LatLng>(EXTRA_POSITION) as LatLng

    private var _binding: SmAddPlaceFragmentBinding? = null
    private val binding get() = _binding!!

    private val launcher = registerForActivityResult(ProposePlaceActivity.Contract()) { success ->
        setFragmentResult(REQUEST_KEY, bundleOf(EXTRA_CANCELLED to !success))
        dismiss()
    }

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
            (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)
            dialog?.window?.setDimAmount(0f)
        }

        binding.closeButton.setOnClickListener { dialog?.cancel() }

        lifecycleScope.launch {
            binding.addPlaceButton.isEnabled = false
            val address = position.toPosition().addressString(requireContext())
            binding.placeAddressTextView.text = address
            binding.addressTextView.text = address
            binding.addressTextView.visibility = if (address.isEmpty()) View.GONE else View.VISIBLE

            binding.addPlaceButton.isEnabled = true
            binding.addPlaceButton.setOnClickListener {
                launcher.launch(ProposePlaceActivity.Contract.Params(position, address))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        setFragmentResult(REQUEST_KEY, bundleOf(EXTRA_CANCELLED to true))
    }

    fun withPosition(position: LatLng) = this.apply {
        arguments = bundleOf(EXTRA_POSITION to position)
    }

    companion object {
        const val REQUEST_KEY = "AddPlaceFragmentRequestKey"

        const val EXTRA_CANCELLED = "Dismiss"
        const val EXTRA_POSITION = "Position"
    }
}
