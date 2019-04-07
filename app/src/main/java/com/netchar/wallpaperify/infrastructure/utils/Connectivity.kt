package com.netchar.wallpaperify.infrastructure.utils

import android.content.Context
import com.netchar.wallpaperify.infrastructure.extensions.getConnectivityManager


/**
 * Created by Netchar on 07.04.2019.
 * e.glushankov@gmail.com
 */
object Connectivity {
    fun isInternetAvailable(context: Context): Boolean {
        val activeNetworkInfo = context.getConnectivityManager().activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}

