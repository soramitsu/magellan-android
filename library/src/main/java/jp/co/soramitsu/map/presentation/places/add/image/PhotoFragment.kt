package jp.co.soramitsu.map.presentation.places.add.image

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import jp.co.soramitsu.map.R
import kotlinx.android.synthetic.main.sm_fragment_photo.*

class PhotoFragment : Fragment(R.layout.sm_fragment_photo) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoUri = requireNotNull(requireArguments().getParcelable<Uri>(EXTRA_PHOTO_URI))
        image.transitionName = generateShareImageTransitionName(photoUri)
        Glide.with(this).load(photoUri).into(image)

        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    companion object {
        fun newInstance(photoUri: Uri): PhotoFragment = PhotoFragment().apply {
            arguments = bundleOf(EXTRA_PHOTO_URI to photoUri)
        }

        private const val EXTRA_PHOTO_URI = "PhotoUri"
    }
}