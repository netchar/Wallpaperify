package com.netchar.wallpaperify.ui

import android.content.Context
import com.netchar.wallpaperify.di.modules.ActivityBindingModule
import com.netchar.wallpaperify.di.modules.ApiModule
import com.netchar.wallpaperify.di.modules.NetworkModule
import com.netchar.wallpaperify.di.modules.ViewModelBindingModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        ApiModule::class,
        ActivityBindingModule::class,
        ViewModelBindingModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<App>(){
        @BindsInstance
        abstract fun context(context: Context): Builder
    }
}