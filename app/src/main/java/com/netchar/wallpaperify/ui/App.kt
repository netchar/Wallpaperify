/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.wallpaperify.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.netchar.common.exceptions.UncaughtExceptionHandler
import com.netchar.common.utils.DebugTree
import com.netchar.common.utils.ReleaseTree
import com.netchar.repository.preferences.IPreferences
import com.netchar.wallpaperify.BuildConfig
import com.netchar.wallpaperify.di.AppComponent
import com.netchar.wallpaperify.di.DaggerAppComponent
import com.netchar.wallpaperify.infrastructure.BuildPreferences
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    init {
        instance = this
    }

    companion object {
        private lateinit var instance: App
        fun get() = instance
    }

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var preferences: IPreferences

    private val prefs = BuildPreferences(this)

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .context(this)
                .buildPrefs(prefs)
                .build()
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Fabric.with(this, Crashlytics())
            Timber.plant(ReleaseTree())
        }

        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler.inContext(this, prefs))

        component.inject(this)
        AndroidThreeTen.init(this)

        AppCompatDelegate.setDefaultNightMode(preferences.themeMode)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return activityInjector
    }
}
