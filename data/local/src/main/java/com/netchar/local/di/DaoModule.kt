package com.netchar.local.di

import com.netchar.local.dao.PhotoDao
import com.netchar.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [LocalModule::class])
object DaoModule {

    @Provides
    @JvmStatic
    @Singleton
    fun providePhotoDao(database: AppDatabase): PhotoDao = database.photoDao()
}