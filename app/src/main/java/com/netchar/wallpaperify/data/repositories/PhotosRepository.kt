package com.netchar.wallpaperify.data.repositories

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.netchar.wallpaperify.data.models.HttpResult
import com.netchar.wallpaperify.data.remote.api.PhotosApi
import com.netchar.wallpaperify.data.models.dto.Photo
import com.netchar.wallpaperify.infrastructure.extensions.awaitSafe
import javax.inject.Inject

/**
 * Created by Netchar on 21.03.2019.
 * e.glushankov@gmail.com
 */

class PhotosRepository @Inject constructor(
        private val api: PhotosApi
) : IPhotosRepository {

    override suspend fun getPhotos(): LiveData<Resource<List<Photo>>> {
        val data = MutableLiveData<Resource<List<Photo>>>()
        val response = api.getPhotosAsync(1, 30, PhotosApi.LATEST).awaitSafe()
        when (response) {
            is HttpResult.Success -> data.value = Resource.success(response.data)
            is HttpResult.Error -> data.value = Resource.error(response.httpStatusCode.description, null)
            is HttpResult.Exception -> data.value = Resource.error(response.exception.localizedMessage, null)
        }
        return data
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}

