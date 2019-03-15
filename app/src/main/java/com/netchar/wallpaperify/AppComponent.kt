package com.netchar.wallpaperify

import com.netchar.wallpaperify.di.modules.*
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
    abstract class Builder : AndroidInjector.Builder<App>()
}