package com.netchar.wallpaperify.di.modules

import com.netchar.common.di.ActivityScope
import com.netchar.wallpaperify.ui.home.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Module(
        includes = [AndroidSupportInjectionModule::class]
)
abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun contributeMainActivity(): MainActivity
}
