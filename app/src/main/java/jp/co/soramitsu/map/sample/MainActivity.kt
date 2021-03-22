package jp.co.soramitsu.map.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import jp.co.soramitsu.map.presentation.SoramitsuMapFragment
import jp.co.soramitsu.map.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            val fragment: Fragment = when (menuItem.itemId) {
                R.id.dashboard -> SoramitsuMapFragment()
                R.id.map_two_tabs -> SoramitsuMapFragment()
                else -> SoramitsuMapFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .commit()

            true
        }

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.dashboard
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