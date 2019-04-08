package com.netchar.wallpaperify.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.data.models.Cause
import com.netchar.wallpaperify.data.models.Resource
import com.netchar.wallpaperify.data.remote.HttpStatusCode
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import com.netchar.wallpaperify.infrastructure.utils.Connectivity
import com.netchar.wallpaperify.testutils.InstantTaskExecutorExtension
import io.mockk.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.Response
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@ExperimentalCoroutinesApi
@ExtendWith(InstantTaskExecutorExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
internal class BoundResourceTest {

    private val mockDispatchers = mockk<CoroutineDispatchers> {
        every { database } returns Dispatchers.Unconfined
        every { default } returns Dispatchers.Unconfined
        every { disk } returns Dispatchers.Unconfined
        every { main } returns Dispatchers.Unconfined
        every { network } returns Dispatchers.Unconfined
    }

    private val mockContext = mockk<Context>()

    private val boundResourceMock = object : BoundResource<String>(mockDispatchers, mockContext) {
        override fun saveRemoteDataInStorage(data: String?) {
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
    }

    @BeforeEach
    fun setUp() {
        mockkObject(Connectivity)
        every { Connectivity.isInternetAvailable(any()) } returns true
        every { mockContext.getString(R.string.no_internet_connection_message) } returns "Please check that you have an Internet connection and try again."
    }

    @Test
    fun `getLiveData() always has instance`() {
        runBlocking {
            assertNotNull(boundResourceMock.getLiveData())
        }
    }

    @Test
    fun `launchIn should always return non-null instances`() {
        runBlocking {
            // arrange
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns null
                coEvery { apiRequestAsync() } returns mockk(relaxed = true)
                coEvery { apiRequestAsync().await() } returns Response.success("success")
            }

            // act
            val boundResponse: IBoundResource<String>?
            boundResponse = testResource.launchIn(this)

            // assert
            assertNotNull(boundResponse)
            assertNotNull(boundResponse.getLiveData().value)
        }
    }

    @Test
    fun `when database data valid should return database data`() {
        runBlocking {
            // arrange
            val expectedLiveData = spyk<LiveData<Resource<String>>> {
                every { value } returns Resource.Success("Mock data")
            }
            val databaseDataMock = "Mock data"
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns databaseDataMock
                every { isNeedRefresh(any()) } returns false
            }

            // act
            val boundResponse = testResource.launchIn(this)
            val liveData = boundResponse.getLiveData()

            // assert
            assertEquals(expectedLiveData.value, liveData.value)
        }
    }

    @Test
    fun `when database data is null return network data`() {
        runBlocking {
            // arrange
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns null
                coEvery { apiRequestAsync().await() } coAnswers {
                    delay(300)
                    Response.success("success")
                }
            }

            // act
            val liveData = testResource.launchIn(this).getLiveData()
            liveData.observeForever { responseSet.add(it) }
            testResource.job.join()

            // assert
            assertAll("LiveData should emit Resource value in strict order", {
                assertEquals(Resource.Loading(true), responseSet.elementAt(0))
                assertEquals(Resource.Loading(false), responseSet.elementAt(1))
                assertEquals(Resource.Success("success"), responseSet.elementAt(2))
            })
        }
    }

    @Test
    fun `when database data is not null but data is invalidated return network data`() {
        runBlocking {
            // arrange
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns "invalidated data"
                every { isNeedRefresh(any()) } returns true
                coEvery { apiRequestAsync().await() } coAnswers {
                    delay(300)
                    Response.success("success")
                }
            }

            // act
            val liveData = testResource.launchIn(this).getLiveData()
            liveData.observeForever { responseSet.add(it) }
            testResource.job.join()

            // assert
            assertAll("LiveData should emit Resource value in strict order", {
                assertEquals(Resource.Loading(true), responseSet.elementAt(0))
                assertEquals(Resource.Loading(false), responseSet.elementAt(1))
                assertEquals(Resource.Success("success"), responseSet.elementAt(2))
            })
        }
    }

    @Test
    fun `when getStorageData throws an exception return error resource`() {
        runBlocking {
            // arrange
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } throws IOException("Unable to get data")
                every { isNeedRefresh(any()) } returns true
            }

            // act
            val liveData = testResource.launchIn(this).getLiveData()
            liveData.observeForever { responseSet.add(it) }
            testResource.job.join()

            // assert
            assertEquals(Resource.Error(Cause.UNEXPECTED, "Unable to get data"), responseSet.elementAt(0))
        }
    }

    @Test
    fun `when isNeedRefresh throws an exception return error resource`() {
        runBlocking {
            // arrange
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns ""
                every { isNeedRefresh(any()) } throws IOException("Unable to get data")
            }

            // act
            val liveData = testResource.launchIn(this).getLiveData()
            liveData.observeForever { responseSet.add(it) }
            testResource.job.join()

            // assert
            assertEquals(Resource.Error(Cause.UNEXPECTED, "Unable to get data"), responseSet.elementAt(0))
        }
    }

    @Test
    fun `when saveRemoteDataInStorage throws an exception return error resource`() {
        runBlocking {
            // arrange
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                coEvery { apiRequestAsync().await() } coAnswers {
                    delay(500)
                    Response.success("success")
                }
                every { saveRemoteDataInStorage(any()) } throws IOException("Unable to get data")
            }

            // act
            val liveData = testResource.launchIn(this).getLiveData()
            liveData.observeForever { responseSet.add(it) }
            testResource.job.join()

            // assert
            assertAll("LiveData should emit Resource value in strict order", {
                assertEquals(Resource.Loading(true), responseSet.elementAt(0))
                assertEquals(Resource.Loading(false), responseSet.elementAt(1))
                assertEquals(Resource.Error(Cause.UNEXPECTED, "Unable to get data"), responseSet.elementAt(2))
            })
        }
    }

    @Test
    fun `when network response error return UNEXPECTED error resource`() {
        runBlocking {
            // arrange
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns "invalidated data"
                every { isNeedRefresh(any()) } returns true
                coEvery { apiRequestAsync().await() } coAnswers {
                    delay(500)
                    mockk(relaxed = true) {
                        every { isSuccessful } returns false
                        every { code() } returns HttpStatusCode.INTERNAL_SERVER_ERROR.code
                        every { errorBody() } returns null
                    }
                }
            }

            // act
            val liveData = testResource.launchIn(this).getLiveData()
            liveData.observeForever { responseSet.add(it) }
            testResource.job.join()

            // assert
            assertAll("LiveData should emit Resource value in strict order", {
                assertEquals(Resource.Loading(true), responseSet.elementAt(0))
                assertEquals(Resource.Loading(false), responseSet.elementAt(1))
                assertEquals(Resource.Error(Cause.UNEXPECTED, HttpStatusCode.INTERNAL_SERVER_ERROR.description), responseSet.elementAt(2))
            })
        }
    }

    @Test
    fun `when network response UNAUTHORIZED return NOT_AUTHENTICATED error resource`() {
        runBlocking {
            // arrange
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns "invalidated data"
                every { isNeedRefresh(any()) } returns true
                coEvery { apiRequestAsync().await() } coAnswers {
                    delay(500)
                    mockk(relaxed = true) {
                        every { isSuccessful } returns false
                        every { code() } returns HttpStatusCode.UNAUTHORIZED.code
                        every { errorBody() } returns null
                    }
                }
            }

            // act
            val liveData = testResource.launchIn(this).getLiveData()
            liveData.observeForever { responseSet.add(it) }
            testResource.job.join()

            // assert
            assertAll("LiveData should emit Resource value in strict order", {
                assertEquals(Resource.Loading(true), responseSet.elementAt(0))
                assertEquals(Resource.Loading(false), responseSet.elementAt(1))
                assertEquals(Resource.Error(Cause.NOT_AUTHENTICATED, HttpStatusCode.UNAUTHORIZED.description), responseSet.elementAt(2))
            })
        }
    }

    @Test
    fun `when body is empty return UNEXPECTED error resource`() {
        runBlocking {
            // arrange
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns "invalidated data"
                every { isNeedRefresh(any()) } returns true
                coEvery { apiRequestAsync().await() } coAnswers {
                    delay(500)
                    Response.success(HttpStatusCode.NO_CONTENT.code, "")
                }
            }

            // act
            val liveData = testResource.launchIn(this).getLiveData()
            liveData.observeForever {
                responseSet.add(it)
            }
            testResource.job.join()

            // assert
            assertAll("LiveData should emit Resource value in strict order", {
                assertEquals(Resource.Loading(true), responseSet.elementAt(0))
                assertEquals(Resource.Loading(false), responseSet.elementAt(1))
                assertEquals(Resource.Error(Cause.UNEXPECTED, "Error during fetching data from server."), responseSet.elementAt(2))
            })
        }
    }

    @Test
    fun `when no network detected return NO_INTERNET_CONNECTION error resource`() {
        runBlocking {
            // arrange
            val responseSet = LinkedHashSet<Resource<String>>()
            every { Connectivity.isInternetAvailable(any()) } returns false
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns "invalidated data"
                every { isNeedRefresh(any()) } returns true
            }

            // act
            val liveData = testResource.launchIn(this).getLiveData()
            liveData.observeForever {
                responseSet.add(it)
            }
            testResource.job.join()

            // assert
            assertEquals(Resource.Error(Cause.NO_INTERNET_CONNECTION, mockContext.getString(R.string.no_internet_connection_message)), responseSet.elementAt(0))
        }
    }

    @Test
    fun `when network throws IO exception return UNEXPECTED error resource`() {
        runBlocking {
            // arrange
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns "invalidated data"
                every { isNeedRefresh(any()) } returns true
                coEvery { apiRequestAsync().await() } coAnswers {
                    delay(500)
                    throw IOException("Parsing exception")
                }
            }

            // act
            val liveData = testResource.launchIn(this).getLiveData()
            liveData.observeForever {
                responseSet.add(it)
            }
            testResource.job.join()

            // assert
            assertAll("LiveData should emit Resource value in strict order", {
                assertEquals(3, responseSet.size)
                assertEquals(Resource.Loading(true), responseSet.elementAt(0))
                assertEquals(Resource.Loading(false), responseSet.elementAt(1))
                assertEquals(Resource.Error(Cause.UNEXPECTED, "Parsing exception"), responseSet.elementAt(2))
            })
        }
    }

    @Test
    fun `when cancel scope skip processing coroutines logic and set state of job to completed`() {
        runBlocking {
            // arrange
            val scope = CoroutineScope(mockDispatchers.main)
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns "invalidated data"
                every { isNeedRefresh(any()) } returns true
                coEvery { apiRequestAsync().await() } coAnswers {
                    delay(1000)
                    Response.success("success")
                }
            }


            val liveData = testResource.launchIn(scope).getLiveData()
            liveData.observeForever {
                responseSet.add(it)
            }

            delay(500)

            assertAll("Before Job cancelled", {
                assertTrue("Is Job Active") { testResource.job.isActive }
                assertFalse("Is Job Cancelled") { testResource.job.isCancelled }
                assertFalse("Is Job Completed") { testResource.job.isCompleted }
            })

            scope.cancel()

            delay(1000)

            assertAll("After Job cancelled", {
                assertFalse("Is Job Active") { testResource.job.isActive }
                assertTrue("Is Job Cancelled") { testResource.job.isCancelled }
                assertTrue("Is Job Completed") { testResource.job.isCompleted }
            })

            assertTrue("LiveData shouldn't emit all values after cancel") { responseSet.size < 2 }
        }
    }

    @Test
    fun `when cancel job state of this job became completed`() {
        runBlocking {
            // arrange
            val scope = CoroutineScope(mockDispatchers.main)
            val responseSet = LinkedHashSet<Resource<String>>()
            val testResource: BoundResource<String> = spyk(boundResourceMock) {
                every { getStorageData() } returns "invalidated data"
                every { isNeedRefresh(any()) } returns true
                coEvery { apiRequestAsync().await() } coAnswers {
                    delay(1000)
                    Response.success("success")
                }
            }


            val liveData = testResource.launchIn(scope).getLiveData()
            liveData.observeForever {
                responseSet.add(it)
            }

            delay(500)

            assertAll("Before Job cancelled", {
                assertTrue("Is Job Active") { testResource.job.isActive }
                assertFalse("Is Job Cancelled") { testResource.job.isCancelled }
                assertFalse("Is Job Completed") { testResource.job.isCompleted }
            })

            testResource.cancelJob()

            delay(1000)

            assertAll("After Job cancelled", {
                assertFalse("Is Job Active") { testResource.job.isActive }
                assertTrue("Is Job Cancelled") { testResource.job.isCancelled }
                assertTrue("Is Job Completed") { testResource.job.isCompleted }
            })

            assertTrue("LiveData shouldn't emit all values after cancel") { responseSet.size < 2 }
        }
    }
}