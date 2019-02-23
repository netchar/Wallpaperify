package com.netchar.wallpaperify.base

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun providePreferences(app: Application): SharedPreferences = app.getSharedPreferences("store", Context.MODE_PRIVATE)
}