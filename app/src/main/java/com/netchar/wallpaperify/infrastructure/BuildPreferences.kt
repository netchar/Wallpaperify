package com.netchar.wallpaperify.infrastructure

import com.netchar.wallpaperify.BuildConfig

object BuildPreferences {

    fun getApiAccessKey() = if (BuildConfig.DEBUG) {
        BuildConfig.DEBUG_API_ACCESS_KEY
    } else {
        BuildConfig.RELEASE_API_ACCESS_KEY
    }

    fun getApiSecretKey() = if (BuildConfig.DEBUG) {
        BuildConfig.DEBUG_API_SECRET_KEY
    } else {
        BuildConfig.RELEASE_API_SECRET_KEY
    }
}