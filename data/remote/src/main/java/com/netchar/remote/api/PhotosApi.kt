package com.netchar.remote.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PhotosApi {

    @GET("photos")
    fun getPhotosAsync(
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("order_by") order_by: String = ""
    ): Deferred<Response<List<com.netchar.models.Photo>>>
}