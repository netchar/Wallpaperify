package com.netchar.repository.services

import androidx.lifecycle.LiveData

interface IDownloadService {
    @Throws(IllegalStateException::class)
    fun download(downloadRequest: DownloadService.DownloadRequest): LiveData<Progress>

    fun cancel()
}