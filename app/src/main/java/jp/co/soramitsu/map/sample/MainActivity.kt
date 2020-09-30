package jp.co.soramitsu.map.sample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

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

        // uncomment for automatic app restart every 20 seconds
//        Handler().postDelayed({
//            val intent = packageManager.getLaunchIntentForPackage(packageName)
//            intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
//            exitProcess(0)
//        }, 20_000)
    }
}