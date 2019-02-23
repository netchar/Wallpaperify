package com.netchar.wallpaperify.base

import android.app.Application
import timber.log.Timber
import com.netchar.wallpaperify.BuildConfig

class MainApp : Application() {

    private val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .application(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
