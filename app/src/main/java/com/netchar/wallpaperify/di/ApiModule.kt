package com.netchar.wallpaperify.di

import android.content.Context
import com.netchar.wallpaperify.data.repository.IPhotosRepository
import com.netchar.wallpaperify.data.repository.PhotosRepository
import com.netchar.common.utils.CoroutineDispatchers
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object ApiModule {

    @JvmStatic
    @Provides
    @Singleton
    fun providesPhotoApi(retrofit: Retrofit): com.netchar.remote.api.PhotosApi = retrofit.create(com.netchar.remote.api.PhotosApi::class.java)

    @JvmStatic
    @Provides
    @Singleton
    fun photosRepo(context: Context, api: com.netchar.remote.api.PhotosApi, dispatchers: CoroutineDispatchers): IPhotosRepository = PhotosRepository(api, dispatchers, context)
}