package com.netchar.remote.di

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.netchar.common.di.BaseUrl
import com.netchar.common.di.OAuthInterceptor
import com.netchar.common.extensions.notExist
import com.netchar.common.utils.Memory
import com.netchar.remote.ApplicationJsonAdapterFactory
import com.netchar.remote.converters.ThreeTenConverter
import com.netchar.remote.interceptors.ConnectivityInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
object NetworkModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideOkHttpClient(
            @OAuthInterceptor authInterceptor: Interceptor,
            loggingInterceptor: HttpLoggingInterceptor,
            connectivityInterceptor: ConnectivityInterceptor,
            httpCache: Cache
    ): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10L, TimeUnit.SECONDS)
            .writeTimeout(10L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(connectivityInterceptor)
            .cache(httpCache)
            .build()


    @JvmStatic
    @Provides
    @Singleton
    fun provideConnectivityInterceptor(context: Context): ConnectivityInterceptor = ConnectivityInterceptor(context)

    @JvmStatic
    @Provides
    @Singleton
    fun provideCache(context: Context, file: File): Cache = Cache(file, Memory.calculateCacheSize(context, .15f))

    @JvmStatic
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @JvmStatic
    @Provides
    @Singleton
    fun provideCacheFile(context: Context): File = File(context.filesDir, "okHttpCache").also {
        if (it.notExist()) {
            it.mkdirs()
        }
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
            .add(ApplicationJsonAdapterFactory)
            .add(ThreeTenConverter())
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

    @JvmStatic
    @Provides
    @Singleton
    @BaseUrl
    fun provideBaseUrl(): String = "https://api.unsplash.com/"
}

