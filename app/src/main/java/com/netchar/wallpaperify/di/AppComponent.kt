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

package com.netchar.wallpaperify.di

import android.content.Context
import com.netchar.common.di.BillingModule
import com.netchar.common.di.CommonModule
import com.netchar.common.utils.IBuildConfig
import com.netchar.local.di.DaoModule
import com.netchar.local.di.PreferencesModule
import com.netchar.remote.di.ApiModule
import com.netchar.remote.di.NetworkModule
import com.netchar.repository.di.RepositoryModule
import com.netchar.wallpaperify.di.modules.ActivityBindingModule
import com.netchar.wallpaperify.di.modules.GlideConfigurationModule
import com.netchar.wallpaperify.di.modules.ViewModelBindingModule
import com.netchar.wallpaperify.ui.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import netchar.com.auth.di.OAuthModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            CommonModule::class,
            PreferencesModule::class,
            OAuthModule::class,
            RepositoryModule::class,
            ActivityBindingModule::class,
            ViewModelBindingModule::class,
            NetworkModule::class,
            ApiModule::class,
            DaoModule::class,
            BillingModule::class
        ]
)
interface AppComponent : AndroidInjector<App> {
    fun inject(glideConfig: GlideConfigurationModule)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun buildPrefs(prefs: IBuildConfig): Builder

        fun build(): AppComponent
    }
}
