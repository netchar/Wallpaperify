package com.netchar.repository.di

import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.api.PhotosApi
import com.netchar.repository.photos.IPhotosRepository
import com.netchar.repository.photos.PhotosRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepositoryModule {

    @JvmStatic
    @Provides
    @Singleton
    fun photosRepo(api: PhotosApi, dispatchers: CoroutineDispatchers): IPhotosRepository = PhotosRepository(api, dispatchers)
}