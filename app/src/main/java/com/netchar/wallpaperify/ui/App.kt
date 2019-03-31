package com.netchar.wallpaperify.ui

import android.app.Activity
import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.netchar.wallpaperify.BuildConfig
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
        DaggerAppComponent.builder().context(this).create(this) as AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector
}
