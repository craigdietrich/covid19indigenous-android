package com.craigdietrich.covid19indigenous

import android.app.Application

class Covid19Indigenous : Application() {
    companion object {
        lateinit var instance: Covid19Indigenous
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}