package com.netchar.wallpaperify.di.modules

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.netchar.wallpaperify.data.remote.converters.ThreeTenConverter
import com.netchar.wallpaperify.data.remote.AuthInterceptor
import com.netchar.wallpaperify.data.repository.OAuthRepository
import com.netchar.wallpaperify.di.factories.ApplicationJsonAdapterFactory
import com.netchar.wallpaperify.infrastructure.extensions.notExist
import com.netchar.wallpaperify.infrastructure.utils.Memory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val BASE_URL = "https://api.unsplash.com/"

@Module(includes = [OAuthModule::class])
object NetworkModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor, loggingInterceptor: HttpLoggingInterceptor, cache: Cache): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10L, TimeUnit.SECONDS)
        .writeTimeout(10L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .cache(cache)
        .build()

    @JvmStatic
    @Provides
    @Singleton
    fun provideCache(context: Context, file: File): Cache = Cache(file, Memory.calculateCacheSize(context, .15f))

    @JvmStatic
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor { Timber.d(it) }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideOAuthInterceptor(oAuthRepository: OAuthRepository): AuthInterceptor = AuthInterceptor(oAuthRepository)

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
        .add(ApplicationJsonAdapterFactory.instance)
        .add(ThreeTenConverter())
        .build()

    @JvmStatic
    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
}

