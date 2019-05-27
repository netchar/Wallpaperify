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
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Message
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netchar.common.extensions.getCursor
import com.netchar.common.extensions.getInt
import com.netchar.common.extensions.using
import com.netchar.common.utils.weak
import com.netchar.repository.pojo.Progress
import com.netchar.repository.services.DownloadService.Companion.DOWNLOAD_MANAGER_MESSAGE_ID
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class DownloadService @Inject constructor(private val context: Context) : IDownloadService {
    var currentRequestedDownloadId: Long = -1

    private lateinit var downloadManager: DownloadManager
    private var downloadBroadcast: DownloadCompletionBroadcastReceiver? = null
    private var observingCursor: Cursor? = null
    private val downloads = hashMapOf<Long, DownloadRequest>()
    private val handler by lazy { DownloadProgressHandler(this) }
    private val contentObserver by lazy { DownloadChangeObserver(handler) }

    private lateinit var _progress: MutableLiveData<Progress>

    @Throws(IllegalStateException::class)
    override fun download(downloadRequest: DownloadRequest): LiveData<Progress> {
        try {
            downloadManager = context.getSystemService<DownloadManager>() ?: throw IllegalStateException("Unable to get DownloadManager")
            _progress = MutableLiveData()

            val uri = downloadRequest.url.toUri()
            val file = File(getFilePath(downloadRequest))

            if (file.exists()) {
                val existingFileUri = getUriForFile(file)
                notifyProgress(Progress.FileExist(existingFileUri))
            } else {
                val notificationVisibility = getNotificationVisibilityMode(downloadRequest)

                val request = DownloadManager.Request(uri).apply {
                    setTitle(downloadRequest.fullFileName)
                    setDestinationInExternalPublicDir("${Environment.DIRECTORY_PICTURES}${File.separator}$DOWNLOAD_MANGER_FILE_SUB_DIR", downloadRequest.fullFileName)
                    setVisibleInDownloadsUi(true)
                    setNotificationVisibility(notificationVisibility)
                    setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                    allowScanningByMediaScanner()
                }

                currentRequestedDownloadId = downloadManager.enqueue(request)
                downloads[currentRequestedDownloadId] = downloadRequest

                unregisterDownloadObservers()
                registerDownloadObservers(currentRequestedDownloadId)
            }
        } catch (ex: IllegalStateException) {
            Timber.e(ex)
            notifyProgress(Progress.Unknown(ex.localizedMessage))
        }

        return _progress
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

    private fun getNotificationVisibilityMode(downloadRequest: DownloadRequest): Int {
        return if (downloadRequest.requestType == DownloadRequest.REQUEST_DOWNLOAD) {
            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
        } else {
            DownloadManager.Request.VISIBILITY_VISIBLE
        }
    }

    private fun registerDownloadObservers(enqueueId: Long) {
        observingCursor = downloadManager.getCursor(enqueueId)?.also {
            it.registerContentObserver(contentObserver)
            downloadBroadcast = DownloadCompletionBroadcastReceiver(this)
            context.registerReceiver(downloadBroadcast, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    fun updateProgressStatus() {
        synchronized(this) {
            val cursor = downloadManager.getCursor(currentRequestedDownloadId)

            if (cursor == null) {
                Timber.e("Cursor is empty for $currentRequestedDownloadId")
                return
            }

            cursor.using {
                val newStatus = getInt(DownloadManager.COLUMN_STATUS)
                val newProgressStatus: Progress

                when (newStatus) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
//
//                        if (isSameStatus(newStatus)) {
//                            return@using
//                        }

                        unregisterDownloadObservers()

                        val photoRequest = downloads[currentRequestedDownloadId]

                        if (photoRequest == null) {
                            val errorMessage = "Unable to find photoRequest for $currentRequestedDownloadId"
                            Timber.e(errorMessage)
                            newProgressStatus = Progress.Error(Progress.ErrorCause.UNKNOWN, errorMessage)
                        } else {
                            val path = getFilePath(photoRequest)
                            val uri = getUriForFile(path)

                            if (photoRequest.requestType == DownloadRequest.REQUEST_WALLPAPER) {
                                forceScanForNewFiles(uri)
                            }

                            newProgressStatus = Progress.Success(uri)
                        }
                    }
                    DownloadManager.STATUS_FAILED -> {
                        unregisterDownloadObservers()
                        newProgressStatus = Progress.Error(Progress.ErrorCause.STATUS_FAILED)
                    }
                    DownloadManager.STATUS_PAUSED -> {
                        unregisterDownloadObservers()
                        newProgressStatus = Progress.Error(Progress.ErrorCause.STATUS_PAUSED)
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

                lastProgressStatus = newStatus
            }
        }
    }

    private fun getUriForFile(filePath: String): Uri {
        val file = File(filePath)
        return getUriForFile(file)
    }

    private fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(context, "com.netchar.wallpaperify.fileprovider", file)
    }

    private fun isSameStatus(downloadStatus: Int) = lastProgressStatus == downloadStatus

    private var lastProgressStatus: Int = -2

    override fun cancel() {
        unregisterDownloadObservers()
        downloadManager.remove(currentRequestedDownloadId)
        downloads.remove(currentRequestedDownloadId)
        notifyProgress(Progress.Canceled)
    }

    private fun notifyProgress(progress: Progress) {
        _progress.postValue(progress)
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

    private fun getFilePath(request: DownloadRequest): String {
        return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}${File.separator}$DOWNLOAD_MANGER_FILE_SUB_DIR${File.separator}${request.fullFileName}"
    }

    data class DownloadRequest(
            val url: String,
            val fileName: String,
            val fileQuality: String,
            val fileExtension: String,
            val requestType: Int
    ) {
        companion object {
            const val REQUEST_DOWNLOAD = 100
            const val REQUEST_WALLPAPER = 101
        }

        val fullFileName get() = "wallpaperify_${fileName}_$fileQuality.$fileExtension"
    }

    companion object {
        const val DOWNLOAD_MANAGER_MESSAGE_ID = 800
        const val DOWNLOAD_MANGER_FILE_SUB_DIR = "Wallpaperify"
    }
}

class DownloadChangeObserver(private val handler: DownloadProgressHandler) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        handler.sendEmptyMessage(DOWNLOAD_MANAGER_MESSAGE_ID)
    }
}

class DownloadProgressHandler(service: DownloadService) : Handler() {
    private val downloadService by weak(service)

    override fun handleMessage(msg: Message) {
        if (msg.what == DOWNLOAD_MANAGER_MESSAGE_ID) {
            downloadService?.updateProgressStatus()
        }
    }
}

class DownloadCompletionBroadcastReceiver(service: DownloadService) : BroadcastReceiver() {
    private val downloadService by weak(service)

    override fun onReceive(context: Context, intent: Intent) {
        downloadService?.run {
            val intentDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)

            if (intentDownloadId == -1L) {
                unregisterDownloadObservers()
                Timber.w("Unable to EXTRA_DOWNLOAD_ID from Intent")
                return@run
            }

            if (currentRequestedDownloadId != intentDownloadId) {
                Timber.w("Wrong download id")
                return@run
            }

            updateProgressStatus()
        }
    }
}