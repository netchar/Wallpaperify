package com.netchar.wallpaperify.ui.photosdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.models.Photo
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.models.uimodel.Message
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.repository.photos.IPhotosRepository
import com.netchar.wallpaperify.R
import javax.inject.Inject

class PhotoDetailsViewModel @Inject constructor(
        coroutineDispatchers: CoroutineDispatchers,
        private val repo: IPhotosRepository

) : BaseViewModel(coroutineDispatchers) {

    private val _photoId = MutableLiveData<String>()
    private val _photo = MediatorLiveData<Photo>()
    private val _loading = MutableLiveData<Boolean>()
    private val _error = MutableLiveData<Message>()

    private val repoLiveData: LiveData<Resource<Photo>> = Transformations.switchMap(_photoId) { id ->
        repo.getPhoto(id, scope).getLiveData()
    }

    init {
        _photo.addSource(repoLiveData) { response ->
            proceedResponse(response)
        }
    }

    private fun proceedResponse(response: Resource<Photo>?) {

        when (response) {
            is Resource.Success -> {
                _photo.value = response.data
            }
            is Resource.Loading -> {
                _loading.value = response.isLoading
            }
            is Resource.Error -> {
                _error.value = getErrorMessage(response)
            }
        }
    }

    fun fetchPhoto(id: String) {
        _photoId.value = id
    }

    val photo: LiveData<Photo> get() = _photo

    val error: LiveData<Message> get() = _error

    val loading: LiveData<Boolean> get() = _loading

    private fun getErrorMessage(response: Resource.Error): Message {
        return when (response.cause) {
            Cause.NO_INTERNET_CONNECTION -> Message(R.string.error_message_no_internet)
            Cause.NOT_AUTHENTICATED, Cause.UNEXPECTED -> Message(R.string.error_message_try_again_later)
        }
    }
}