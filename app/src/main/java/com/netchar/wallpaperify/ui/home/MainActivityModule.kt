package com.netchar.wallpaperify.ui.home

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment


}