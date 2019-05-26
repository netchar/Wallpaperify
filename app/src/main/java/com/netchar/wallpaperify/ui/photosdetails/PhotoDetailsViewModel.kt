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
import com.netchar.common.utils.Event
import com.netchar.remote.enums.Cause
import com.netchar.repository.photos.IPhotosRepository
import com.netchar.repository.pojo.Message
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.repository.pojo.Progress
import com.netchar.repository.pojo.Resource
import com.netchar.wallpaperify.R
import javax.inject.Inject


class PhotoDetailsViewModel @Inject constructor(
        coroutineDispatchers: CoroutineDispatchers,
        private val repo: IPhotosRepository
) : BaseViewModel(coroutineDispatchers) {

    private val _photoId = MutableLiveData<String>()
    private val _photo = MediatorLiveData<PhotoPOJO>()
    private val _loading = MutableLiveData<Boolean>()
    private val _error = MutableLiveData<Message>()
    private val _downloading = MediatorLiveData<Boolean>()
    private val _downloadProgress = MutableLiveData<Float>()
    private val _downloadRequest = MutableLiveData<Event<PhotoPOJO>>()

    private val repoLiveData = Transformations.switchMap(_photoId) { id ->
        repo.getPhoto(id, scope).getLiveData()
    }


    private val downloadProgressLiveData = Transformations.switchMap(_downloadRequest) { photo ->
        repo.download(photo.peekContent())
    }

    init {
        _photo.addSource(repoLiveData) { response ->
            proceedResponse(response)
        }

        _downloading.addSource(downloadProgressLiveData) { progress ->
            proceedProgress(progress)
        }
    }

    val photo: LiveData<PhotoPOJO> get() = _photo

    val error: LiveData<Message> get() = _error

    val loading: LiveData<Boolean> get() = _loading

    val downloading: LiveData<Boolean> get() = _downloading

    val downloadProgress: LiveData<Float> get() = _downloadProgress

    fun fetchPhoto(id: String) {
        _photoId.value = id
    }

    fun downloadImage() {
        _photo.value?.let {
            _downloadRequest.value = Event(it)
            _downloading.value = true
        }
    }

    fun cancelDownloading() {
        _downloading.value = false
        repo.cancelDownload()
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

    private fun proceedProgress(progress: Progress) {
        when (progress) {
            is Progress.Success -> {
                _downloading.value = false
            }
            is Progress.Error -> {
                _downloading.value = false
                _error.value = when (progress.cause) {
                    Progress.ErrorCause.UNKNOWN -> Message(R.string.message_error_unknown)
                    Progress.ErrorCause.STATUS_FAILED -> Message(R.string.message_error_download_failed)
                    Progress.ErrorCause.STATUS_PAUSED -> Message(R.string.message_error_download_failed)
                }
            }
            is Progress.Downloading -> {
                _downloadProgress.value = progress.progressSoFar
            }
        }
    }

    private fun getErrorMessage(response: Resource.Error): Message {
        return when (response.cause) {
            Cause.NO_INTERNET_CONNECTION -> Message(R.string.error_message_no_internet)
            Cause.NOT_AUTHENTICATED, Cause.UNEXPECTED -> Message(R.string.error_message_try_again_later)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repo.unregisterDownloadObservers()
    }
}