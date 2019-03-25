package com.netchar.wallpaperify.data.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.netchar.wallpaperify.data.models.Cause
import com.netchar.wallpaperify.data.remote.HttpResult
import com.netchar.wallpaperify.data.models.Resource
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import com.netchar.wallpaperify.infrastructure.extensions.awaitSafe
import kotlinx.coroutines.*
import retrofit2.Response

abstract class BoundResource<TResult : Any>(private val dispatchers: CoroutineDispatchers) {

    private val result = MediatorLiveData<Resource<TResult>>()

    fun launchIn(scope: CoroutineScope): LiveData<Resource<TResult>> {
        scope.launch {
            val databaseData = fetchFromDatabaseAsync()
            if (shouldRefresh(databaseData)) {
                val resource = fetchFromNetworkAsync()
                if (resource is Resource.Success) {
                    saveRemoteDataInStorage(resource.data)
                }
                result.value = resource
            } else {
                result.value = Resource.Success(databaseData)
            }
        }
        return result
    }

    abstract fun saveRemoteDataInStorage(data: TResult?)

    // todo: make suspend call
    private suspend fun fetchFromDatabaseAsync(): TResult? = withContext(dispatchers.database) {
        null
    }

    abstract fun shouldRefresh(localData: TResult?): Boolean

    private suspend fun fetchFromNetworkAsync(): Resource<TResult> {
        result.value = Resource.Loading(true)
        val apiResponse = apiRequestAsync().awaitSafe()
        result.value = Resource.Loading(false)
        return when (apiResponse) {
            is HttpResult.Success -> {
                if (apiResponse.data == null) {
                    Resource.Error(Cause.UNEXPECTED, "Error during fetching data from server.")
                }
                Resource.Success(apiResponse.data)
            }
            is HttpResult.Error -> Resource.Error.parse(apiResponse)
            is HttpResult.Exception -> Resource.Error(Cause.UNEXPECTED, message = apiResponse.exception.localizedMessage)
            else -> Resource.Error(Cause.UNEXPECTED, "Unable to fetch data from network.")
        }
    }

    abstract suspend fun apiRequestAsync(): Deferred<Response<TResult>>
}