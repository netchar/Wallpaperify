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

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.jakewharton.threetenabp.AndroidThreeTen
import com.netchar.common.exceptions.UncaughtExceptionHandler
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

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler.inContext(this))

        component.inject(this)
        AndroidThreeTen.init(this)
        // todo: add setting screen option
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector
}
