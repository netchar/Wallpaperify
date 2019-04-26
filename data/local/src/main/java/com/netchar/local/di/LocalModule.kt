package com.netchar.local.di

import android.content.Context
import androidx.room.Room
import com.netchar.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object LocalModule {

    @Singleton
    @JvmStatic
    @Provides
    fun provideDatabase(context: Context): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "wallpeperify").build()

}