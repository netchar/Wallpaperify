package com.netchar.wallpaperify.ui

import android.content.Context
import com.netchar.common.di.CommonModule
import com.netchar.local.di.PreferencesModule
import com.netchar.remote.di.ApiModule
import com.netchar.repository.di.RepositoryModule
import com.netchar.wallpaperify.di.NetworkModule
import com.netchar.wallpaperify.di.modules.ActivityBindingModule
import com.netchar.wallpaperify.di.modules.ViewModelBindingModule
import com.netchar.wallpaperify.di.modules.GlideConfigurationModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton


@Singleton
@Component(
        modules = [
            CommonModule::class,
            PreferencesModule::class,
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

        fun build(): AppComponent
    }
}
