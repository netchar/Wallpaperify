package com.netchar.wallpaperify.di

import android.content.Context
import com.netchar.wallpaperify.infrastructure.glide.GlideConfigurationModule
import com.netchar.wallpaperify.ui.App
import com.netchar.wallpaperify.ui.base.BaseActivity
import com.netchar.wallpaperify.ui.base.BaseFragment
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection


/**
 * Created by Netchar on 31.03.2019.
 * e.glushankov@gmail.com
 */
object Injector {

    fun inject(fragment: BaseFragment) {
        AndroidSupportInjection.inject(fragment)
    }

    fun inject(activity: BaseActivity) {
        AndroidInjection.inject(activity)
    }

    fun inject(glideModule: GlideConfigurationModule) {
        App.get().component.inject(glideModule)
    }
}