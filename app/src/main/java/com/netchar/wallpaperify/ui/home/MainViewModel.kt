package com.netchar.wallpaperify.ui.home

import android.arch.lifecycle.*
import com.netchar.wallpaperify.data.models.PhotosApiRequest
import com.netchar.wallpaperify.data.remote.api.PhotosApi
import com.netchar.wallpaperify.data.remote.dto.Photo
import com.netchar.wallpaperify.data.repositories.IPhotosRepository
import com.netchar.wallpaperify.data.models.Resource
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import kotlinx.coroutines.*
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val repository: IPhotosRepository,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val job = Job()
    private val coroutineContext = job + dispatchers.main
    private val scope = CoroutineScope(coroutineContext)
    private val photosRequest = MutableLiveData<PhotosApiRequest>().apply { value = PhotosApiRequest() }

//    init {
//        requestPhotos()
//    }

    val photos: LiveData<Resource<List<Photo>>> = Transformations.switchMap(photosRequest) { request ->
        repository.getPhotosAsync(request.page, request.perPage, request.orderBy, scope)
    }


    fun requestPhotos(page: Int = 0, perPage: Int = 30, orderBy: String = PhotosApi.LATEST) {
        photosRequest.value = PhotosApiRequest(page, perPage, orderBy)
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }
}
