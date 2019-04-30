package com.netchar.wallpaperify.ui.latest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.SingleLiveData
import com.netchar.models.Photo
import com.netchar.models.apirequest.PhotosRequest
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.models.uimodel.Message
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.repository.IPhotosRepository
import com.netchar.wallpaperify.R
import javax.inject.Inject

/**
 * Created by Netchar on 26.04.2019.
 * e.glushankov@gmail.com
 */

class LatestViewModel @Inject constructor(
        private val repository: IPhotosRepository,
        dispatchers: CoroutineDispatchers
) : BaseViewModel(dispatchers) {

    private var page = 1
    private val _errorPlaceholder = SingleLiveData<ErrorMessage>()
    private val _toast = SingleLiveData<Message>()
    private val _error = SingleLiveData<ErrorMessage>()
    private val _refreshing = SingleLiveData<Boolean>()
    private val _ordering = MutableLiveData<String>()
    private val _photos = MediatorLiveData<List<Photo>>().apply {
        addSource(_ordering) { orderBy ->
            fetchPhotos(PhotosRequest(1, PhotosRequest.ITEMS_PER_PAGE, orderBy))
        }
    }

    init {
        page = 1
        fetchPhotos(PhotosRequest(1, PhotosRequest.ITEMS_PER_PAGE, orderingOrDefault()))
    }


    fun refresh() {
        page = 1
        fetchPhotos(PhotosRequest(1, PhotosRequest.ITEMS_PER_PAGE, orderingOrDefault()))
    }

    fun loadMore() {
        fetchPhotos(PhotosRequest(++page, PhotosRequest.ITEMS_PER_PAGE, orderingOrDefault()))
    }

    fun orderBy(ordering: String) {
        _ordering.value = ordering
    }

    val photos: LiveData<List<Photo>> get() = _photos
    val refreshing: LiveData<Boolean> get() = _refreshing
    val error: LiveData<ErrorMessage> get() = _error
    val toast: LiveData<Message> get() = _toast
    val errorPlaceholder: LiveData<ErrorMessage> = _errorPlaceholder

    private fun fetchPhotos(request: PhotosRequest) {
        hideError()

        val repoLiveData = repository.getPhotos(request, scope).getLiveData()
        _photos.removeSource(repoLiveData)
        _photos.addSource(repoLiveData) { response ->
            val isRefreshing = request.page <= 1
            if (isRefreshing) {
                onFreshFetch(response)
            } else {
                onFetchNextPage(response)
            }
        }
    }

    private fun onFreshFetch(response: Resource<List<Photo>>) {
        when (response) {
            is Resource.Success -> {
                _photos.value = response.data
                _toast.value = Message(R.string.latest_message_data_updated)
            }
            is Resource.Loading -> _refreshing.value = response.isLoading
            is Resource.Error -> {
                _refreshing.value = false
                raiseErrorMessage(response)
            }
        }
    }

    private fun onFetchNextPage(response: Resource<List<Photo>>) {
        when (response) {
            is Resource.Success -> _photos.apply { value = value?.plus(response.data) }
            is Resource.Error -> {
                if (page > 1) {
                    page--
                }
                raiseErrorMessage(response)
            }
        }
    }

    private fun hideError() {
        _errorPlaceholder.value = ErrorMessage.empty()
    }

    private fun raiseErrorMessage(response: Resource.Error) {
        val errorMessage = when (response.cause) {
            Cause.NO_INTERNET_CONNECTION -> ErrorMessage(true, Message(R.string.error_message_no_internet), R.drawable.ic_no_internet_connection)
            Cause.NOT_AUTHENTICATED, Cause.UNEXPECTED -> ErrorMessage(true, Message(R.string.error_message_try_again_later), R.drawable.img_unexpected_error)
        }

        if (_photos.value == null) {
            _errorPlaceholder.value = errorMessage
        } else {
            _error.value = errorMessage
        }
    }

    private fun orderingOrDefault() = _ordering.value ?: PhotosRequest.LATEST
}
