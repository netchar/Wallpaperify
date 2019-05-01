package com.netchar.repository.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.netchar.common.di.BaseUrl
import com.netchar.remote.ApplicationJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Created by Netchar on 01.05.2019.
 * e.glushankov@gmail.com
 */
@Module
object TestNetworkModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideOkHttpClient(
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10L, TimeUnit.SECONDS)
        .writeTimeout(10L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .build()

    @JvmStatic
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(ApplicationJsonAdapterFactory.instance)
        .add(com.netchar.remote.converters.ThreeTenConverter())
        .build()

    @JvmStatic
    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient, moshi: Moshi, @BaseUrl baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

//        @JvmStatic
//        @Provides
//        @Singleton
//        @BaseUrl
//        fun provideBaseUrl(): String = url


}