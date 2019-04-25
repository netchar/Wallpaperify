package com.netchar.wallpaperify.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.wallpaperify.R
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.Connectivity
import com.netchar.wallpaperify.testutils.InstantTaskExecutorExtension
import io.mockk.*
import kotlinx.coroutines.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
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

    private val mockContext = mockk<Context>()
    private val successApiResponseMock = Response.success("success")
    private val storageDataMock = "Mock database data"
    private lateinit var boundResource: BoundResource<String>
    private val responseSet = LinkedHashSet<Resource<String>>()
    private val successReferenceResponseSet: LinkedHashSet<Resource<String>> = linkedSetOf(Resource.Loading(true), Resource.Loading(false), Resource.Success(successApiResponseMock.body()!!))
    private val observer = Observer<Resource<String>> { responseSet.add(it) }

    @BeforeEach
    fun setUp() {
        mockkObject(Connectivity)
        every { Connectivity.isInternetAvailable(any()) } returns true
        every { mockContext.getString(R.string.error_message_no_internet) } returns "Please check that you have an Internet connection and try again."

        boundResource = spyk(object : BoundResource<String>(mockDispatchers, mockContext) {
            override fun saveRemoteDataInStorage(data: String) {
            }

            override fun getStorageData(): String? {
                return null
            }

            override fun isNeedRefresh(localData: String): Boolean {
                return true
            }

            override fun apiRequestAsync(): Deferred<Response<String>> {
                return spyk()
            }
        })
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
            coEvery { boundResource.apiRequestAsync().await() } returns successApiResponseMock

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
            coEvery { boundResource.apiRequestAsync().await() } coAnswers {
                delay(50)
                successApiResponseMock
            }

            // act
            launchAndObserve()

            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(successReferenceResponseSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.apiRequestAsync() }
            verify { boundResource.saveRemoteDataInStorage(successApiResponseMock.body()!!) }
        }
    }

    @Test
    fun `when storage data is not null but invalidated return success resource with network data`() {
        runBlocking {
            // arrange
            every { boundResource.getStorageData() } returns storageDataMock
            every { boundResource.isNeedRefresh(any()) } returns true
            coEvery { boundResource.apiRequestAsync().await() } coAnswers {
                delay(50)
                successApiResponseMock
            }

            // act
            launchAndObserve()

            // assert
            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(successReferenceResponseSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(storageDataMock) }
            verify { boundResource.apiRequestAsync() }
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
            coEvery { boundResource.apiRequestAsync().await() } coAnswers {
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
            coEvery { boundResource.apiRequestAsync().await() } coAnswers {
                delay(50)
                getMockErrorResponse(com.netchar.remote.enums.HttpStatusCode.INTERNAL_SERVER_ERROR)
            }

            // act
            launchAndObserve()

            // assert
            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(referenceSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(storageDataMock) }
            verify { boundResource.apiRequestAsync() }
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
            coEvery { boundResource.apiRequestAsync().await() } coAnswers {
                delay(50)
                getMockErrorResponse(com.netchar.remote.enums.HttpStatusCode.UNAUTHORIZED)
            }

            // act
            launchAndObserve()

            // assert
            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(referenceSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(any()) }
            verify { boundResource.apiRequestAsync() }
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
            coEvery { boundResource.apiRequestAsync().await() } coAnswers {
                delay(50)
                Response.success(com.netchar.remote.enums.HttpStatusCode.NO_CONTENT.code, "")
            }

            // act
            launchAndObserve()

            // assert
            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(referenceSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(any()) }
            coVerify { boundResource.apiRequestAsync() }
            verify(exactly = 0) { boundResource.saveRemoteDataInStorage(storageDataMock) }
        }
    }


    @Test
    fun `when no network detected return NO_INTERNET_CONNECTION error resource`() {
        runBlocking {
            // arrange
            every { Connectivity.isInternetAvailable(any()) } returns false
            every { boundResource.getStorageData() } returns storageDataMock
            every { boundResource.isNeedRefresh(any()) } returns true

            // act
            launchAndObserve()

            // assert
            assertThat(responseSet, allOf(hasItem(Resource.Error(Cause.NO_INTERNET_CONNECTION, mockContext.getString(R.string.error_message_no_internet))), hasSize(equalTo(1))))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(storageDataMock) }
            verify(exactly = 0) { boundResource.apiRequestAsync() }
        }
    }

    @Test
    fun `when network throws IO exception return UNEXPECTED error resource`() {
        runBlocking {
            // arrange
            val referenceSet = linkedSetOf<Resource<String>>(Resource.Loading(true), Resource.Loading(false), Resource.Error(Cause.UNEXPECTED, "Parsing exception"))
            every { boundResource.getStorageData() } returns storageDataMock
            every { boundResource.isNeedRefresh(any()) } returns true
            coEvery { boundResource.apiRequestAsync().await() } coAnswers {
                delay(50)
                throw IOException("Parsing exception")
            }

            // act
            launchAndObserve()

            // assert

            assertThat(ASSERT_LIVE_DATA_IN_ORDER, responseSet, equalTo(referenceSet))

            verify { boundResource.getStorageData() }
            verify { boundResource.isNeedRefresh(any()) }
            verify { boundResource.apiRequestAsync() }
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
            coEvery { boundResource.apiRequestAsync().await() } coAnswers {
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
            coEvery { boundResource.apiRequestAsync().await() } coAnswers {
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