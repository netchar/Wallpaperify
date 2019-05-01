package com.netchar.repository.photos

import androidx.lifecycle.Observer
import com.netchar.models.Photo
import com.netchar.models.apirequest.PhotosRequest
import com.netchar.remote.Resource
import com.netchar.remote.api.PhotosApi
import com.netchar.remote.enums.HttpResult
import com.netchar.remote.extensions.awaitSafe
import com.netchar.repository.InstantTaskExecutorExtension
import com.netchar.repository.utils.mockDispatchers
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by Netchar on 01.05.2019.
 * e.glushankov@gmail.com
 */
@ExtendWith(InstantTaskExecutorExtension::class)
class PhotosRepositoryTest {

    private val photoApi: PhotosApi = mockk()
    lateinit var repo: PhotosRepository
    private lateinit var observer: Observer<Resource<List<Photo>>>

    @BeforeEach
    fun onBeforeEachTest() {
        observer = mockk(relaxed = true)
        repo = PhotosRepository(photoApi, mockDispatchers)
    }

    @Test
    fun getPhotos() {
        mockkStatic("com.netchar.remote.extensions.RetrofitExtKt")
        every { photoApi.getPhotosAsync(any(), any(), any()) } returns mockk(relaxed = true)
        coEvery { photoApi.getPhotosAsync(any(), any(), any()).awaitSafe() } coAnswers {
            delay(200)
            HttpResult.Success(listOf())
        }

        runBlocking {
            repo.getPhotos(PhotosRequest(), this).getLiveData().observeForever(observer)
        }

        verifyOrder {
            observer.onChanged(Resource.Loading(true))
            observer.onChanged(Resource.Loading(false))
            observer.onChanged(Resource.Success(listOf()))
        }

        confirmVerified(observer)
    }
}