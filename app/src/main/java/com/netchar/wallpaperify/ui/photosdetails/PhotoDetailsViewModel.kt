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

package com.netchar.wallpaperify.ui.photosdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.repository.photos.IPhotosRepository
import com.netchar.repository.pojo.Message
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.repository.services.DownloadService
import com.netchar.wallpaperify.R
import timber.log.Timber
import javax.inject.Inject


class PhotoDetailsViewModel @Inject constructor(
        coroutineDispatchers: CoroutineDispatchers,
        private val repo: IPhotosRepository,
        private val downloadService: DownloadService

) : BaseViewModel(coroutineDispatchers) {

    private val _photoId = MutableLiveData<String>()
    private val _photo = MediatorLiveData<PhotoPOJO>()
    private val _loading = MutableLiveData<Boolean>()
    private val _error = MutableLiveData<Message>()
    private var _downloading = MutableLiveData<Boolean>()

    private val repoLiveData: LiveData<Resource<PhotoPOJO>> = Transformations.switchMap(_photoId) { id ->
        repo.getPhoto(id, scope).getLiveData()
    }

    init {
        _photo.addSource(repoLiveData) { response ->
            proceedResponse(response)
        }
    }

    private fun proceedResponse(response: Resource<PhotoPOJO>) {

        when (response) {
            is Resource.Success -> {
                _photo.value = response.data
            }
            is Resource.Loading -> {
                _loading.value = response.isLoading
            }
            is Resource.Error -> {
                _error.value = getErrorMessage(response)
            }
        }
    }

    fun fetchPhoto(id: String) {
        _photoId.value = id
    }

    val photo: LiveData<PhotoPOJO> get() = _photo

    val error: LiveData<Message> get() = _error

    val loading: LiveData<Boolean> get() = _loading

    val downloading: LiveData<Boolean> get() = _downloading

    // todo: rid up from this
    var progress = Transformations.map(downloadService.progress) {
        it
    }

    private fun getErrorMessage(response: Resource.Error): Message {
        return when (response.cause) {
            Cause.NO_INTERNET_CONNECTION -> Message(R.string.error_message_no_internet)
            Cause.NOT_AUTHENTICATED, Cause.UNEXPECTED -> Message(R.string.error_message_try_again_later)
        }
    }

    fun downloadImage() {
        _photo.value?.let { photo ->
            try {
                val request = DownloadService.DownloadRequest(
                        url = photo.urls.raw,
                        fileName = photo.id,
                        fileQuality = "full",
                        fileExtension = "jpg",
                        requestType = DownloadService.DownloadRequest.REQUEST_DOWNLOAD
                )
                downloadService.download(request) {
                    _downloading.value = false
                }

                _downloading.value = true

            } catch (ex: IllegalStateException) {
                _error.value = Message(R.string.error_download_unable_to_start_downloading)
                Timber.e(ex)
            }
        }
    }

    fun cancelDownloading() {
        downloadService.cancel()
    }
}