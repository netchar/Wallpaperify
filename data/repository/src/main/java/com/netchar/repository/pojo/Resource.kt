package com.netchar.repository.pojo

import com.netchar.remote.enums.Cause
import com.netchar.remote.enums.HttpResult
import com.netchar.remote.enums.HttpStatusCode

sealed class Resource<out TValue> {
    data class Success<out TValue>(val data: TValue) : Resource<TValue>()
    data class Error(val cause: Cause, val message: String = "") : Resource<Nothing>() {
        companion object {

            fun parse(response: HttpResult.Error): Error {
                val cause = when (response.httpStatusCode) {
                    HttpStatusCode.UNAUTHORIZED -> Cause.NOT_AUTHENTICATED
                    else -> Cause.UNEXPECTED
                }

                val message = response.error?.errors?.joinToString { it } ?: response.httpStatusCode.description

                return Error(cause, message)
            }

            fun parse(throwable: Throwable): Error {
                return Error(Cause.UNEXPECTED, throwable.localizedMessage ?: "Unexpected error")
            }
        }
    }

    data class Loading(val isLoading: Boolean) : Resource<Nothing>()
}