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
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.net.HttpURLConnection
import javax.inject.Inject

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

    @Test
    fun `On Init photos LiveData should emit network data response`() {
        val adapter = moshi.adapter<List<Photo>>(Types.newParameterizedType(List::class.java, Photo::class.java))
        val expectedResult = adapter.fromJson(getJson("photos.json"))

        mockHttpResponse("photos.json", HttpURLConnection.HTTP_OK)

        runBlocking {
            val latestViewModel = LatestViewModel(repository, dispatchers)
            latestViewModel.photos.observeForever(photosObserver)
        }

        verify {
            photosObserver.onChanged(expectedResult)
        }

        confirmVerified(photosObserver)
    }

    @Test
    fun `On Init should emit loading LiveData`() {
        mockHttpResponse("photos.json", HttpURLConnection.HTTP_OK)

        runBlocking {
            val latestViewModel = LatestViewModel(repository, dispatchers)
            latestViewModel.refreshing.observeForever(refreshingObserver)
        }

        verifyOrder {
            refreshingObserver.onChanged(true)
            refreshingObserver.onChanged(false)
        }

        confirmVerified(refreshingObserver)
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