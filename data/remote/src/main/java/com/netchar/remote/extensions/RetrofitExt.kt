package com.netchar.remote.extensions

import com.netchar.common.exceptions.NoNetworkException
import com.netchar.remote.enums.HttpResult
import com.netchar.remote.enums.HttpStatusCode
import com.squareup.moshi.JsonDataException
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
                else -> {
                    val exception = IllegalStateException("Body is empty with ${response.code()} status code.")
                    Timber.e(exception)
                    HttpResult.Exception(exception)
                }
            }
        } else {
            HttpResult.Error.parse(response)
        }
    } catch (e: JsonDataException) {
        Timber.e(e)
        HttpResult.Exception(e)
    } catch (e: NoNetworkException) {
        HttpResult.Exception(e)
    } catch (e: IOException) {
        Timber.e(e)
        HttpResult.Exception(e)
    }
}

private fun Any?.isValidBody() = this != null && this != ""
private fun <T : Any> Response<T>.noContent() = code().toHttpCode() == HttpStatusCode.NO_CONTENT
private fun Int.toHttpCode() = HttpStatusCode.getByCode(this)