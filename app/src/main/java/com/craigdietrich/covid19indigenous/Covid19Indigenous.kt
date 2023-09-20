package com.craigdietrich.covid19indigenous

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import java.util.*

class Covid19Indigenous : Application() {
    companion object {
        lateinit var instance: Covid19Indigenous
        lateinit var language: String
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        language = Locale.getDefault().language
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        language = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            newConfig.locales.get(0).language
        } else {
            newConfig.locale.language
        }
    }
}