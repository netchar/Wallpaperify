package com.netchar.wallpaperify.ui.latest

import androidx.lifecycle.Observer
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.models.Photo
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.models.uimodel.Message
import com.netchar.repository.photos.PhotosRepository
import com.netchar.wallpaperify.ui.InstantTaskExecutorExtension
import com.netchar.wallpaperify.ui.di.TestAppComponent
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.HttpURLConnection
import javax.inject.Inject

private const val PHOTOS_JSON_FILE_NAME = "photos.json"
private const val PHOTOS_LOAD_MORE_JSON_FILE_NAME = "photos_load_more.json"

/**
 * Created by Netchar on 02.05.2019.
 * e.glushankov@gmail.com
 */
@ExtendWith(InstantTaskExecutorExtension::class)
class LatestViewModelTest : BaseMockServerTest() {

    override fun onSetupDaggerComponent(component: TestAppComponent) {
        component.inject(this)
    }

    private lateinit var photosObserver: Observer<List<Photo>>
    private lateinit var refreshingObserver: Observer<Boolean>
    private lateinit var errorObserver: Observer<ErrorMessage>
    private lateinit var toastObserver: Observer<Message>
    private lateinit var errorPlaceholderObserver: Observer<ErrorMessage>

    @BeforeEach
    fun setUpBeforeEach() {
        photosObserver = mockk(relaxed = true)
        refreshingObserver = mockk(relaxed = true)
        errorObserver = mockk(relaxed = true)
        toastObserver = mockk(relaxed = true)
        errorPlaceholderObserver = mockk(relaxed = true)
    }

    @Inject
    lateinit var repository: PhotosRepository

    @Inject
    lateinit var dispatchers: CoroutineDispatchers

    @Inject
    lateinit var moshi: Moshi
//
//    val adapter by lazy {
//        moshi.adapter<List<Photo>>(Types.newParameterizedType(List::class.java, Photo::class.java))
//    }
//
//    val expectedFreshResult by lazy {
//        adapter.fromJson(getJson(PHOTOS_JSON_FILE_NAME))
//    }
//    val expectedLoadMoreFreshResult by lazy {
//        adapter.fromJson(getJson(PHOTOS_LOAD_MORE_JS
//
//
//
//
//
//        ON_FILE_NAME))
//    }

    @Test
    fun `On Init photos LiveData should emit network data response`() {
        val adapter = moshi.adapter<List<Photo>>(Types.newParameterizedType(List::class.java, Photo::class.java))
        val expectedFreshResult = adapter.fromJson(getJson(PHOTOS_JSON_FILE_NAME))

        mockHttpResponse(PHOTOS_JSON_FILE_NAME, HttpURLConnection.HTTP_OK)

        runBlocking {
            val latestViewModel = LatestViewModel(repository, dispatchers)
            latestViewModel.photos.observeForever(photosObserver)
        }

        verify {
            photosObserver.onChanged(expectedFreshResult)
        }

        confirmVerified(photosObserver)
    }

    @Test
    fun `On Init should emit loading LiveData`() {
        mockHttpResponse(PHOTOS_JSON_FILE_NAME, HttpURLConnection.HTTP_OK)

        runBlocking {
            val latestViewModel = LatestViewModel(repository, dispatchers)
            latestViewModel.photos.observeForever(photosObserver)
            latestViewModel.refreshing.observeForever(refreshingObserver)
        }

        verifyOrder {
            refreshingObserver.onChanged(true)
            refreshingObserver.onChanged(false)
        }

        confirmVerified(errorObserver)
    }

    @Test
    fun `On Init should return error when bad response`() {
        mockHttpResponse(PHOTOS_JSON_FILE_NAME, HttpURLConnection.HTTP_INTERNAL_ERROR)

        runBlocking {
            val latestViewModel = LatestViewModel(repository, dispatchers)
            latestViewModel.photos.observeForever(photosObserver)
            latestViewModel.errorPlaceholder.observeForever(errorPlaceholderObserver)
            latestViewModel.error.observeForever(errorObserver)
        }

        verifyOrder {
            errorPlaceholderObserver.onChanged(ErrorMessage.empty())
            errorPlaceholderObserver.onChanged(ofType(ErrorMessage::class))
        }

        verifyAll {
            errorObserver wasNot Called
            photosObserver wasNot Called
        }

        confirmVerified(errorPlaceholderObserver, errorObserver, photosObserver)
    }

    @Test
    fun `On success refresh should fetch fresh data`() {
        val adapter = moshi.adapter<List<Photo>>(Types.newParameterizedType(List::class.java, Photo::class.java))
        val expectedLoadMoreFreshResult = adapter.fromJson(getJson(PHOTOS_JSON_FILE_NAME))


//        mockHttpResponse(PHOTOS_LOAD_MORE_JSON_FILE_NAME, HttpURLConnection.HTTP_OK)
        val latestViewModel = LatestViewModel(repository, dispatchers)

        runBlocking {
            mockHttpResponse(PHOTOS_LOAD_MORE_JSON_FILE_NAME, HttpURLConnection.HTTP_OK)
            latestViewModel.refresh()
            latestViewModel.photos.observeForever(photosObserver)
        }

        verify {
            photosObserver.onChanged(expectedLoadMoreFreshResult)
        }

        confirmVerified(photosObserver)
    }

    @Test
    fun refresh() {
    }

    @Test
    fun loadMore() {
    }

    @Test
    fun orderBy() {
    }
}