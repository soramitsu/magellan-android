package jp.co.soramitsu.map.presentation.places.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.transition.*
import com.google.android.gms.maps.model.LatLng
import jp.co.soramitsu.map.R
import jp.co.soramitsu.map.ext.toPosition
import jp.co.soramitsu.map.presentation.places.add.image.PhotoFragment
import jp.co.soramitsu.map.presentation.places.add.image.PhotoShower
import jp.co.soramitsu.map.presentation.places.add.schedule.AddScheduleFragment
import jp.co.soramitsu.map.presentation.places.add.schedule.PlaceProposalViewModel
import jp.co.soramitsu.map.presentation.places.add.schedule.Screen

class ProposePlaceActivity :
    AppCompatActivity(R.layout.sm_activity_place_proposal), PhotoShower {

    private lateinit var placeProposalViewModel: PlaceProposalViewModel

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        placeProposalViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[PlaceProposalViewModel::class.java]

        if (savedInstanceState == null) {
            val position = intent.extras?.getParcelable<LatLng>(EXTRA_POSITION)
            if (position == null) {
                finish()
                return
            }
            val address = intent.extras?.getString(EXTRA_ADDRESS).orEmpty()

            placeProposalViewModel.address = address
            placeProposalViewModel.position = position.toPosition()

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PlaceProposalFragment(), "PlaceProposalFragment")
                .commit()
        }
        placeProposalViewModel.screen.observe(this) { screen ->
            when (screen) {
                is Screen.PlaceProposal -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, PlaceProposalFragment(), "PlaceProposalFragment")
                        .commit()
                }

                is Screen.ChangePosition -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, SelectPointOnMapFragment(), "SelectPointOnMap")
                        .addToBackStack(null)
                        .commit()
                }

                is Screen.AddSchedule -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, AddScheduleFragment(), "ScheduleFragment")
                        .addToBackStack(null)
                        .commit()
                }
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
            ordering = ORDERING_TOGETHER
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            addTransition(ChangeImageTransform())
        }
    }

    class Contract : ActivityResultContract<Contract.Params, Boolean>() {

        data class Params(
            internal val position: LatLng,
            internal val address: String
        )

        override fun createIntent(context: Context, input: Params): Intent {
            return Intent(context, ProposePlaceActivity::class.java)
                .apply {
                    putExtra(EXTRA_POSITION, input.position)
                    putExtra(EXTRA_ADDRESS, input.address)
                }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
            return resultCode == Activity.RESULT_OK
        }
    }

    companion object {
        private const val EXTRA_POSITION = "jp.co.soramitsu.map.presentation.places.add.Position"
        private const val EXTRA_ADDRESS = "jp.co.soramitsu.map.presentation.places.add.Address"
    }
}
