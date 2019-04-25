package com.netchar.repository.di

import android.content.Context
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.api.PhotosApi
import com.netchar.remote.di.ApiModule
import com.netchar.repository.IPhotosRepository
import com.netchar.repository.PhotosRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ApiModule::class])
object RepositoryModule {

    @JvmStatic
    @Provides
    @Singleton
    fun photosRepo(context: Context, api: PhotosApi, dispatchers: CoroutineDispatchers): IPhotosRepository = PhotosRepository(api, dispatchers, context)
}