package com.netchar.remote.api

import com.netchar.models.Collection
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Netchar on 09.05.2019.
 * e.glushankov@gmail.com
 */

interface CollectionsApi {

    @GET("collections")
    fun getCollectionsAsync(
            @Query("page") page: Int,
            @Query("per_page") per_page: Int
    ): Deferred<Response<List<Collection>>>
}