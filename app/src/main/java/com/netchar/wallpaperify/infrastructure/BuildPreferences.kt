package com.netchar.wallpaperify.infrastructure

import android.content.Context
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