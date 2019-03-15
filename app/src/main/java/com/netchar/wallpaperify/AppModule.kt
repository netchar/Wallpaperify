package com.netchar.wallpaperify

import android.content.Context
import android.content.SharedPreferences
import com.netchar.wallpaperify.App
import com.netchar.wallpaperify.di.AppPrefs
import com.netchar.wallpaperify.di.AuthPrefs
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AppModule {
    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        @AppPrefs
        fun providePreferences(app: Context): SharedPreferences = app.getSharedPreferences("store", Context.MODE_PRIVATE)

        @JvmStatic
        @Provides
        @Singleton
        @AuthPrefs
        fun provideAuthPrefs(app: Context): SharedPreferences = app.getSharedPreferences("OAuth", Context.MODE_PRIVATE)

    }


    @Binds
    @Singleton
    abstract fun provideContext(app: App): Context
}