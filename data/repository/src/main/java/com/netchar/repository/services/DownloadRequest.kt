package com.netchar.repository.services

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