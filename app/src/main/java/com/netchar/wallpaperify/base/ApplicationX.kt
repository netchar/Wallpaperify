package com.netchar.wallpaperify.base

import android.app.Application
import timber.log.Timber
import com.netchar.wallpaperify.BuildConfig

class ApplicationX : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
