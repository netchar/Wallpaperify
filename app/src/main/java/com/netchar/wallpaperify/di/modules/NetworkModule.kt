package com.netchar.wallpaperify.di.modules

import android.content.Context
import com.netchar.wallpaperify.data.converters.ThreeTenConverter
import com.netchar.wallpaperify.data.services.oauth.AuthInterceptor
import com.netchar.wallpaperify.di.factories.ApplicationJsonAdapterFactory
import com.netchar.wallpaperify.utils.Memory
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val BASE_URL = "https://api.unsplash.com/"

@Module
abstract class NetworkModule {
    @Module
    companion object {

        @JvmStatic
        @Provides
        @Singleton
        fun provideOkHttpClient(context: Context, authInterceptor: AuthInterceptor): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10L, TimeUnit.SECONDS)
            .writeTimeout(10L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .cache(Cache(File(context.cacheDir, "okHttpCache"), Memory.calculateCacheSize(context, .15f)))
            .build()

        @JvmStatic
        @Provides
        @Singleton
        fun provideMoshi(): Moshi = Moshi.Builder()
            .add(ApplicationJsonAdapterFactory.instance)
            .add(ThreeTenConverter.instance)
            .build()

        @JvmStatic
        @Provides
        @Singleton
        fun provideRetrofit(httpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Binds
    @Singleton
    abstract fun provideInterceptor(interceptor: AuthInterceptor) : Interceptor
}

