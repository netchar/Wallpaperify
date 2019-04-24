package com.netchar.wallpaperify.di.modules

import com.netchar.wallpaperify.ui.about.AboutFragment
import com.netchar.wallpaperify.ui.home.HomeFragment
import com.netchar.wallpaperify.ui.latest.LatestFragment
import com.netchar.wallpaperify.ui.photosdetails.PhotoDetailsFragment
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

    @ContributesAndroidInjector
    abstract fun contributePhotoDetailsFragment(): PhotoDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutFragment(): AboutFragment

}