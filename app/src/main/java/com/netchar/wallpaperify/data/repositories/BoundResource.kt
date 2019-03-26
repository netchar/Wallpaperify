package com.netchar.wallpaperify.data.repositories

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

    final override fun getLiveData() = result

    fun launchIn(scope: CoroutineScope): IBoundResource<TResult> {
        job = scope.launch(dispatchers.main) {
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
        return this
    }

    final override fun cancelJob() {
        if (job.isActive) {
            job.cancel()
        }
    }

    abstract fun saveRemoteDataInStorage(data: TResult?)

    // todo: make suspend call
    private suspend fun fetchFromDatabaseAsync(): TResult? = withContext(dispatchers.database) {
        null
    }

    abstract fun shouldRefresh(localData: TResult?): Boolean

    private suspend fun fetchFromNetworkAsync(): Resource<TResult> {
        result.value = Resource.Loading(true)
        val apiResponse = withContext(dispatchers.network) { apiRequestAsync().awaitSafe() }
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