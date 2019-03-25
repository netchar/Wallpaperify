package com.netchar.wallpaperify.data.remote.api

import android.support.annotation.StringDef
import com.netchar.wallpaperify.data.models.dto.Photo
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosApi {
    companion object {
        const val LATEST = "latest"
        const val OLDEST = "oldest"
        const val POPULAR = "popular"

        @StringDef(LATEST, OLDEST, POPULAR)
        @Target(AnnotationTarget.VALUE_PARAMETER)
        @Retention(AnnotationRetention.SOURCE)
        annotation class OrderBy
    }


    @GET("photos")
    fun getPhotosAsync(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("order_by") order_by: String = ""
    ): Deferred<Response<List<Photo>>>
}