package com.netchar.wallpaperify.data.services.oauth

import android.content.SharedPreferences
import com.netchar.wallpaperify.di.AuthPrefs
import javax.inject.Inject

class OAuthService @Inject constructor(
    val oauthPrefs: SharedPreferences
) {

    var isAuthorized = false
        private set

    var userApiAccessToken: String = ""
        private set
}