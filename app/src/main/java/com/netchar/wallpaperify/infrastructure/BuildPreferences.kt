package com.netchar.wallpaperify.infrastructure

import com.netchar.common.utils.IBuildPreferences
import com.netchar.wallpaperify.BuildConfig

class BuildPreferences : IBuildPreferences {

    override fun getApiAccessKey() = if (BuildConfig.DEBUG) {
        BuildConfig.DEBUG_API_ACCESS_KEY
    } else {
        BuildConfig.RELEASE_API_ACCESS_KEY
    }

    override fun getApiSecretKey() = if (BuildConfig.DEBUG) {
        BuildConfig.DEBUG_API_SECRET_KEY
    } else {
        BuildConfig.RELEASE_API_SECRET_KEY
    }

    // todo: move to SystemUtils.kt
//    fun Context.getVersionCode(): Int {
//        return try {
//            val manager = packageManager
//            val info = manager.getPackageInfo(packageName, 0)
//            info.versionCode
//        } catch (e: Exception) {
//            e.printStackTrace()
//            -1
//        }
//    }
//
//    fun Context.getVersionName(): String? {
//        return try {
//            val manager = packageManager
//            val info = manager.getPackageInfo(packageName, 0)
//            "${info.versionName} Build ${info.versionCode}"
//        } catch (e: Exception) {
//            null
//        }
//    }
}