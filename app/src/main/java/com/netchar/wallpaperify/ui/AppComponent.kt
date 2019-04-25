package com.netchar.wallpaperify.ui

import android.content.Context
import com.netchar.common.di.CommonModule
import com.netchar.common.utils.IBuildPreferences
import com.netchar.local.di.PreferencesModule
import com.netchar.repository.di.RepositoryModule
import com.netchar.remote.di.NetworkModule
import com.netchar.wallpaperify.di.modules.ActivityBindingModule
import com.netchar.wallpaperify.di.modules.ViewModelBindingModule
import com.netchar.wallpaperify.di.modules.GlideConfigurationModule
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
            NetworkModule::class,
            RepositoryModule::class,
            ActivityBindingModule::class,
            ViewModelBindingModule::class
        ]
)
interface AppComponent : AndroidInjector<App> {

    fun inject(glideConfig: GlideConfigurationModule)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun buildPrefs(prefs: IBuildPreferences): Builder

        fun build(): AppComponent
    }
}
