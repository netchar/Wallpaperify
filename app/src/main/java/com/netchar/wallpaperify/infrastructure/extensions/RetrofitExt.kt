package com.netchar.wallpaperify.infrastructure.extensions

import com.netchar.remote.enums.HttpStatusCode
import kotlinx.coroutines.Deferred
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

suspend fun <T : Any> Deferred<Response<T>>.awaitSafe(): com.netchar.remote.enums.HttpResult<T> {
    return try {
        val response = this.await()
        if (response.isSuccessful) {
            val body = response.body()
            when {
                body.isValidBody() -> com.netchar.remote.enums.HttpResult.Success(body!!)
                response.noContent() -> com.netchar.remote.enums.HttpResult.Empty
                else -> com.netchar.remote.enums.HttpResult.Exception(IllegalStateException("Body is empty with ${response.raw().code()} status code."))
            }
        } else {
            com.netchar.remote.enums.HttpResult.Error.parse(response)
        }
    } catch (e: IOException) {
        Timber.e(e)
        com.netchar.remote.enums.HttpResult.Exception(e)
    }
}

private fun Any?.isValidBody() = this != null && this != ""
private fun <T : Any> Response<T>.noContent() = code().toHttpCode() == com.netchar.remote.enums.HttpStatusCode.NO_CONTENT
private fun Int.toHttpCode() = com.netchar.remote.enums.HttpStatusCode.getByCode(this)