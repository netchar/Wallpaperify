package com.netchar.wallpaperify.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.netchar.remote.Resource
import com.netchar.wallpaperify.data.repository.IBoundResource
import com.netchar.wallpaperify.data.repository.IPhotosRepository
import com.netchar.wallpaperify.data.repository.PhotosRepository
import com.netchar.wallpaperify.infrastructure.AbsentLiveData
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject


class MainViewModel @Inject constructor(
        private val repository: IPhotosRepository,
        private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(job + dispatchers.main)
    private val photosRequest = MutableLiveData<PhotosRepository.PhotosApiRequest>().apply { value = PhotosRepository.PhotosApiRequest() }
    private lateinit var photosBoundResult: IBoundResource<List<com.netchar.models.Photo>>

    val photos: LiveData<Resource<List<com.netchar.models.Photo>>> = Transformations.switchMap(photosRequest) { request ->
        if (request == null) {
            AbsentLiveData.create()
        } else {
            photosBoundResult = repository.getPhotos(request, scope)
            photosBoundResult.getLiveData()
        }
    }

    fun cancelFetchingPhotos() = photosBoundResult.cancelJob()

    fun requestPhotos(page: Int = 0, perPage: Int = 30, orderBy: String = PhotosRepository.PhotosApiRequest.LATEST) {
        photosRequest.value = PhotosRepository.PhotosApiRequest(page, perPage, orderBy)
    }

    override fun onCleared() {
        super.onCleared()
        scope.coroutineContext.cancel()
    }
}
