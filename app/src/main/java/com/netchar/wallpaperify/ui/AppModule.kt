package com.netchar.wallpaperify.ui

import android.content.Context
import android.content.SharedPreferences
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import com.netchar.wallpaperify.di.AppPrefs
import com.netchar.wallpaperify.di.AuthPrefs
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

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

    @JvmStatic
    @Provides
    @Singleton
    fun provideDispatchers(): CoroutineDispatchers = CoroutineDispatchers()
}

