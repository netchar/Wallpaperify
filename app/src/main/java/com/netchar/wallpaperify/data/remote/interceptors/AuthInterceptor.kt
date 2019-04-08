package com.netchar.wallpaperify.data.remote.interceptors

import com.netchar.wallpaperify.data.repository.OAuthRepository
import com.netchar.wallpaperify.infrastructure.BuildPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

const val AUTHORIZATION = "Authorization"

class AuthInterceptor @Inject constructor(private val oAuthRepository: OAuthRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader(AUTHORIZATION, getRequestHeader())
            .build()
        return chain.proceed(request)
    }

    private fun getRequestHeader() = if (oAuthRepository.isAuthorized()) {
        "Bearer ${oAuthRepository.getUserApiAccessTokenKey()}"
    } else {
        "Client-ID ${BuildPreferences.getApiAccessKey()}"
    }
}