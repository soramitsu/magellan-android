package jp.co.soramitsu.map

import android.util.Log

interface Logger {
    fun log(tag: String = "SoramitsuMap", message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun log(tag: String = "SoramitsuMap", throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, throwable)
        }
    }
}