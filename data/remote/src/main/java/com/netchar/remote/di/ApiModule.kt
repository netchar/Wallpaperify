package com.netchar.remote.di

import com.netchar.remote.api.PhotosApi
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

}