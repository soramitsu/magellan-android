package jp.co.soramitsu.map.presentation.places.add.image

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.databinding.SmFragmentPhotoBinding

internal class PhotoFragment : Fragment(R.layout.sm_fragment_photo) {

    private var _binding: SmFragmentPhotoBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = SmFragmentPhotoBinding.bind(view)

        val photoUri = requireNotNull(requireArguments().getParcelable<Uri>(EXTRA_PHOTO_URI))
        binding.image.transitionName = generateShareImageTransitionName(photoUri)
        Glide.with(this).load(photoUri).into(binding.image)

        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        private const val EXTRA_PHOTO_URI = "PhotoUri"

        fun newInstance(photoUri: Uri): PhotoFragment = PhotoFragment().apply {
            arguments = bundleOf(EXTRA_PHOTO_URI to photoUri)
        }
    }
}
