package com.netchar.wallpaperify.infrastructure.extensions

import com.netchar.wallpaperify.data.remote.HttpResult
import com.netchar.wallpaperify.data.remote.HttpStatusCode
import kotlinx.coroutines.Deferred
import retrofit2.Response
import java.io.IOException

suspend fun <T : Any> Deferred<Response<T>>.awaitSafe(): HttpResult<T> {
    return try {
        val response = this.await()
        if (response.isSuccessful) {
            when {
                response.valid() -> HttpResult.Success(response.body())
                response.noContent() -> HttpResult.Empty
                else -> HttpResult.Exception(IllegalStateException("Body is empty with ${response.raw().code()} status code."))
            }
        } else {
            HttpResult.Error.parse(response)
        }
    } catch (e: IOException) {
        HttpResult.Exception(e)
    }
}

private fun <T : Any> Response<T>.valid() = body() != null && body() != ""
private fun <T : Any> Response<T>.noContent() = code().toHttpCode() == HttpStatusCode.NO_CONTENT
private fun Int.toHttpCode() = HttpStatusCode.getByCode(this)