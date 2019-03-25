package com.netchar.wallpaperify.di.modules

import android.content.SharedPreferences
import com.netchar.wallpaperify.data.services.oauth.OAuthService
import com.netchar.wallpaperify.di.AuthPrefs
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by Netchar on 21.03.2019.
 * e.glushankov@gmail.com
 */

@Module
object OAuthServiceModule {

    @JvmStatic
    @Provides
    @Singleton
    fun bindOauthService(@AuthPrefs preferences: SharedPreferences): OAuthService = OAuthService(preferences)
}