package com.netchar.repository.services

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netchar.common.extensions.getCursor
import com.netchar.common.extensions.getInt
import com.netchar.common.extensions.using
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.File
import javax.inject.Inject


class DownloadService @Inject constructor(val context: Context) {

    enum class DownloadStatus { SUCCESS, FAILED, DOWNLOADING }

    private var photoEnqueueId: Long = -1
    private lateinit var completionHandler: (filePath: Uri) -> Unit
    private lateinit var downloadManager: DownloadManager
    private val downloads = hashMapOf<Long, DownloadRequest>()
    private val _progress: MutableLiveData<Float> = MutableLiveData()

    val progress: LiveData<Float> = _progress

    var isDownloading = false

    val job = Job()
    val scope = CoroutineScope(job + Dispatchers.Main)

    @Throws(IllegalStateException::class)
    fun download(request: DownloadRequest, onDownloadComplete: (filePath: Uri) -> Unit) {
        downloadManager = context.getSystemService<DownloadManager>() ?: throw IllegalStateException("Unable to get DownloadManager")
        val path = request.getStorePath()
        val uri = Uri.parse(request.url)

        val dmRequest = DownloadManager.Request(uri).apply {
            setTitle(path)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, path)
            setVisibleInDownloadsUi(true)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setMimeType("image/jpeg")
            allowScanningByMediaScanner()
        }

        completionHandler = onDownloadComplete
        photoEnqueueId = downloadManager.enqueue(dmRequest)
        downloads[photoEnqueueId] = request
        context.registerReceiver(downloadBroadcast, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        isDownloading = true

        Thread {
            Thread.sleep(300)
            while (isDownloading) {
                val cursor = downloadManager.getCursor(photoEnqueueId)!!.using {
                    val progg = getDownloadProgress(this)
                    _progress.postValue(progg)
                }
            }
        }.start()


//        scope.launch {
//        Thread {
//            while (isDownloading) {
////                delay(100)
////                val progg = withContext(Dispatchers.IO) {
//                val progg  = getDownloadProgress(cursor)
////                }
//
//                _progress.postValue(progg)
//            }
//        }.start()
//        while (isDownloading) {
////                delay(100)
////                val progg = withContext(Dispatchers.IO) {
//            getDownloadProgress(cursor)
////                }
//
//            _progress.value = 30f
//        }
//        }
    }

    fun cancel() {
        downloadManager.remove(photoEnqueueId)
        downloads.remove(photoEnqueueId)
    }

    private val downloadBroadcast = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val intentDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (intentDownloadId != photoEnqueueId) return

            val request = downloads[intentDownloadId] ?: throw IllegalAccessException("Request for enqueueId: $photoEnqueueId not found.")
            val cursor = downloadManager.getCursor(intentDownloadId)

            cursor?.using {

                when (getDownloadStatus(cursor = this)) {
                    DownloadStatus.SUCCESS -> {
                        val uri = getPhotoUri(context, intentDownloadId)
                        val progress = getDownloadProgress(this)

                        if (request.requestType == DownloadRequest.REQUEST_WALLPAPER) {
                            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                        }

                        completionHandler(uri)
                    }
                    DownloadStatus.FAILED -> {

                    }
                    DownloadStatus.DOWNLOADING -> {

                    }
                }

                isDownloading = false
            }
        }
    }

    private fun getPhotoUri(context: Context, photoEnqueueId: Long): Uri {
        val request = downloads[photoEnqueueId] ?: throw IllegalAccessException("Request for enqueueId: $photoEnqueueId not found.")
        val path = getFilePath(request)
        val uri = FileProvider.getUriForFile(context, "com.netchar.wallpaperify.fileprovider", File(path))
        return uri
    }

    private fun getFilePath(request: DownloadRequest): String {
        return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}${File.separator}${request.getStorePath()}"
    }

    private fun getDownloadStatus(cursor: Cursor): DownloadStatus {
        return when (cursor.getInt(DownloadManager.COLUMN_STATUS)) {
            DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.SUCCESS
            DownloadManager.STATUS_FAILED, DownloadManager.STATUS_PAUSED -> DownloadStatus.FAILED
            else -> DownloadStatus.DOWNLOADING
        }
    }

    fun getDownloadProgress(cursor: Cursor): Float {
        val soFar = cursor.getInt(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
        val total = cursor.getInt(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
        var result = (100.0 * soFar / total).toInt()
        result = Math.max(0, result)
        result = Math.min(100, result)
        return result.toFloat()
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

        fun getStorePath() = "${fileName}_$fileQuality.$fileExtension"
    }
}