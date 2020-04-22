package jp.co.soramitsu.feature.maps.impl.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.co.soramitsu.feature_map_impl.R

class GoogleMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
    }
}