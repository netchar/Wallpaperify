package com.netchar.wallpaperify.di.modules

import com.netchar.wallpaperify.data.remote.api.PhotosApi
import com.netchar.wallpaperify.data.repository.IPhotosRepository
import com.netchar.wallpaperify.data.repository.PhotosRepository
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object ApiModule {

    @JvmStatic
    @Provides
    @Singleton
    fun providesPhotoApi(retrofit: Retrofit): PhotosApi = retrofit.create(PhotosApi::class.java)

    @JvmStatic
    @Provides
    @Singleton
    fun photosRepo(api: PhotosApi, dispatchers: CoroutineDispatchers): IPhotosRepository = PhotosRepository(api, dispatchers)
}