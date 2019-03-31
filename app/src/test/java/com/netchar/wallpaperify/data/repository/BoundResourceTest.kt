package com.netchar.wallpaperify.data.repository

import com.netchar.wallpaperify.data.remote.api.PhotosApi
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import io.mockk.clearMocks
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.Response

class BoundResourceTest {

    val coroutineDispatchers = CoroutineDispatchers()

    val boundResource = object : BoundResource<List<String>>(coroutineDispatchers) {
        override fun saveRemoteDataInStorage(data: List<String>?) {

        }

        override fun getStorageData(): List<String>? {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun isNeedRefresh(localData: List<String>): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override suspend fun apiRequestAsync(): Deferred<Response<List<String>>> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private val photosApi: PhotosApi = mockk()

    @BeforeEach
    fun init() {
        clearMocks(photosApi)
    }

    @Test
    @DisplayName("getLiveData should return not nul LiveData()")
    fun getLiveData() {
        Assertions.assertNotNull(boundResource.getLiveData())
    }

    @Test
    fun launchIn() {
    }

    @Test
    fun cancelJob() {
    }

    @Test
    fun saveRemoteDataInStorage() {
    }

    @Test
    fun getStorageData() {
    }

    @Test
    fun shouldRefresh() {
    }

    @Test
    fun apiRequestAsync() {
    }
}