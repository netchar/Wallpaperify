package com.netchar.common.utils

import android.content.Context
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.netchar.common.extensions.getConnectivityManager

enum class ConnectivityState {
    CELLULAR,
    WIFI,
    VPN,
    NONE
}

object Connectivity {

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.M)
    fun isInternetAvailable(context: Context): Boolean {
        val networkState = getNetworkState(context)
        return networkState != ConnectivityState.NONE
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.M)
    fun getNetworkState(context: Context): ConnectivityState {
        val connectivityManager = context.getConnectivityManager()
        val activeNetwork = connectivityManager.activeNetwork ?: return ConnectivityState.NONE
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return ConnectivityState.NONE
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectivityState.CELLULAR
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectivityState.WIFI
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> ConnectivityState.VPN
            else -> ConnectivityState.NONE
        }
    }
}

