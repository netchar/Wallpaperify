package com.netchar.repository.di

import android.content.Context
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.api.CollectionsApi
import com.netchar.remote.api.PhotosApi
import com.netchar.repository.collection.CollectionRepository
import com.netchar.repository.collection.ICollectionRepository
import com.netchar.repository.photos.IPhotosRepository
import com.netchar.repository.photos.PhotosRepository
import com.netchar.repository.services.DownloadService
import com.netchar.repository.services.IDownloadService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepositoryModule {

    @JvmStatic
    @Provides
    @Singleton
    fun photosRepo(photosApi: PhotosApi, dispatchers: CoroutineDispatchers, downloadService: IDownloadService): IPhotosRepository = PhotosRepository(dispatchers, photosApi, downloadService)

    @JvmStatic
    @Provides
    @Singleton
    fun collectionsRepo(collectionsApi: CollectionsApi, dispatchers: CoroutineDispatchers): ICollectionRepository = CollectionRepository(collectionsApi, dispatchers)

    @JvmStatic
    @Provides
    @Singleton
    fun provideDownloadService(context: Context): IDownloadService = DownloadService(context)

}