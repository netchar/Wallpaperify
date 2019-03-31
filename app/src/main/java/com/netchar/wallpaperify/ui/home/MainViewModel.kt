package com.netchar.wallpaperify.ui.home

import android.arch.lifecycle.*
import com.netchar.wallpaperify.data.models.PhotosApiRequest
import com.netchar.wallpaperify.data.remote.api.PhotosApi
import com.netchar.wallpaperify.data.remote.dto.Photo
import com.netchar.wallpaperify.data.repository.IPhotosRepository
import com.netchar.wallpaperify.data.models.Resource
import com.netchar.wallpaperify.data.repository.IBoundResource
import com.netchar.wallpaperify.infrastructure.AbsentLiveData
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import kotlinx.coroutines.*
import javax.inject.Inject


class MainViewModel @Inject constructor(
        private val repository: IPhotosRepository,
        private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(job + dispatchers.main)
    private val photosRequest = MutableLiveData<PhotosApiRequest>().apply { value = PhotosApiRequest() }
    private lateinit var photosBoundResult: IBoundResource<List<Photo>>

    val photos: LiveData<Resource<List<Photo>>> = Transformations.switchMap(photosRequest) { request ->
        if (request == null) {
            AbsentLiveData.create()
        } else {
            photosBoundResult = repository.getPhotos(request.page, request.perPage, request.orderBy, scope)
            photosBoundResult.getLiveData()
        }
    }

    fun cancelFetchingPhotos() = photosBoundResult.cancelJob()

    fun requestPhotos(page: Int = 0, perPage: Int = 30, orderBy: String = PhotosApi.LATEST) {
        photosRequest.value = PhotosApiRequest(page, perPage, orderBy)
    }

    override fun onCleared() {
        super.onCleared()
        scope.coroutineContext.cancel()
    }
}
