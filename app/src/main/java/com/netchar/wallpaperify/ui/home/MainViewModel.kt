package com.netchar.wallpaperify.ui.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.netchar.wallpaperify.data.models.dto.Photo
import com.netchar.wallpaperify.data.repositories.IPhotosRepository
import com.netchar.wallpaperify.data.repositories.Resource
import com.netchar.wallpaperify.data.repositories.Status
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

    private val photos = MutableLiveData<List<Photo>>()
    private val loading = MutableLiveData<Boolean>()
    private val error = MutableLiveData<String>()

    val onLoading: LiveData<Boolean> = loading
    val onError: LiveData<String> = error

    fun getPhotos(): LiveData<List<Photo>> {
        if (photos.value == null) {
            refreshPhotos()
        }
        return photos
    }

    fun refreshPhotos() {
        scope.launch {
            val result: LiveData<Resource<List<Photo>>> = repository.getPhotos()
            when (result.value?.status) {
                Status.SUCCESS -> photos.value = result.value?.data
                Status.ERROR -> error.value = "Error"
                Status.LOADING -> loading.value = true
                null -> TODO()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }
}
