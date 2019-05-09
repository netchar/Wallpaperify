package com.netchar.repository.di

import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.api.CollectionsApi
import com.netchar.remote.api.PhotosApi
import com.netchar.repository.collection.CollectionRepository
import com.netchar.repository.collection.ICollectionRepository
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

    @JvmStatic
    @Provides
    @Singleton
    fun collectionsRepo(api: CollectionsApi, dispatchers: CoroutineDispatchers): ICollectionRepository = CollectionRepository(api, dispatchers)
}