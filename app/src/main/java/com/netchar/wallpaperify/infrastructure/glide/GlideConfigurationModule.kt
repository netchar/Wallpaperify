package com.netchar.wallpaperify.infrastructure.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.MemoryCategory
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.netchar.wallpaperify.di.Injector
import com.netchar.wallpaperify.infrastructure.utils.PerformanceChecker
import okhttp3.OkHttpClient
import java.io.InputStream
import javax.inject.Inject

/**
 * Created by Netchar on 31.03.2019.
 * e.glushankov@gmail.com
 */

@GlideModule
class GlideConfigurationModule : AppGlideModule() {

    init {
        Injector.inject(this)
    }

    @Inject
    lateinit var performanceChecker: PerformanceChecker

    @Inject
    lateinit var httpClient: OkHttpClient

    override fun isManifestParsingEnabled() = false

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val requestOptions = RequestOptions()
        if (performanceChecker.isHighPerformingDevice) {
            builder.setDefaultRequestOptions(requestOptions.format(DecodeFormat.PREFER_ARGB_8888))
        } else {
            builder.setDefaultRequestOptions(requestOptions.format(DecodeFormat.PREFER_RGB_565))
        }

        super.applyOptions(context, builder)
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(httpClient))

        if (performanceChecker.isHighPerformingDevice) {
            glide.setMemoryCategory(MemoryCategory.NORMAL)
        } else {
            glide.setMemoryCategory(MemoryCategory.LOW)
        }

        super.registerComponents(context, glide, registry)
    }
}
