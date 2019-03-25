package com.netchar.wallpaperify.data.remote

import com.netchar.wallpaperify.data.services.oauth.OAuthService
import com.netchar.wallpaperify.infrastructure.BuildPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

const val AUTHORIZATION = "Authorization"

class AuthInterceptor @Inject constructor(private val oAuthService: OAuthService) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader(AUTHORIZATION, getRequestHeader())
            .build()
        return chain.proceed(request)
    }

    private fun getRequestHeader() = if (!oAuthService.isAuthorized) {
        "Bearer ${oAuthService.userApiAccessToken}"
    } else {
        "Client-ID ${BuildPreferences.getApiAccessKey()}"
    }
}