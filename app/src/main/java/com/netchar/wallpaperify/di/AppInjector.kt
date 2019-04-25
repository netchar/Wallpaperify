package com.netchar.wallpaperify.di

import com.netchar.common.utils.Injector
import com.netchar.wallpaperify.di.modules.GlideConfigurationModule
import com.netchar.wallpaperify.ui.App

class AppInjector : Injector() {
    companion object {
        fun inject(glideModule: GlideConfigurationModule) {
            App.get().component.inject(glideModule)
        }
    }
}