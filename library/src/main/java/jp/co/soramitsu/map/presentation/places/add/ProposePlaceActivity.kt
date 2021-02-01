package jp.co.soramitsu.map.presentation.places.add

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.*
import com.google.android.gms.maps.model.LatLng
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.presentation.places.add.image.PhotoFragment
import jp.co.soramitsu.map.presentation.places.add.image.PhotoShower
import jp.co.soramitsu.map.presentation.places.add.schedule.AddScheduleFragment
import jp.co.soramitsu.map.presentation.places.add.schedule.ScheduleFragmentHost

class ProposePlaceActivity
    : AppCompatActivity(R.layout.sm_activity_place_proposal), PhotoShower, ScheduleFragmentHost {

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

    @ExperimentalStdlibApi
    override fun showScheduleFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AddScheduleFragment(), "ScheduleFragment")
            .addToBackStack(null)
            .commit()
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

    override fun showPhoto(photoUri: Uri, sharedView: View) {
        val photoFragment = PhotoFragment.newInstance(photoUri).apply {
            sharedElementEnterTransition = SharedPhotoTransition()
            enterTransition = Fade()
            sharedElementReturnTransition = SharedPhotoTransition()
        }

        supportFragmentManager.beginTransaction()
            .addSharedElement(sharedView, sharedView.transitionName)
            // todo: use replace when source fragment's scroll position and data will be stored
            .add(R.id.container, photoFragment, "PhotoFragment")
            .addToBackStack(null)
            .commit()
    }

    private class SharedPhotoTransition : TransitionSet() {
        init {
            ordering = ORDERING_TOGETHER;
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            addTransition(ChangeImageTransform())
        }
    }
}
