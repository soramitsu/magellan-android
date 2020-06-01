package jp.co.soramitsu.map.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener { menuItem ->
            val fragment: Fragment = when (menuItem.itemId) {
                R.id.dashboard -> DashboardFragment()
                R.id.map_two_tabs -> SoramitsuMapFragment()
                else -> SoramitsuMapFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit()

            true
        }

        if (savedInstanceState == null) {
            bottom_navigation.selectedItemId = R.id.dashboard
        }
    }
}