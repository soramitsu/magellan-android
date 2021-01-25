package jp.co.soramitsu.map.presentation.places.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import jp.co.soramitsu.map.R

class ProposePlaceActivity : AppCompatActivity(R.layout.sm_activity_place_proposal) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val position = intent.extras?.getParcelable<LatLng>(EXTRA_POSITION)
            if (position == null) {
                finish()
                return
            }
            val address = intent.extras?.getString(EXTRA_ADDRESS).orEmpty()
            val fragment = PlaceProposalFragment().withParams(position, address)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }

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
