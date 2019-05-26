package com.netchar.repository.services

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData

interface IDownloadService : LifecycleObserver {
    @Throws(IllegalStateException::class)
    fun download(downloadRequest: DownloadService.DownloadRequest): LiveData<Progress>

    fun cancel()
}