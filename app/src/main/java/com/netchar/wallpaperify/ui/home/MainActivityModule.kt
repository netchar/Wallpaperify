package com.netchar.wallpaperify.ui.home

import com.netchar.wallpaperify.ui.latest.LatestFragment
import com.netchar.wallpaperify.ui.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeLatestFragment(): LatestFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

}