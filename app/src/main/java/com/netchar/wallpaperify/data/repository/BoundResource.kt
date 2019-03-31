package com.netchar.wallpaperify.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.netchar.wallpaperify.data.models.Cause
import com.netchar.wallpaperify.data.remote.HttpResult
import com.netchar.wallpaperify.data.models.Resource
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import com.netchar.wallpaperify.infrastructure.extensions.awaitSafe
import kotlinx.coroutines.*
import retrofit2.Response

abstract class BoundResource<TResult : Any>(private val dispatchers: CoroutineDispatchers) : IBoundResource<TResult> {
    private val result = MutableLiveData<Resource<TResult>>()

    private lateinit var job: Job

    final override fun getLiveData(): LiveData<Resource<TResult>> = result

    fun launchIn(scope: CoroutineScope): IBoundResource<TResult> {
        job = scope.launch(dispatchers.main) {
            val databaseData = fetchDataFromDatabaseAsync()

            result.value = if (databaseData == null || isNeedRefresh(databaseData)) {
                fetchFromNetworkAsync().also {
                    writeInStorageOnSuccessAsync(it)
                }
            } else {
                Resource.Success(databaseData)
            }
        }
        return this
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

    abstract fun saveRemoteDataInStorage(data: TResult?)

    private suspend fun fetchDataFromDatabaseAsync(): TResult? = withContext(dispatchers.database) {
        getStorageData()
    }

    abstract fun getStorageData(): TResult?

    abstract fun isNeedRefresh(localData: TResult): Boolean

    private suspend fun fetchFromNetworkAsync(): Resource<TResult> {
        result.value = Resource.Loading(true)
        val apiResponse = apiRequestAsync().awaitSafe()
        result.value = Resource.Loading(false)
        return when (apiResponse) {
            is HttpResult.Success -> prepareResourceFor(apiResponse)
            is HttpResult.Error -> Resource.Error.parse(apiResponse)
            is HttpResult.Exception -> Resource.Error(Cause.UNEXPECTED, message = apiResponse.exception.localizedMessage)
            else -> Resource.Error(Cause.UNEXPECTED, "Unable to fetch data from network.")
        }
    }

    private fun prepareResourceFor(apiResponse: HttpResult.Success<TResult>): Resource<TResult> {
        return if (apiResponse.data == null) {
            Resource.Error(Cause.UNEXPECTED, "Error during fetching data from server.")
        } else {
            Resource.Success(apiResponse.data)
        }
    }

    abstract suspend fun apiRequestAsync(): Deferred<Response<TResult>>
}