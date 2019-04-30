package com.netchar.repository

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netchar.common.exceptions.NoNetworkException
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.remote.enums.HttpResult
import com.netchar.remote.extensions.awaitSafe
import kotlinx.coroutines.*
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

abstract class BoundResource<TResult : Any>(private val dispatchers: CoroutineDispatchers) : IBoundResource<TResult> {
    private val result = MutableLiveData<Resource<TResult>>()

    @VisibleForTesting
    lateinit var job: Job

    final override fun getLiveData(): LiveData<Resource<TResult>> = result

    fun launchIn(scope: CoroutineScope): IBoundResource<TResult> {
        job = scope.launch(dispatchers.main) {
            result.value = tryGetResponse()
        }
        return this
    }

    private suspend fun tryGetResponse(): Resource<TResult> {
        return try {
            val databaseData = fetchFromDatabaseAsync()
            if (databaseData.isInvalidated()) {
                fetchFromNetworkAsync().also { writeInStorageOnSuccessAsync(it) }
            } else {
                Resource.Success(databaseData!!)
            }
        } catch (ex: IOException) {
            Timber.e(ex)
            Resource.Error.parse(ex)
        }
    }

    private suspend fun writeInStorageOnSuccessAsync(resource: Resource<TResult>) {
        if (resource is Resource.Success) {
            withContext(dispatchers.database) {
                saveRemoteDataInStorage(resource.data)
            }
        }
    }

    final override fun cancelJob() {
        if (job.isActive) {
            job.cancel()
        }
    }

    abstract fun saveRemoteDataInStorage(data: TResult)

    private suspend fun fetchFromDatabaseAsync(): TResult? = withContext(dispatchers.database) {
        getStorageData()
    }

    abstract fun getStorageData(): TResult?

    abstract fun isNeedRefresh(localData: TResult): Boolean

    private suspend fun fetchFromNetworkAsync(): Resource<TResult> {
        result.value = Resource.Loading(true)
        val apiResponse = getApiCallAsync().awaitSafe()
        result.value = Resource.Loading(false)

        return when (apiResponse) {
            is HttpResult.Success -> Resource.Success(apiResponse.data)
            is HttpResult.Empty -> Resource.Error(Cause.UNEXPECTED, "Error during fetching data from server.")
            is HttpResult.Error -> Resource.Error.parse(apiResponse)
            is HttpResult.Exception -> {
                if (apiResponse.exception is NoNetworkException) {
                    Resource.Error(Cause.NO_INTERNET_CONNECTION)
                } else {
                    Resource.Error(Cause.UNEXPECTED, message = apiResponse.exception.localizedMessage)
                }
            }
        }
    }

    abstract fun getApiCallAsync(): Deferred<Response<TResult>>

    private fun TResult?.isInvalidated() = this == null || isNeedRefresh(this)
}