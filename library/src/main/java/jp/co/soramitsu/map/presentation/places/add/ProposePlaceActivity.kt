package jp.co.soramitsu.map.presentation.places.add

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import jp.co.soramitsu.map.R

class ProposePlaceActivity : AppCompatActivity(R.layout.sm_activity_place_proposal) {

    companion object {

        private const val EXTRA_POSITION = "jp.co.soramitsu.map.presentation.places.add.Position"
        private const val EXTRA_ADDRESS = "jp.co.soramitsu.map.presentation.places.add.Address"

        fun createLaunchIntent(context: Context, placePosition: LatLng, address: String): Intent {
            return Intent(context, ProposePlaceActivity::class.java)
                .apply {
                    putExtra(EXTRA_POSITION, placePosition)
                    putExtra(EXTRA_ADDRESS, address)
                }
        }
    }
}
