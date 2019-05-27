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
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netchar.common.extensions.getCursor
import com.netchar.common.extensions.getInt
import com.netchar.common.extensions.toFileProviderUri
import com.netchar.common.extensions.using
import com.netchar.repository.pojo.Progress
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject


class DownloadService @Inject constructor(private val context: Context) : IDownloadService {
    private var downloadBroadcast: DownloadCompletionBroadcastReceiver? = null
    private var observingCursor: Cursor? = null
    private val downloads = hashMapOf<Long, DownloadRequest>()
    private val handler by lazy { DownloadProgressHandler(this) }
    private val contentObserver by lazy { DownloadChangeObserver(handler) }

    private lateinit var downloadManager: DownloadManager
    private lateinit var progress: MutableLiveData<Progress>

    var currentDownloadId: Long = -1
        private set

    @Throws(IllegalStateException::class)
    override fun download(request: DownloadRequest): LiveData<Progress> {
        try {
            downloadManager = context.getSystemService<DownloadManager>() ?: throw IllegalStateException("Unable to get DownloadManager")

            val file = request.toFile()

            progress = when (request.requestType) {
                DownloadRequest.REQUEST_DOWNLOAD -> downloadFile(downloadManager, request, file)
                DownloadRequest.REQUEST_WALLPAPER -> downloadWallpaper(downloadManager, request, file)
                else -> throw IllegalArgumentException("Wrong DownloadRequest type")
            }
        } catch (ex: IllegalArgumentException) {
            Timber.e(ex)
            notifyProgress(Progress.Error(Progress.ErrorCause.UNKNOWN))
        } catch (ex: IllegalStateException) {
            Timber.e(ex)
            notifyProgress(Progress.Error(Progress.ErrorCause.UNKNOWN))
        }

        return progress
    }

    private fun downloadWallpaper(dm: DownloadManager, request: DownloadRequest, file: File): MutableLiveData<Progress> {
        val progress = MutableLiveData<Progress>()

        if (file.exists()) {
            return progress.apply { value = Progress.FileExist(file.toFileProviderUri(context)) }
        }

        currentDownloadId = downloadImpl(
                downloadManager = dm,
                title = request.fullFileName,
                url = request.url,
                fileName = request.fullFileName,
                notificationMode = DownloadManager.Request.VISIBILITY_VISIBLE
        )
        downloads[currentDownloadId] = request
        return progress
    }

    private fun downloadFile(dm: DownloadManager, request: DownloadRequest, file: File): MutableLiveData<Progress> {
        val progress = MutableLiveData<Progress>()

        if (file.exists()) {
            if (request.forceOverride) {
                deleteSafe(file)
            } else {
                return progress.apply { value = Progress.FileExist(file.toFileProviderUri(context)) }
            }
        }

        currentDownloadId = downloadImpl(
                downloadManager = dm,
                title = request.fullFileName,
                url = request.url,
                fileName = request.fullFileName,
                notificationMode = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        )
        downloads[currentDownloadId] = request
        return progress
    }

    private fun downloadImpl(downloadManager: DownloadManager, title: String, url: String, fileName: String, notificationMode: Int): Long {
        val uri = url.toUri()
        val request = DownloadManager.Request(uri).apply {
            setTitle(title)
            setDestinationInExternalPublicDir("${Environment.DIRECTORY_PICTURES}${File.separator}$DOWNLOAD_MANGER_FILE_SUB_DIR", fileName)
            setVisibleInDownloadsUi(true)
            setNotificationVisibility(notificationMode)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            allowScanningByMediaScanner()
        }

        val enqueueId = downloadManager.enqueue(request)

        unregisterDownloadObservers()
        registerDownloadObservers(enqueueId)

        return enqueueId
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
        unregisterDownloadObservers()
        downloadManager.remove(currentDownloadId)
        downloads.remove(currentDownloadId)
        notifyProgress(Progress.Canceled)
    }

    override fun unregisterDownloadObservers() {
        if (observingCursor != null) {
            observingCursor?.unregisterContentObserver(contentObserver)
            observingCursor?.close()
            observingCursor = null
        }

        if (downloadBroadcast != null) {
            context.unregisterReceiver(downloadBroadcast)
            downloadBroadcast = null
        }
    }

    private fun registerDownloadObservers(enqueueId: Long) {
        observingCursor = downloadManager.getCursor(enqueueId)?.also {
            it.registerContentObserver(contentObserver)
            downloadBroadcast = DownloadCompletionBroadcastReceiver(this)
            context.registerReceiver(downloadBroadcast, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    fun updateProgressStatus() = synchronized(this) {
        val cursor = downloadManager.getCursor(currentDownloadId)

        if (cursor == null) {
            Timber.e("Cursor is empty for $currentDownloadId")
            return
        }

        cursor.using {
            val newStatus = getInt(DownloadManager.COLUMN_STATUS)
            val newProgressStatus: Progress

            when (newStatus) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    unregisterDownloadObservers()

                    val photoRequest = downloads[currentDownloadId]

                    if (photoRequest == null) {
                        Timber.e("Unable to find photoRequest for $currentDownloadId")
                        newProgressStatus = Progress.Error(Progress.ErrorCause.UNKNOWN)
                    } else {

                        val uri = photoRequest.toFile().toFileProviderUri(context)

                        if (photoRequest.requestType == DownloadRequest.REQUEST_WALLPAPER) {
                            forceScanForNewFiles(uri)
                        }

                        newProgressStatus = Progress.Success(uri)
                    }
                }
                DownloadManager.STATUS_FAILED -> {
                    unregisterDownloadObservers()
                    val errorCause = when (getInt(DownloadManager.COLUMN_REASON)) {
                        DownloadManager.ERROR_CANNOT_RESUME -> Progress.ErrorCause.CANNOT_RESUME
                        DownloadManager.ERROR_DEVICE_NOT_FOUND -> Progress.ErrorCause.DEVICE_NOT_FOUND
                        DownloadManager.ERROR_FILE_ALREADY_EXISTS -> Progress.ErrorCause.FILE_ALREADY_EXISTS
                        DownloadManager.ERROR_FILE_ERROR -> Progress.ErrorCause.FILE_ERROR
                        DownloadManager.ERROR_HTTP_DATA_ERROR -> Progress.ErrorCause.HTTP_DATA_ERROR
                        DownloadManager.ERROR_INSUFFICIENT_SPACE -> Progress.ErrorCause.INSUFFICIENT_SPACE
                        DownloadManager.ERROR_TOO_MANY_REDIRECTS -> Progress.ErrorCause.TOO_MANY_REDIRECTS
                        DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> Progress.ErrorCause.UNHANDLED_HTTP_CODE
                        DownloadManager.ERROR_UNKNOWN -> Progress.ErrorCause.UNKNOWN
                        else -> Progress.ErrorCause.UNKNOWN
                    }
                    newProgressStatus = Progress.Error(errorCause)
                }
                DownloadManager.STATUS_PAUSED -> {
                    unregisterDownloadObservers()
                    newProgressStatus = Progress.Error(Progress.ErrorCause.UNEXPECTED_PAUSE)
                }
                DownloadManager.STATUS_RUNNING -> {
                    val progress = getDownloadProgress(this)
                    newProgressStatus = Progress.Downloading(progress)
                }
                else -> {
                    newProgressStatus = Progress.Unknown(newStatus.toString())
                }
            }

            notifyProgress(newProgressStatus)
        }
    }


    private fun notifyProgress(progress: Progress) {
        this.progress.postValue(progress)
    }

    private fun forceScanForNewFiles(uri: Uri?) {
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    }

    private fun getDownloadProgress(cursor: Cursor): Float {
        val soFar = cursor.getInt(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        val total = cursor.getInt(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        val result = (100.0 * soFar / total).toInt()
        return result.coerceIn(0..100).toFloat()
    }

    private fun createFilePath(request: DownloadRequest): String {
        return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}${File.separator}$DOWNLOAD_MANGER_FILE_SUB_DIR${File.separator}${request.fullFileName}"
    }

    private fun DownloadRequest.toFile() = File(createFilePath(this))

    companion object {
        const val DOWNLOAD_MANAGER_MESSAGE_ID = 800
        const val DOWNLOAD_MANGER_FILE_SUB_DIR = "Wallpaperify"
    }
}

data class DownloadRequest(
        val url: String,
        val fileName: String,
        val fileQuality: String,
        val fileExtension: String,
        val requestType: Int,
        val forceOverride: Boolean = false
) {
    companion object {
        const val REQUEST_DOWNLOAD = 100
        const val REQUEST_WALLPAPER = 101
    }

    val fullFileName get() = "wallpaperify_${fileName}_$fileQuality.$fileExtension"
}