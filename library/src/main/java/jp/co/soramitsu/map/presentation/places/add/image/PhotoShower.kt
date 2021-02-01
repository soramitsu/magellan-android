package jp.co.soramitsu.map.presentation.places.add.image

import android.net.Uri
import android.view.View

interface PhotoShower {
    fun showPhoto(photoUri: Uri, sharedView: View)
}