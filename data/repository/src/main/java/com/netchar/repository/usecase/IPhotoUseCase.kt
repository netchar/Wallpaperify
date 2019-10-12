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

package com.netchar.repository.usecase

import androidx.lifecycle.LiveData
import com.netchar.remote.apirequest.ApiRequest
import com.netchar.repository.IBoundResource
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.repository.pojo.Progress
import com.netchar.repository.services.DownloadRequest
import kotlinx.coroutines.CoroutineScope

interface IPhotoUseCase {
    fun getPhotos(request: ApiRequest.Photos, scope: CoroutineScope): IBoundResource<List<PhotoPOJO>>

    fun getPhoto(id: String, scope: CoroutineScope): IBoundResource<PhotoPOJO>

    suspend fun downloadAsync(photoId: String, request: DownloadRequest): LiveData<Progress>

    fun cancelDownload()
}