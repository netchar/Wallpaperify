package com.netchar.repository.services

import androidx.lifecycle.LiveData
import com.netchar.repository.pojo.Progress

interface IDownloadService {
    @Throws(IllegalStateException::class)
    fun download(downloadRequest: DownloadService.DownloadRequest): LiveData<Progress>

    fun cancel()

    fun unregisterDownloadObservers()
}