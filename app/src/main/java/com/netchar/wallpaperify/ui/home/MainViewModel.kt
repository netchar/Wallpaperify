package com.netchar.wallpaperify.ui.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.netchar.wallpaperify.data.models.dto.Photo
import com.netchar.wallpaperify.data.repositories.HttpResult
import com.netchar.wallpaperify.data.repositories.IPhotosRepository
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainViewModel @Inject constructor(
    private val repository: IPhotosRepository,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val job = Job()
    private val coroutineContext = job + dispatchers.default
    private val scope = CoroutineScope(coroutineContext)

    private val photos = MutableLiveData<List<Photo>>()

    fun getPhotos(): LiveData<List<Photo>> {
        if (photos.value == null) {
            refreshPhotos()
        }
        return photos
    }

    private fun refreshPhotos() {
        scope.launch {
            val result: HttpResult<List<Photo>> = repository.getPhotos()

//            photos.value = result.
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }
}
