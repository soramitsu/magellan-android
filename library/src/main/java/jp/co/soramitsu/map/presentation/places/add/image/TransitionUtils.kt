package jp.co.soramitsu.map.presentation.places.add.image

import android.net.Uri

fun generateShareImageTransitionName(uri: Uri): String = "ImageTransition:$uri"