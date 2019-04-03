package com.netchar.wallpaperify.data.models

import com.netchar.wallpaperify.data.remote.HttpResult
import com.netchar.wallpaperify.data.remote.HttpStatusCode

sealed class Resource<out TValue> {
    data class Success<out TValue>(val data: TValue) : Resource<TValue>()
    data class Error(val cause: Cause, val message: String) : Resource<Nothing>() {
        companion object {

            fun parse(response: HttpResult.Error): Error {
                val cause = when (response.httpStatusCode) {
                    HttpStatusCode.UNAUTHORIZED -> Cause.NOT_AUTHENTICATED
                    else -> Cause.UNEXPECTED
                }

                val message = response.error?.errors?.joinToString { it } ?: response.httpStatusCode.description

                return Error(cause, message)
            }
        }
    }

    data class Loading(val isLoading: Boolean) : Resource<Nothing>()
}