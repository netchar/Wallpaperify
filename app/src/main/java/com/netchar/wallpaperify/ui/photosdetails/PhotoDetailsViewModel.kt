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
import com.netchar.common.services.IExternalAppService
import com.netchar.common.services.IWallpaperApplierService
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.SingleLiveData
import com.netchar.remote.enums.Cause
import com.netchar.repository.pojo.Message
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.repository.pojo.Progress
import com.netchar.repository.pojo.Resource
import com.netchar.repository.services.DownloadRequest
import com.netchar.repository.usecase.IPhotoUseCase
import com.netchar.wallpaperify.R
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DialogState(val show: Boolean, val closeReason: Int = 0) {
    companion object Reason {
        private const val CLOSE_REASON_COMPLETED = -1
        private const val CLOSE_REASON_CANCELED = -2

        fun show() = DialogState(true)
        fun hide() = DialogState(false, CLOSE_REASON_COMPLETED)
        fun cancel() = DialogState(false, CLOSE_REASON_CANCELED)
    }

    val isShown get() = show
    val isCanceled get() = closeReason == CLOSE_REASON_CANCELED
}

class PhotoDetailsViewModel @Inject constructor(
        coroutineDispatchers: CoroutineDispatchers,
        private val wallpaperService: IWallpaperApplierService,
        private val externalAppService: IExternalAppService,
        private val useCase: IPhotoUseCase
) : BaseViewModel(coroutineDispatchers) {
    private val _photoId = MutableLiveData<String>()
    private val _photo = MediatorLiveData<PhotoPOJO>()
    private val _loading = MutableLiveData<Boolean>()
    private val _error = SingleLiveData<Message>()
    private val _downloadDialog = MediatorLiveData<DialogState>()
    private val _downloadProgress = MutableLiveData<Float>()
    private val _toast = SingleLiveData<Message>()
    private val _overrideDialog = SingleLiveData<DialogState>()

    private val repoLiveData: LiveData<Resource<PhotoPOJO>> = Transformations.switchMap(_photoId) { id ->
        useCase.getPhoto(id, this).getLiveData()
    }

    init {
        _photo.observe(repoLiveData) { response ->
            proceedResponse(response)
        }
    }

    val photo: LiveData<PhotoPOJO> get() = _photo

    val error: LiveData<Message> get() = _error

    val loading: LiveData<Boolean> get() = _loading

    val downloadDialog: LiveData<DialogState> get() = _downloadDialog

    val overrideDialog: LiveData<DialogState> get() = _overrideDialog

    val downloadProgress: LiveData<Float> get() = _downloadProgress

    val toast: LiveData<Message> get() = _toast

    fun fetchPhoto(id: String) {
        _photoId.value = id
    }

    fun downloadImage(forceOverride: Boolean = false) {
        val photo = _photo.value
        if (photo == null) {
            _error.value = Message(R.string.error_message_photo_details_not_loaded)
        } else {
            // todo: possibility to change file quality to download
            val request = DownloadRequest(
                    url = photo.urls.raw,
                    fileName = photo.id,
                    fileQuality = "raw",
                    fileExtension = "jpg",
                    requestType = DownloadRequest.REQUEST_DOWNLOAD,
                    forceOverride = forceOverride
            )

            download(photo, request)
        }
    }

    fun overrideDownloadedPhoto() {
        _overrideDialog.value = DialogState.hide()
        downloadImage(true)
    }

    fun downloadWallpaper() {
        val photo = _photo.value
        if (photo == null) {
            _error.value = Message(R.string.error_message_photo_details_not_loaded)
        } else {
            val request = DownloadRequest(
                    url = photo.urls.raw,
                    fileName = photo.id,
                    fileQuality = "raw",
                    fileExtension = "jpg",
                    requestType = DownloadRequest.REQUEST_WALLPAPER
            )
            download(photo, request)
        }
    }

    fun cancelDownloading() {
        useCase.cancelDownload()
    }

    fun openAuthorProfile(url: String) {
        externalAppService.openWebPage(url)
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

    private fun proceedProgress(progress: Progress, request: Int) {
        when (progress) {
            is Progress.Success -> {
                when (request) {
                    DownloadRequest.REQUEST_DOWNLOAD -> {
                        _downloadDialog.value = DialogState.hide()
                        _toast.value = Message(R.string.message_download_success)
                    }
                    DownloadRequest.REQUEST_WALLPAPER -> {
                        _downloadDialog.value = DialogState.hide()
                        wallpaperService.setWallpaper(progress.fileUri)
                    }
                }
            }
            is Progress.Downloading -> {
                val currentValue = _downloadDialog.value
                if (currentValue == null || !currentValue.isShown || currentValue.isCanceled) {
                    _downloadDialog.value = DialogState.show()
                }

                _downloadProgress.value = progress.progressSoFar
            }
            is Progress.Error -> {
                _downloadDialog.value = DialogState.hide()

                when (progress.cause) {
                    Progress.ErrorCause.UNKNOWN -> _error.value = Message(R.string.error_message_unknown)
                    Progress.ErrorCause.INSUFFICIENT_SPACE -> _error.value = Message(R.string.error_message_download_not_enough_space)
                    else -> _error.value = Message(R.string.error_message_download_failed)
                }
            }
            is Progress.FileExist -> {
                when (request) {
                    DownloadRequest.REQUEST_DOWNLOAD -> {
                        _overrideDialog.value = DialogState.show()
                    }
                    DownloadRequest.REQUEST_WALLPAPER -> {
                        wallpaperService.setWallpaper(progress.fileUri)
                    }
                }
            }
            is Progress.Canceled -> {
                _downloadDialog.value = DialogState.cancel()
                _toast.value = Message(R.string.message_canceled)
            }
        }
    }

    private fun download(photo: PhotoPOJO, request: DownloadRequest) {
        launch {
            val responseLiveData = useCase.downloadAsync(photo.id, request)
            _downloadDialog.observe(responseLiveData) { progress -> proceedProgress(progress, request.requestType) }
        }
    }

    private fun getErrorMessage(response: Resource.Error): Message {
        return when (response.cause) {
            Cause.NO_INTERNET_CONNECTION -> Message(R.string.message_error_no_internet)
            Cause.NOT_AUTHENTICATED, Cause.UNEXPECTED -> Message(R.string.error_message_unexpected_server_response)
        }
    }

    override fun onCleared() {
        super.onCleared()
        useCase.unregisterDownloadObservers()
    }
}