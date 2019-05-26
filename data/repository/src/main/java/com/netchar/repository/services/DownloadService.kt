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
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleObserver
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

class DownloadService @Inject constructor(private val context: Context) : LifecycleObserver, IDownloadService {

    private var photoEnqueueId: Long = -1
    private lateinit var downloadManager: DownloadManager
    private var observingCursor: Cursor? = null

    private val downloads = hashMapOf<Long, DownloadRequest>()
    private val handler by lazy { DownloadProgressHandler(this) }
    private val contentObserver by lazy { DownloadChangeObserver(handler) }
    private var downloadBroadcast: DownloadCompletionBroadcastReceiver? = null

    private val _progress: MutableLiveData<Progress> = MutableLiveData()

    @Throws(IllegalStateException::class)
    override fun download(downloadRequest: DownloadRequest): LiveData<Progress> {
        try {
            downloadManager = context.getSystemService<DownloadManager>() ?: throw IllegalStateException("Unable to get DownloadManager")

            val uri = downloadRequest.url.toUri()
            val request = DownloadManager.Request(uri).apply {
                setTitle(downloadRequest.fullFileName)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, downloadRequest.fullFileName)
                setVisibleInDownloadsUi(true)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                allowScanningByMediaScanner()
            }

            photoEnqueueId = downloadManager.enqueue(request)
            downloads[photoEnqueueId] = downloadRequest

            unregisterDownloadObservers()
            registerDownloadObservers()

        } catch (ex: IllegalStateException) {
            Timber.e(ex)
            _progress.value = Progress.Unknown(ex.localizedMessage)
        }

        return _progress
    }

    private fun registerDownloadObservers() {
        observingCursor = downloadManager.getCursor(photoEnqueueId)?.also {
            it.registerContentObserver(contentObserver)
        }
        downloadBroadcast = DownloadCompletionBroadcastReceiver(this)
        context.registerReceiver(downloadBroadcast, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    fun unregisterDownloadObservers() {
        observingCursor.release()
        observingCursor = null

        if (downloadBroadcast != null) {
            context.unregisterReceiver(downloadBroadcast)
            downloadBroadcast = null
        }
    }

    private fun notifyProgress(progress: Progress) {
        _progress.postValue(progress)
    }

    fun updateProgressStatus() {
        synchronized(this) {
            val cursor = downloadManager.getCursor(photoEnqueueId)

            if (cursor == null) {
                Timber.e("Cursor is empty for $photoEnqueueId")
                return
            }

            val downloadStatus = cursor.getInt(DownloadManager.COLUMN_STATUS)
            val newProgressStatus: Progress

            when (downloadStatus) {
                DownloadManager.STATUS_SUCCESSFUL -> {
                    unregisterDownloadObservers()

                    val photoRequest = downloads[photoEnqueueId]

                    if (photoRequest == null) {
                        val errorMessage = "Unable to find photoRequest for $photoEnqueueId"
                        Timber.e(errorMessage)
                        newProgressStatus = Progress.Error(Progress.ErrorCause.UNKNOWN, errorMessage)
                    } else {
                        val uri = Uri.parse(getFilePath(photoRequest))

                        if (photoRequest.requestType == DownloadRequest.REQUEST_WALLPAPER) {
                            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
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
                    val progress = getDownloadProgress(cursor)
                    newProgressStatus = Progress.Downloading(progress)
                }
                else -> {
                    newProgressStatus = Progress.Unknown(downloadStatus.toString())
                }
            }

            notifyProgress(newProgressStatus)
        }
    }

    private fun Cursor?.release() = this?.using {
        unregisterContentObserver(contentObserver)
    }

    private fun getDownloadProgress(cursor: Cursor): Float {
        val soFar = cursor.getInt(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        val total = cursor.getInt(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        val result = (100.0 * soFar / total).toInt()
        return result.coerceIn(0..100).toFloat()
    }

    override fun cancel() {
        // todo: check how it will work with remove
        downloadManager.remove(photoEnqueueId)
        downloads.remove(photoEnqueueId)
        observingCursor.release()
    }

//    private fun getPhotoUri(context: Context, photoEnqueueId: Long): Uri {
//        val request = downloads[photoEnqueueId] ?: throw IllegalAccessException("Request for enqueueId: $photoEnqueueId not found.")
//
//        val path = getFilePath(request)
//        // todo: get app id from BuildConfig
//        val uri = FileProvider.getUriForFile(context, "com.netchar.wallpaperify.fileprovider", File(path))
//        return uri
//    }

    private fun getFilePath(request: DownloadRequest): String {
        return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}${File.separator}${request.fullFileName}"
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
            const val REQUEST_WALLPAPER = 100
        }

        val fullFileName get() = "wallpaperify_${fileName}_$fileQuality.$fileExtension"
    }

    companion object {
        const val DOWNLOAD_MANAGER_MESSAGE_ID = 800
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

            updateProgressStatus()
        }
    }
}