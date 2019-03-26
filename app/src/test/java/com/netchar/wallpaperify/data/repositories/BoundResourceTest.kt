package com.netchar.wallpaperify.data.repositories

import android.arch.lifecycle.LiveData
import com.netchar.wallpaperify.data.models.Resource
import com.netchar.wallpaperify.data.remote.api.PhotosApi
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import io.mockk.clearMocks
import io.mockk.mockk
import kotlinx.coroutines.Deferred
import org.junit.jupiter.api.BeforeEach
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

        override fun shouldRefresh(localData: List<String>?): Boolean {
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
    fun `getLiveData should return LiveData()`() {
        val resultValue = boundResource.getLiveData()
        assert(true)
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