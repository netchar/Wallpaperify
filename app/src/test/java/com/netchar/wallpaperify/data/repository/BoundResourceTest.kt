package com.netchar.wallpaperify.data.repository

import androidx.lifecycle.LiveData
import com.netchar.wallpaperify.data.models.Resource
import com.netchar.wallpaperify.data.remote.HttpResult
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import com.netchar.wallpaperify.infrastructure.extensions.awaitSafe
import io.mockk.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertTrue
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import org.junit.rules.TestRule
import org.junit.Rule
import org.mockito.Mock


@ExperimentalCoroutinesApi
internal class BoundResourceTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    val mockdispatchers = spyk<CoroutineDispatchers> {
        every { database } answers { Dispatchers.Unconfined }
        every { default } answers { Dispatchers.Unconfined }
        every { disk } answers { Dispatchers.Unconfined }
        every { main } answers { Dispatchers.Unconfined }
        every { network } answers { Dispatchers.Unconfined }
    }

    val boundResourceMock = object : BoundResource<String>(mockdispatchers) {
        override fun saveRemoteDataInStorage(data: String?) {
        }

        override fun getStorageData(): String? {
            return null
        }

        override fun isNeedRefresh(localData: String): Boolean {
            return true
        }

        override suspend fun apiRequestAsync(): Deferred<Response<String>> {
            return spyk()
        }
    }

    @Mock
    lateinit var observer: Observer<Resource<String>>

    @Test
    fun launchIn() {
        val testResource: BoundResource<String> = spyk(boundResourceMock) {
            every { getStorageData() } returns null
            coEvery { apiRequestAsync() } returns mockk(relaxed = true)
            coEvery { apiRequestAsync().awaitSafe() } answers { HttpResult.Success("Success data") }
//            coEvery { any<Deferred<Response<String>>>().awaitSafe() } returns HttpResult.Success("Success data")
        }
        runBlocking {
            val bres = testResource.launchIn(this)
//            val livedata = bres.getLiveData()
//            livedata.observeForever(observer)

//            assertTrue { livedata.value is Resource.Success }
        }
//        val liveData = testResource.launchIn(GlobalScope)
//        assertTrue { liveData.getLiveData().value == Resource.Success("Success data") }
    }
}