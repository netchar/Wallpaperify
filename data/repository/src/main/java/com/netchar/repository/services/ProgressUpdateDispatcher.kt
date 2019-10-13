package com.netchar.repository.services

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.core.content.FileProvider
import com.netchar.common.extensions.getCursor
import com.netchar.common.extensions.getInt
import com.netchar.common.utils.IBuild
import com.netchar.repository.pojo.Progress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ProgressUpdateDispatcher(
        private val request: DownloadRequest,
        private val downloadManager: DownloadManager,
        private val context: Context,
        private val build: IBuild,
        private val onProgressUpdate: (progress: Progress) -> Unit
) {
    companion object {
        private const val DELAY_BETWEEN_PROGRESS_UPDATE_MS = 10L
    }

    fun dispatch(downloadId: Long, scope: CoroutineScope) {
        scope.launch {
            var currentProgress: Progress
            do {
                currentProgress = getProgress(downloadId)
                onProgressUpdate(currentProgress)
                delay(DELAY_BETWEEN_PROGRESS_UPDATE_MS)
            } while (isActive && (currentProgress is Progress.Downloading || currentProgress is Progress.Pending))
        }
    }

    private fun getProgress(id: Long): Progress {
        val cursor = downloadManager.getCursor(id) ?: return Progress.Unknown("Cursor is empty")
        cursor.use {
            return when (val columnStatus = it.getInt(DownloadManager.COLUMN_STATUS)) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    val uri = FileProvider.getUriForFile(context, "${build.getApplicationId()}.fileprovider", request.toFile())

                    if (request.requestType == DownloadRequest.REQUEST_WALLPAPER) {
                        forceScanForNewFiles(uri)
                    }

                    Progress.Success(uri)
                }
                DownloadManager.STATUS_FAILED -> {
                    val errorCause = when (it.getInt(DownloadManager.COLUMN_REASON)) {
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
                    Progress.Error(errorCause)
                }
                DownloadManager.STATUS_PAUSED -> {
                    Progress.Error(Progress.ErrorCause.UNEXPECTED_PAUSE)
                }
                DownloadManager.STATUS_PENDING -> {
                    Progress.Pending
                }
                DownloadManager.STATUS_RUNNING -> {
                    val progress = getDownloadProgress(it)
                    Progress.Downloading(progress)
                }
                else -> Progress.Unknown(columnStatus.toString())
            }
        }
    }

    private fun getDownloadProgress(cursor: Cursor): Float {
        val soFar = cursor.getInt(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        val total = cursor.getInt(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        val result = (100.0 * soFar / total).toInt()
        return result.coerceIn(0..100).toFloat()
    }

    private fun forceScanForNewFiles(uri: Uri?) {
        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
    }
}