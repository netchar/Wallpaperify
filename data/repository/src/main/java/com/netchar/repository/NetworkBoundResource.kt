package com.netchar.repository

import com.netchar.common.utils.CoroutineDispatchers
import kotlinx.coroutines.Deferred
import retrofit2.Response

class NetworkBoundResource<TResult : Any>(dispatchers: CoroutineDispatchers, private val apiCall: () -> Deferred<Response<TResult>>) : BoundResource<TResult>(dispatchers) {
    override fun saveRemoteDataInStorage(data: TResult) {
        // ignore
    }

    override fun getStorageData(): TResult? = null

    override fun isNeedRefresh(localData: TResult): Boolean = true

    override fun getApiCallAsync(): Deferred<Response<TResult>> = apiCall()
}