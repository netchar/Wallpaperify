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

package com.netchar.repository.photos

import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.api.PhotosApi
import com.netchar.remote.apirequest.ApiRequest
import com.netchar.remote.dto.Photo
import com.netchar.repository.IBoundResource
import com.netchar.repository.NetworkBoundResource
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.repository.utils.Mapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import retrofit2.Response
import javax.inject.Inject

class PhotosRepository @Inject constructor(
        private val api: PhotosApi,
        private val dispatchers: CoroutineDispatchers
) : IPhotosRepository {

    override fun getPhotos(request: ApiRequest.Photos, scope: CoroutineScope): IBoundResource<List<PhotoPOJO>> {
        return object : NetworkBoundResource<List<PhotoPOJO>, List<Photo>>(dispatchers) {
            override fun mapToPOJO(data: List<Photo>): List<PhotoPOJO> {
                return data.map { Mapper.map(it) }
            }

            override fun getApiCallAsync(): Deferred<Response<List<Photo>>> {
                return api.getPhotosAsync(request.page, request.perPage, request.order.value)
            }
        }.launchIn(scope)
    }

    override fun getPhoto(id: String, scope: CoroutineScope): IBoundResource<PhotoPOJO> {
        return object : NetworkBoundResource<PhotoPOJO, Photo>(dispatchers) {
            override fun mapToPOJO(data: Photo): PhotoPOJO {
                return Mapper.map(data)
            }

            override fun getApiCallAsync(): Deferred<Response<Photo>> {
                return api.getPhotoAsync(id)
            }
        }.launchIn(scope)
    }

//    override fun downloadPhoto(url: String, scope: CoroutineScope): ByteArray {

//        val workerParameters = WorkerParameters("id")
//        return object : CoroutineWorker(context, workerParameters) {
//
//            override suspend fun doWork(): Result {
//                val byteArray = withContext(scope.coroutineContext) {
//
//                }
//

//            }
//        }
//    }
}



