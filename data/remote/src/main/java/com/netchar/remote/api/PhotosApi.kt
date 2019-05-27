/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.remote.api

import com.netchar.remote.dto.Photo
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotosApi {

    @GET("photos")
    fun getPhotosAsync(
            @Query("page") page: Int,
            @Query("per_page") per_page: Int,
            @Query("order_by") order_by: String = ""
    ): Deferred<Response<List<Photo>>>

    @GET("photos/{id}")
    fun getPhotoAsync(@Path("id") id: String) : Deferred<Response<Photo>>
}