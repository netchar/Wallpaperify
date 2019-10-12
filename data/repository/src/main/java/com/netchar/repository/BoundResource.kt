/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.repository

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netchar.common.exceptions.NoNetworkException
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.enums.Cause
import com.netchar.remote.enums.HttpResult
import com.netchar.remote.extensions.awaitSafe
import com.netchar.repository.pojo.Resource
import kotlinx.coroutines.*
import retrofit2.Response
import timber.log.Timber
import java.io.IOException


abstract class BoundResource<TResourceData : Any, TNetworkDto : Any>(private val dispatchers: CoroutineDispatchers) : IBoundResource<TResourceData> {
    private val result = MutableLiveData<Resource<TResourceData>>()

    @VisibleForTesting
    lateinit var job: Job

    final override fun getLiveData(): LiveData<Resource<TResourceData>> = result

    fun launchIn(scope: CoroutineScope): IBoundResource<TResourceData> {
        job = scope.launch(dispatchers.main) {
            result.value = tryGetResponse()
        }
        return this
    }

    override suspend fun getAsync(): Resource<TResourceData> {
        return tryGetResponse()
    }

    private suspend fun tryGetResponse(): Resource<TResourceData> {
        return try {
            val databaseData = fetchFromDatabaseAsync()
            if (databaseData.isInvalidated()) {
                fetchFromNetworkAsync()
            } else {
                Resource.Success(databaseData!!)
            }
        } catch (ex: IllegalArgumentException) {
            Timber.e(ex)
            Resource.Error.parse(ex)
        } catch (ex: IOException) {
            Timber.e(ex)
            Resource.Error.parse(ex)
        }
    }

    private suspend fun writeInStorageOnSuccessAsync(data: TNetworkDto) = withContext(dispatchers.database) {
        saveRemoteDataInStorage(data)
    }

    final override fun cancelJob() {
        if (job.isActive) {
            job.cancel()
        }
    }

    abstract fun saveRemoteDataInStorage(data: TNetworkDto)

    private suspend fun fetchFromDatabaseAsync(): TResourceData? = withContext(dispatchers.database) {
        getStorageData()
    }

    abstract fun getStorageData(): TResourceData?

    abstract fun isNeedRefresh(localData: TResourceData): Boolean

    private suspend fun fetchFromNetworkAsync(): Resource<TResourceData> {
        result.value = Resource.Loading(true)
        val apiResponse = getApiCallAsync().awaitSafe()
        result.value = Resource.Loading(false)

        return when (apiResponse) {
            is HttpResult.Success -> {
                writeInStorageOnSuccessAsync(apiResponse.data)
                val data = mapToPOJO(apiResponse.data)
                Resource.Success(data)
            }
            is HttpResult.Empty -> Resource.Error(Cause.UNEXPECTED, "Error during fetching data from server.")
            is HttpResult.Error -> Resource.Error.parse(apiResponse)
            is HttpResult.Exception -> {
                if (apiResponse.exception is NoNetworkException) {
                    Resource.Error(Cause.NO_INTERNET_CONNECTION)
                } else {
                    Resource.Error(Cause.UNEXPECTED, message = apiResponse.exception.localizedMessage ?: "")
                }
            }
        }
    }

    abstract fun mapToPOJO(data: TNetworkDto): TResourceData

    abstract fun getApiCallAsync(): Deferred<Response<TNetworkDto>>

    private fun TResourceData?.isInvalidated() = this == null || isNeedRefresh(this)
}