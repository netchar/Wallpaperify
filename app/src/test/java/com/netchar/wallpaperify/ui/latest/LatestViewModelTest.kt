package com.netchar.wallpaperify.ui.latest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.models.Photo
import com.netchar.models.apirequest.OLDEST
import com.netchar.models.apirequest.Ordering
import com.netchar.models.apirequest.Paging
import com.netchar.models.apirequest.PhotosRequest
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.models.uimodel.Message
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.repository.photos.IPhotosRepository
import com.netchar.wallpaperify.ui.InstantTaskExecutorExtension
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by Netchar on 02.05.2019.
 * e.glushankov@gmail.com
 */
@ExtendWith(InstantTaskExecutorExtension::class)
class LatestViewModelTest {

    private lateinit var photosObserver: Observer<List<Photo>>
    private lateinit var refreshingObserver: Observer<Boolean>
    private lateinit var errorObserver: Observer<ErrorMessage>
    private lateinit var toastObserver: Observer<Message>
    private lateinit var errorPlaceholderObserver: Observer<ErrorMessage>
    private lateinit var orderingObserver: Observer<Ordering>
    private lateinit var repo: IPhotosRepository

    private val dispatchersMock = CoroutineDispatchers(
        Dispatchers.Unconfined,
        Dispatchers.Unconfined,
        Dispatchers.Unconfined,
        Dispatchers.Unconfined
    )

    @BeforeEach
    fun onBeforeEach() {
        photosObserver = mockk(relaxed = true)
        refreshingObserver = mockk(relaxed = true)
        errorObserver = mockk(relaxed = true)
        toastObserver = mockk(relaxed = true)
        errorPlaceholderObserver = mockk(relaxed = true)
        orderingObserver = mockk(relaxed = true)
        repo = mockk(relaxed = true)
    }

    private val successValue: MutableList<Photo> = mutableListOf(spyk<Photo>().apply { id = "1" }, spyk<Photo>().apply { id = "2" })
    private val successLoadMoreValue: List<Photo> = listOf(spyk<Photo>().apply { id = "3" }, spyk<Photo>().apply { id = "4" })
    private val errorValue = Cause.UNEXPECTED
    private val loadingStartValue = true
    private val loadingEndValue = false

    private val successResponseMock = MutableLiveData<Resource<List<Photo>>>().apply { value = Resource.Success(successValue) }
    private val successLoadMoreResponseMock = MutableLiveData<Resource<List<Photo>>>().apply { value = Resource.Success(successLoadMoreValue) }
    private val errorResponseMock = MutableLiveData<Resource<List<Photo>>>().apply { value = Resource.Error(errorValue) }
    private val loadingStartResponseMock = MutableLiveData<Resource<List<Photo>>>().apply { value = Resource.Loading(loadingStartValue) }
    private val loadingEndResponseMock = MutableLiveData<Resource<List<Photo>>>().apply { value = Resource.Loading(loadingEndValue) }

    @Test
    fun `On init should start fetching`() {
        every { repo.getPhotos(any(), any()).getLiveData() } returns successResponseMock

        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)

        verify {
            photosObserver.onChanged(successValue)
        }

        confirmVerified(photosObserver)
    }

    @Test
    fun `On init when success fetch should emit toast`() {
        every { repo.getPhotos(any(), any()).getLiveData() } returns successResponseMock

        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.toast.observeForever(toastObserver)

        verify {
            photosObserver.onChanged(successValue)
            toastObserver.onChanged(any())
        }

        confirmVerified(photosObserver, toastObserver)
    }

    @Test
    fun `On init when error should emit error placeholder`() {
        every { repo.getPhotos(any(), any()).getLiveData() } returns errorResponseMock

        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.errorPlaceholder.observeForever(errorPlaceholderObserver)
        latestViewModel.error.observeForever(errorObserver)

        verify {
            errorPlaceholderObserver.onChanged(any())
        }

        verify {
            photosObserver wasNot called
            errorObserver wasNot called
        }

        confirmVerified(photosObserver, errorPlaceholderObserver, errorObserver)
    }

    @Test
    fun `On init when start loading should emit refreshing`() {
        every { repo.getPhotos(any(), any()).getLiveData() } returns loadingStartResponseMock

        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.refreshing.observeForever(refreshingObserver)

        verify {
            refreshingObserver.onChanged(loadingStartValue)
        }

        verify {
            photosObserver wasNot called
        }

        confirmVerified(refreshingObserver)
    }

    @Test
    fun `On init when end loading should emit refreshing`() {
        every { repo.getPhotos(any(), any()).getLiveData() } returns loadingEndResponseMock

        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.refreshing.observeForever(refreshingObserver)

        verify {
            refreshingObserver.onChanged(loadingEndValue)
        }

        verify {
            photosObserver wasNot called
        }

        confirmVerified(refreshingObserver)
    }

    @Test
    fun `On init when error should emit refreshing to false`() {
        every { repo.getPhotos(any(), any()).getLiveData() } returns errorResponseMock

        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.refreshing.observeForever(refreshingObserver)

        verify {
            refreshingObserver.onChanged(loadingEndValue)
        }

        verify {
            photosObserver wasNot called
        }

        confirmVerified(refreshingObserver)
    }

    @Test
    fun `On refresh assume valid request arguments`() {
        val expectedRefreshRequest = PhotosRequest(Paging().fromStart())

        every { repo.getPhotos(any(), any()).getLiveData() } returns successResponseMock andThen successResponseMock

        // act
        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.refresh()

        verifyOrder {
            repo.getPhotos(expectedRefreshRequest, any())
            repo.getPhotos(expectedRefreshRequest, any())
        }

        verify {
            photosObserver.onChanged(successValue)
        }

        confirmVerified(photosObserver, repo)
    }

    @Test
    fun `On refresh when error and previous attempt failed should emit errorPlaceholder LiveData`() {

        every { repo.getPhotos(any(), any()).getLiveData() } returns errorResponseMock

        // act
        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.error.observeForever(errorObserver)
        latestViewModel.errorPlaceholder.observeForever(errorPlaceholderObserver)
        latestViewModel.toast.observeForever(toastObserver)
        latestViewModel.refresh()

        verifyAll {
            errorPlaceholderObserver.onChanged(ofType())
            photosObserver wasNot called
            errorObserver wasNot called
            toastObserver wasNot called
        }

        confirmVerified(photosObserver, errorObserver, errorPlaceholderObserver, toastObserver)
    }

    @Test
    fun `On refresh when error and previous attempt was success should emit error LiveData`() {

        every { repo.getPhotos(any(), any()).getLiveData() } returns successResponseMock andThen errorResponseMock

        // act
        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.error.observeForever(errorObserver)
        latestViewModel.errorPlaceholder.observeForever(errorPlaceholderObserver)
        latestViewModel.toast.observeForever(toastObserver)
        latestViewModel.refresh()

        verifyAll {
            errorObserver.onChanged(ofType())
            photosObserver.onChanged(successValue)
            errorPlaceholderObserver wasNot called
        }

        confirmVerified(photosObserver, errorObserver, errorPlaceholderObserver)
    }

    @Test
    fun `On loadMore assume valid request arguments`() {
        val expectedFreshRequest = PhotosRequest(Paging().fromStart())
        val expectedLoadMoreRequest = PhotosRequest(Paging().nextPage())

        every { repo.getPhotos(any(), any()).getLiveData() } returns successResponseMock andThen successLoadMoreResponseMock

        // act
        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.loadMore()

        verifyOrder {
            repo.getPhotos(expectedFreshRequest, any())
            repo.getPhotos(expectedLoadMoreRequest, any())
        }

        confirmVerified(repo)
    }

    @Test
    fun `On loadMore when success emit previous loaded photos plus photos next page`() {
        every { repo.getPhotos(any(), any()).getLiveData() } returns successResponseMock andThen successLoadMoreResponseMock

        // act
        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.loadMore()

        verifyOrder {
            photosObserver.onChanged(successValue)
            photosObserver.onChanged(successValue + successLoadMoreValue)
        }

        confirmVerified(photosObserver)
    }

    @Test
    fun `On loadMore when error should emit error LiveData`() {
        every { repo.getPhotos(any(), any()).getLiveData() } returns successResponseMock andThen errorResponseMock

        // act
        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.error.observeForever(errorObserver)
        latestViewModel.loadMore()

        verifyOrder {
            photosObserver.onChanged(successValue)
            errorObserver.onChanged(ofType())
        }

        confirmVerified(photosObserver, errorObserver)
    }


    @Test
    fun `On loadMore when retry should run same page`() {
        val freshRequest = PhotosRequest(Paging().fromStart())
        val loadMore1Request = PhotosRequest(Paging(1).nextPage())
        val loadMore2Request = PhotosRequest(Paging(2).nextPage())
        every { repo.getPhotos(any(), any()).getLiveData() } returns successResponseMock andThen successLoadMoreResponseMock andThen errorResponseMock andThen successLoadMoreResponseMock

        // act
        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.error.observeForever(errorObserver)
        latestViewModel.loadMore()
        latestViewModel.loadMore()
        latestViewModel.loadMore()

        verifyOrder {
            repo.getPhotos(freshRequest, any())
            repo.getPhotos(loadMore1Request, any())
            repo.getPhotos(loadMore2Request, any())
            errorObserver.onChanged(ofType())
            repo.getPhotos(loadMore2Request, any())
        }

        confirmVerified(errorObserver, repo)
    }

    @Test
    fun `On orderBy assume valid order parameter`() {
        val expectedOrderByParameter = PhotosRequest(Paging().fromStart(), orderBy = OLDEST)
        every { repo.getPhotos(any(), any()).getLiveData() } returns successResponseMock andThen successResponseMock

        val latestViewModel = LatestViewModel(repo, dispatchersMock)
        latestViewModel.photos.observeForever(photosObserver)
        latestViewModel.ordering.observeForever(orderingObserver)
        latestViewModel.orderBy(Ordering(OLDEST))

        verifyOrder {
            repo.getPhotos(any(), any())
            repo.getPhotos(expectedOrderByParameter, any())
            orderingObserver.onChanged(Ordering(OLDEST))
        }

        confirmVerified(orderingObserver, repo)
    }
}