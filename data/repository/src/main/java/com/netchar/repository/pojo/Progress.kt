package com.netchar.repository.pojo

import android.net.Uri

sealed class Progress {
    enum class ErrorCause {
        UNKNOWN, STATUS_FAILED, STATUS_PAUSED
    }

    data class Success(val fileUri: Uri) : Progress()
    data class Error(val cause: ErrorCause, val message: String = "") : Progress()
    data class Downloading(val progressSoFar: Float) : Progress()
    data class Unknown(val status: String) : Progress()
}