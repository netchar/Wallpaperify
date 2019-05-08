package com.netchar.wallpaperify.ui

import android.app.Activity
import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.netchar.common.utils.DebugTree
import com.netchar.common.utils.ReleaseTree
import com.netchar.wallpaperify.BuildConfig
import com.netchar.wallpaperify.di.AppComponent
import com.netchar.wallpaperify.di.DaggerAppComponent
import com.netchar.wallpaperify.infrastructure.BuildPreferences
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    init {
        instance = this
    }

    companion object {
        private lateinit var instance: App
        fun get() = instance
    }

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
            .context(this)
            .buildPrefs(BuildPreferences())
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector
}
