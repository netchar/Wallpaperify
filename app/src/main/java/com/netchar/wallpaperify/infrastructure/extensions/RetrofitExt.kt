package com.netchar.wallpaperify.infrastructure.extensions

import com.netchar.wallpaperify.data.remote.HttpResult
import com.netchar.wallpaperify.data.remote.HttpStatusCode
import com.netchar.wallpaperify.infrastructure.exceptions.NoNetworkException
import kotlinx.coroutines.Deferred
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

suspend fun <T : Any> Deferred<Response<T>>.awaitSafe(): HttpResult<T> {
    return try {
        val response = this.await()
        if (response.isSuccessful) {
            val body = response.body()
            when {
                body.isValidBody() -> HttpResult.Success(body!!)
                response.noContent() -> HttpResult.Empty
                else -> HttpResult.Exception(IllegalStateException("Body is empty with ${response.raw().code()} status code."))
            }
        } else {
            HttpResult.Error.parse(response)
        }
    } catch (e: IOException) {
        Timber.e(e)
        HttpResult.Exception(e)
    }
}

private fun Any?.isValidBody() = this != null && this != ""
private fun <T : Any> Response<T>.noContent() = code().toHttpCode() == HttpStatusCode.NO_CONTENT
private fun Int.toHttpCode() = HttpStatusCode.getByCode(this)