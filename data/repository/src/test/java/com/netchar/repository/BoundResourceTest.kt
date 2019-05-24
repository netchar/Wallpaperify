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

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.netchar.common.exceptions.NoNetworkException
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import io.mockk.*
import kotlinx.coroutines.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val ASSERT_LIVE_DATA_IN_ORDER = "LiveData should emit Resource value in strict order"

@Suppress("DeferredResultUnused")
@ExperimentalCoroutinesApi
@ExtendWith(InstantTaskExecutorExtension::class)
internal class BoundResourceTest {

    private val mockDispatchers = mockk<CoroutineDispatchers> {
        every { database } returns Dispatchers.Unconfined
        every { default } returns Dispatchers.Unconfined
        every { disk } returns Dispatchers.Unconfined
        every { main } returns Dispatchers.Unconfined
        every { network } returns Dispatchers.Unconfined
    }

    private val successApiResponseMock = Response.success("success")
    private val storageDataMock = "Mock database data"
    private lateinit var boundResource: BoundResource<String, String>
    private val responseSet = LinkedHashSet<Resource<String>>()
    private val successReferenceResponseSet: LinkedHashSet<Resource<String>> = linkedSetOf(
            Resource.Loading(true),
            Resource.Loading(false),
            Resource.Success("success"))

    private val observer = Observer<Resource<String>> { responseSet.add(it) }

    private lateinit var observerMock: Observer<Resource<String>>

    @BeforeEach
    fun setUp() {
        boundResource = spyk(object : BoundResource<String, String>(mockDispatchers) {
            override fun saveRemoteDataInStorage(data: String) {
            }

            override fun getStorageData(): String? {
                return null
            }

            override fun isNeedRefresh(localData: String): Boolean {
                return true
            }

            override fun mapToPOJO(data: String): String {
                return ""
            }

            override fun getApiCallAsync(): Deferred<Response<String>> {
                return spyk()
            }
        })

        observerMock = mockk(relaxed = true)
    }

    @AfterEach
    fun afterEach() {
        clearMocks(boundResource)
        responseSet.clear()
    }

    @Test
    fun `when getLiveData() return LiveData instance`() {
        assertNotNull(boundResource.getLiveData())
    }

    @Test
    fun `when launchIn return non-null IBoundResource instance`() {
        runBlocking {
            // arrange
            every { boundResource.getStorageData() } returns null
            coEvery { boundResource.getApiCallAsync().await() } returns successApiResponseMock

            // act
            val boundResponse = boundResource.launchIn(this)

            // assert
            assertThat(boundResponse.getLiveData().value, notNullValue())
        }
    }

    @Test
    fun `when storage data valid should return database data`() {
        runBlocking {
            // arrange
            val expectedLiveData = mockk<LiveData<Resource<String>>> {
                every { value } returns Resource.Success(storageDataMock)
            }
            every { boundResource.getStorageData() } returns storageDataMock
            every { boundResource.isNeedRefresh(storageDataMock) } returns false

            // act
            val boundResponse = boundResource.launchIn(this)
            boundResource.job.join()

            // assert
            assertThat(expectedLiveData.value, equalTo(boundResponse.getLiveData().value))

            verify { boundResource.getStorageData() }
            verify(exactly = 1) { boundResource.getStorageData() }
            verify(exactly = 1) { boundResource.isNeedRefresh(storageDataMock) }
        }
    }


    @Test
    fun `when storage data is null return success resource with network data`() {
        runBlocking {
            // arrange
            every { boundResource.getStorageData() } returns null
            coEvery { boundResource.getApiCallAsync().await() } coAnswers {
                delay(50)
                successApiResponseMock
            }

            // act
            launchAndObserve()

            verify { boundResource.getStorageData() }
            verify { boundResource.getApiCallAsync() }
            verify { boundResource.saveRemoteDataInStorage(successApiResponseMock.body()!!) }
        }
    }

    @Test
    fun `when storage data is not null but invalidated return success resource with network data`() {
        runBlocking {
            // arrange
            every { boundResource.getStorageData() } returns storageDataMock
            every { boundResource.isNeedRefresh(any()) } returns true
            coEvery { boundResource.getApiCallAsync().await() } coAnswers {
                delay(50)
                successApiResponseMock
            }

            // act
            launchAndObserve()

            // assert
            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(storageDataMock) }
            verify { boundResource.getApiCallAsync() }
            verify { boundResource.saveRemoteDataInStorage(successApiResponseMock.body()!!) }
        }
    }

    @Test
    fun `when fetching storage data throws an exception return error resource`() {
        runBlocking {
            // arrange
            every { boundResource.getStorageData() } throws IOException("Unable to get data")
            every { boundResource.isNeedRefresh(any()) } returns true

            // act
            launchAndObserve()

            // assert
            assertThat(responseSet, allOf(hasItem(Resource.Error(Cause.UNEXPECTED, "Unable to get data")), hasSize(equalTo(1))))

            verify { boundResource.getStorageData() }
            verify(exactly = 0) { boundResource.saveRemoteDataInStorage(any()) }
        }
    }

    @Test
    fun `when isNeedRefresh throws an exception return error resource`() {
        runBlocking {
            // arrange
            every { boundResource.getStorageData() } returns storageDataMock
            every { boundResource.isNeedRefresh(any()) } throws IOException("Unable to get data")

            // act
            launchAndObserve()

            // assert
            assertThat(responseSet, allOf(hasItem(Resource.Error(Cause.UNEXPECTED, "Unable to get data")), hasSize(equalTo(1))))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(any()) }
        }
    }

    @Test
    fun `when saveRemoteDataInStorage throws an exception return error resource`() {
        runBlocking {
            // arrange
            val referenceSet = linkedSetOf<Resource<String>>(Resource.Loading(true), Resource.Loading(false), Resource.Error(Cause.UNEXPECTED, "Unable to store"))
            every { boundResource.saveRemoteDataInStorage(any()) } throws IOException("Unable to store")
            coEvery { boundResource.getApiCallAsync().await() } coAnswers {
                delay(50)
                successApiResponseMock
            }

            // act
            launchAndObserve()

            // assert
            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(referenceSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.saveRemoteDataInStorage(successApiResponseMock.body()!!) }
        }
    }

    @Test
    fun `when network response error return UNEXPECTED error resource`() {
        runBlocking {
            // arrange
            val referenceSet = linkedSetOf<Resource<String>>(Resource.Loading(true), Resource.Loading(false), Resource.Error(Cause.UNEXPECTED, com.netchar.remote.enums.HttpStatusCode.INTERNAL_SERVER_ERROR.description))
            every { boundResource.getStorageData() } returns storageDataMock
            every { boundResource.isNeedRefresh(storageDataMock) } returns true
            coEvery { boundResource.getApiCallAsync().await() } coAnswers {
                delay(50)
                getMockErrorResponse(com.netchar.remote.enums.HttpStatusCode.INTERNAL_SERVER_ERROR)
            }

            // act
            launchAndObserve()

            // assert
            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(referenceSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(storageDataMock) }
            verify { boundResource.getApiCallAsync() }
            verify(exactly = 0) { boundResource.saveRemoteDataInStorage(eq(storageDataMock)) }
        }
    }


    @Test
    fun `when network response UNAUTHORIZED return NOT_AUTHENTICATED error resource`() {
        runBlocking {
            // arrange
            val referenceSet = linkedSetOf<Resource<String>>(Resource.Loading(true), Resource.Loading(false), Resource.Error(Cause.NOT_AUTHENTICATED, com.netchar.remote.enums.HttpStatusCode.UNAUTHORIZED.description))
            every { boundResource.getStorageData() } returns storageDataMock
            every { boundResource.isNeedRefresh(any()) } returns true
            coEvery { boundResource.getApiCallAsync().await() } coAnswers {
                delay(50)
                getMockErrorResponse(com.netchar.remote.enums.HttpStatusCode.UNAUTHORIZED)
            }

            // act
            launchAndObserve()

            // assert
            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(referenceSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(any()) }
            verify { boundResource.getApiCallAsync() }
            verify(exactly = 0) { boundResource.saveRemoteDataInStorage(any()) }
        }
    }

    @Test
    fun `when body is empty return UNEXPECTED error resource`() {
        runBlocking {
            // arrange
            val referenceSet = linkedSetOf<Resource<String>>(Resource.Loading(true), Resource.Loading(false), Resource.Error(Cause.UNEXPECTED, "Error during fetching data from server."))
            every { boundResource.getStorageData() } returns storageDataMock
            every { boundResource.isNeedRefresh(any()) } returns true
            coEvery { boundResource.getApiCallAsync().await() } coAnswers {
                delay(50)
                Response.success(com.netchar.remote.enums.HttpStatusCode.NO_CONTENT.code, "")
            }

            // act
            launchAndObserve()

            // assert
            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(referenceSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(any()) }
            coVerify { boundResource.getApiCallAsync() }
            verify(exactly = 0) { boundResource.saveRemoteDataInStorage(storageDataMock) }
        }
    }

    @Test
    fun `when no network detected return NO_INTERNET_CONNECTION error resource`() {
        // arrange
        every { boundResource.getApiCallAsync() } returns mockk()
        coEvery { boundResource.getApiCallAsync().await() } throws NoNetworkException("No internet connection.")
        every { boundResource.getStorageData() } returns storageDataMock
        every { boundResource.isNeedRefresh(any()) } returns true

        // act
        runBlocking {
            boundResource.launchIn(this).getLiveData().observeForever(observerMock)
        }

        verify { observerMock.onChanged(Resource.Error(Cause.NO_INTERNET_CONNECTION)) }

        // assert
        confirmVerified(observerMock)
    }

    @Test
    fun `when network throws IO exception return UNEXPECTED error resource`() {
        runBlocking {
            // arrange
            val referenceSet = linkedSetOf<Resource<String>>(Resource.Loading(true), Resource.Loading(false), Resource.Error(Cause.UNEXPECTED, "Parsing exception"))
            every { boundResource.getStorageData() } returns storageDataMock
            every { boundResource.isNeedRefresh(any()) } returns true
            coEvery { boundResource.getApiCallAsync().await() } coAnswers {
                delay(50)
                throw IOException("Parsing exception")
            }

            // act
            launchAndObserve()

            // assert

            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(referenceSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(any()) }
            verify { boundResource.getApiCallAsync() }
            verify(exactly = 0) { boundResource.saveRemoteDataInStorage(any()) }
        }
    }

    @Test
    fun `when cancel scope skip processing coroutines logic and set state of job to completed`() {
        runBlocking {
            // arrange

            val scope = CoroutineScope(mockDispatchers.main)
            every { boundResource.getStorageData() } returns "invalidated data"
            every { boundResource.isNeedRefresh(any()) } returns true
            coEvery { boundResource.getApiCallAsync().await() } coAnswers {
                delay(500)
                successApiResponseMock
            }

            boundResource.launchIn(scope).getLiveData().observeForever(observer)

            scope.cancel()

            assertAll("After Job cancelled", {
                assertFalse("Is Job Active") { boundResource.job.isActive }
                assertTrue("Is Job Cancelled") { boundResource.job.isCancelled }
                assertTrue("Is Job Completed") { boundResource.job.isCompleted }
            })

            assertTrue("LiveData shouldn't emit all values after cancel") { responseSet.size < 2 }
        }
    }

    @Test
    fun `when cancel job state of this job became completed`() {
        runBlocking {
            // arrange
            val scope = CoroutineScope(mockDispatchers.main)
            every { boundResource.getStorageData() } returns "invalidated data"
            every { boundResource.isNeedRefresh(any()) } returns true
            coEvery { boundResource.getApiCallAsync().await() } coAnswers {
                delay(500)
                successApiResponseMock
            }

            boundResource.launchIn(scope).getLiveData().observeForever(observer)

            boundResource.cancelJob()

            assertAll("After Job cancelled", {
                assertFalse("Is Job Active") { boundResource.job.isActive }
                assertTrue("Is Job Cancelled") { boundResource.job.isCancelled }
                assertTrue("Is Job Completed") { boundResource.job.isCompleted }
            })

            assertTrue("LiveData shouldn't emit all values after cancel") { responseSet.size < 2 }
        }
    }

    private suspend fun CoroutineScope.launchAndObserve() {
        boundResource.launchIn(this).getLiveData().observeForever(observer)
        boundResource.job.join()
    }

    private fun getMockErrorResponse(status: com.netchar.remote.enums.HttpStatusCode): Response<String> {
        return mockk(relaxed = true) {
            every { isSuccessful } returns false
            every { code() } returns status.code
            every { errorBody() } returns null
        }
    }
}