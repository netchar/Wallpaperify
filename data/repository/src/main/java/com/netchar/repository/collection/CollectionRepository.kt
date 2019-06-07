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

package com.netchar.repository.collection

import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.api.CollectionsApi
import com.netchar.remote.apirequest.ApiRequest
import com.netchar.remote.dto.Collection
import com.netchar.remote.dto.Photo
import com.netchar.repository.IBoundResource
import com.netchar.repository.NetworkBoundResource
import com.netchar.repository.pojo.CollectionPOJO
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.repository.utils.Mapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import retrofit2.Response
import javax.inject.Inject


class CollectionRepository @Inject constructor(
        private val collectionsApi: CollectionsApi,
        private val coroutineDispatchers: CoroutineDispatchers
) : ICollectionRepository {

    override fun getCollections(request: ApiRequest.Collections, scope: CoroutineScope): IBoundResource<List<CollectionPOJO>> {

        return object : NetworkBoundResource<List<CollectionPOJO>, List<Collection>>(coroutineDispatchers) {

            override fun mapToPOJO(data: List<Collection>): List<CollectionPOJO> {
                return data.map { Mapper.map(it) }
            }

            override fun getApiCallAsync(): Deferred<Response<List<Collection>>> {
                return collectionsApi.getCollectionsAsync(request.page, request.perPage)
            }
        }.launchIn(scope)
    }

    override fun getCollectionPhotos(request: ApiRequest.Collection, scope: CoroutineScope): IBoundResource<List<PhotoPOJO>> {
        return object : NetworkBoundResource<List<PhotoPOJO>, List<Photo>>(coroutineDispatchers) {

            override fun mapToPOJO(data: List<Photo>): List<PhotoPOJO> {
                return data.map { Mapper.map(it) }
            }

            override fun getApiCallAsync(): Deferred<Response<List<Photo>>> {
                return collectionsApi.getCollectionPhotosAsync(request.id, request.page, request.perPage)
            }
        }.launchIn(scope)
    }
}