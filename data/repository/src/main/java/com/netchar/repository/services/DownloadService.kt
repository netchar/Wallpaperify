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

package com.netchar.repository.services

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netchar.common.utils.IBuild
import com.netchar.repository.pojo.Progress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class DownloadService @Inject constructor(
        private val context: Context,
        private val build: IBuild
) : IDownloadService, CoroutineScope {
    private val downloads = hashMapOf<DownloadRequest, Downloading>()
    private lateinit var downloadManager: DownloadManager
    private lateinit var progress: MutableLiveData<Progress>
    private lateinit var currentRequest: DownloadRequest
    private val supervisorJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() {
            return supervisorJob + Dispatchers.IO
        }

    @Throws(IllegalStateException::class)
    override fun download(request: DownloadRequest): LiveData<Progress> {
        try {
            downloadManager = context.getSystemService() ?: throw IllegalStateException("Unable to get DownloadManager")
            currentRequest = request

            val file = request.toFile()

            progress = when (request.requestType) {
                DownloadRequest.REQUEST_DOWNLOAD -> downloadFile(downloadManager, request, file)
                DownloadRequest.REQUEST_WALLPAPER -> downloadWallpaper(downloadManager, request, file)
                else -> throw IllegalArgumentException("Wrong DownloadRequest type")
            }
        } catch (ex: IllegalArgumentException) {
            Timber.e(ex)
            progress.postValue(Progress.Error(Progress.ErrorCause.UNKNOWN))
        } catch (ex: IllegalStateException) {
            Timber.e(ex)
            progress.postValue(Progress.Error(Progress.ErrorCause.UNKNOWN))
        }

        return progress
    }


    private fun downloadWallpaper(dm: DownloadManager, request: DownloadRequest, file: File): MutableLiveData<Progress> {
        val progress = MutableLiveData<Progress>()

        if (file.exists()) {
            val uri = FileProvider.getUriForFile(context, "${build.getApplicationId()}.fileprovider", file)
            return progress.apply { value = Progress.FileExist(uri) }
        }

        val currentDownloadId = downloadImpl(
                downloadManager = dm,
                title = request.fullFileName,
                url = request.url,
                fileName = request.fullFileName,
                notificationMode = DownloadManager.Request.VISIBILITY_VISIBLE
        )

        val response = registerDownloading(request, progress, currentDownloadId)
        response.progressDispatcher.dispatch(currentDownloadId, this)

        return progress
    }

    private fun registerDownloading(request: DownloadRequest, progress: MutableLiveData<Progress>, currentDownloadId: Long): Downloading {
        val response = Downloading(currentDownloadId, ProgressUpdateDispatcher(request, downloadManager, context, build) {
            progress.postValue(it)
        })
        downloads[request] = response
        return response
    }

    private fun downloadFile(dm: DownloadManager, request: DownloadRequest, file: File): MutableLiveData<Progress> {
        val progress = MutableLiveData<Progress>()

        if (file.exists()) {
            if (request.forceOverride) {
                deleteSafe(file)
            } else {
                val uri = FileProvider.getUriForFile(context, "${build.getApplicationId()}.fileprovider", file)
                return progress.apply { value = Progress.FileExist(uri) }
            }
        }

        val currentDownloadId = downloadImpl(
                downloadManager = dm,
                title = request.fullFileName,
                url = request.url,
                fileName = request.fullFileName,
                notificationMode = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )

        val response = registerDownloading(request, progress, currentDownloadId)
        response.progressDispatcher.dispatch(currentDownloadId, this)

        return progress
    }

    private fun downloadImpl(downloadManager: DownloadManager, title: String, url: String, fileName: String, notificationMode: Int): Long {
        val uri = url.toUri()
        val request = DownloadManager.Request(uri).apply {
            setTitle(title)
            setDestinationInExternalPublicDir("${Environment.DIRECTORY_PICTURES}${File.separator}$DOWNLOAD_MANGER_FILE_SUB_DIR", fileName)
            setVisibleInDownloadsUi(true)
            setNotificationVisibility(notificationMode)
            setMimeType("image/*")
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            allowScanningByMediaScanner()
        }

        return downloadManager.enqueue(request)
    }

    private fun deleteSafe(file: File) {
        try {
            val result = file.delete()
            Timber.d("File deleted: $result")
        } catch (ex: IOException) {
            Timber.e(ex)
        } catch (ex: SecurityException) {
            Timber.e(ex)
        }
    }

    override fun cancel() {
        downloads[currentRequest]?.let {
            coroutineContext.cancelChildren()
            downloadManager.remove(it.downloadId)
            downloads.remove(currentRequest)
            progress.postValue(Progress.Canceled)
        }
    }

    private data class Downloading(val downloadId: Long, val progressDispatcher: ProgressUpdateDispatcher)

    companion object {
        const val DOWNLOAD_MANGER_FILE_SUB_DIR = "Wallpaperify"
    }
}

fun DownloadRequest.toFile() = File(createFilePath(this))

//todo: change for Andorid Q. https://stackoverflow.com/questions/56468539/getexternalstoragepublicdirectory-deprecated-in-android-q
private fun createFilePath(request: DownloadRequest): String {
    return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}${File.separator}${DownloadService.DOWNLOAD_MANGER_FILE_SUB_DIR}${File.separator}${request.fullFileName}"
}