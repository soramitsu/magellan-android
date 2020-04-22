package jp.co.soramitsu.map

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import jp.co.soramitsu.map.presentation.GoogleMapActivity
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        show_map_activity.setOnClickListener {
            startActivity(Intent(requireActivity(), GoogleMapActivity::class.java))
        }
    }
}