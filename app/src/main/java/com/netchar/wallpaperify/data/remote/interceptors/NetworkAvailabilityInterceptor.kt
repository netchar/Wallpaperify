package com.netchar.wallpaperify.data.remote.interceptors

import com.netchar.wallpaperify.infrastructure.exceptions.NoNetworkException
import com.netchar.wallpaperify.infrastructure.utils.Connectivity
import com.netchar.wallpaperify.ui.App
import okhttp3.Interceptor
import okhttp3.Response


/**
 * Created by Netchar on 07.04.2019.
 * e.glushankov@gmail.com
 */
class NetworkAvailabilityInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (Connectivity.isInternetAvailable(App.get())) {
            chain.proceed(chain.request())
        } else {
            throw NoNetworkException("No internet connections")
        }
    }
}
