package com.netchar.wallpaperify.di.modules

import android.content.SharedPreferences
import com.netchar.wallpaperify.data.api.PhotosApi
import com.netchar.wallpaperify.data.services.oauth.OAuthService
import com.netchar.wallpaperify.di.AuthPrefs
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
abstract class ApiModule {
    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun providesPhotoApi(retrofit: Retrofit): PhotosApi = retrofit.create(PhotosApi::class.java)

        @JvmStatic
        @Provides
        @Singleton
        fun bindOauthService(@AuthPrefs preferences: SharedPreferences) = OAuthService(preferences)
    }
}