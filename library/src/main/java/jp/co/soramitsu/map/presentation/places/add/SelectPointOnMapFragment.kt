package jp.co.soramitsu.map.presentation.places.add

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.animation.doOnCancel
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import jp.co.soramitsu.map.BuildConfig
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.databinding.SmFragmentSelectPointOnMapBinding
import jp.co.soramitsu.map.ext.addressString
import jp.co.soramitsu.map.ext.toPosition
import jp.co.soramitsu.map.presentation.places.add.schedule.PlaceProposalViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

internal class SelectPointOnMapFragment : Fragment(R.layout.sm_fragment_select_point_on_map) {

    private var googleMap: GoogleMap? = null
    private var pointerAnimator: ValueAnimator? = null

    private lateinit var placeProposalViewModel: PlaceProposalViewModel

    private var _binding: SmFragmentSelectPointOnMapBinding? = null
    private val binding get() = _binding!!

    private var getAddressJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SmFragmentSelectPointOnMapBinding.bind(view)

        placeProposalViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.NewInstanceFactory()
        )[PlaceProposalViewModel::class.java]

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { onMapReady(it) }

        binding.closeScreenButton.setOnClickListener { activity?.onBackPressed() }
        binding.doneButton.setOnClickListener {
            googleMap?.cameraPosition?.target?.let {
                placeProposalViewModel.position = it.toPosition()
                placeProposalViewModel.address = binding.addressEditText.text.toString()
            }
            activity?.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()

        stopPointerAnimation()

        _binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onMapReady(map: GoogleMap?) {
        googleMap = map

        placeProposalViewModel.position?.let { position ->
            val latLng = LatLng(position.latitude, position.longitude)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
        }

        map?.setOnCameraIdleListener {
            stopPointerAnimation()
            updateAddress()
        }

        map?.setOnCameraMoveCanceledListener {
            stopPointerAnimation()
            updateAddress()
        }

        map?.setOnCameraMoveStartedListener { startPointerAnimation() }
    }

    private fun updateAddress() {
        googleMap?.cameraPosition?.target?.let { target ->
            getAddressJob?.cancel()
            getAddressJob = lifecycleScope.launch {
                binding.addressEditText.isEnabled = false
                binding.addressEditText.setText(target.toPosition().addressString(requireContext()))
                binding.addressEditText.isEnabled = true
            }
        }
    }

    private fun startPointerAnimation() {
        log("startPointerAnimation")

        val delta = resources.getDimension(R.dimen.sm_margin_padding_size_small)
        pointerAnimator = ValueAnimator.ofFloat(0f, -delta, 0f)
        pointerAnimator?.duration = TimeUnit.SECONDS.toMillis(1)
        pointerAnimator?.repeatMode = ValueAnimator.RESTART
        pointerAnimator?.repeatCount = ValueAnimator.INFINITE
        pointerAnimator?.addUpdateListener {
            val dy = it.animatedValue as Float
            binding.arrowsImageView.translationY = dy
            binding.arrowView.translationY = dy
        }
        pointerAnimator?.doOnCancel {
            binding.arrowsImageView.translationY = 0f
            binding.arrowView.translationY = 0f
        }
        pointerAnimator?.start()
    }

    private fun stopPointerAnimation() {
        log("stopPointerAnimation")

        pointerAnimator?.cancel()
        pointerAnimator = null
    }

    private fun log(message: String) {
        if (BuildConfig.DEBUG) Log.d("MapView", message)
    }

    private companion object {
        private const val DEFAULT_ZOOM = 16f
    }
}
