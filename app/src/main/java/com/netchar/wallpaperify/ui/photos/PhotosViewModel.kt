package com.netchar.wallpaperify.ui.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.SingleLiveData
import com.netchar.models.Photo
import com.netchar.models.apirequest.ApiRequest
import com.netchar.models.apirequest.Paging
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.models.uimodel.Message
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.repository.photos.IPhotosRepository
import com.netchar.wallpaperify.R
import javax.inject.Inject

/**
 * Created by Netchar on 26.04.2019.
 * e.glushankov@gmail.com
 */

class PhotosViewModel @Inject constructor(
        private val repository: IPhotosRepository,
        dispatchers: CoroutineDispatchers
) : BaseViewModel(dispatchers) {

    private val paging = Paging(startPage = 1)
    private val request = MediatorLiveData<ApiRequest.Photos>()

    private val _errorPlaceholder = SingleLiveData<ErrorMessage>()
    private val _toast = SingleLiveData<Message>()
    private val _error = SingleLiveData<ErrorMessage>()
    private val _refreshing = SingleLiveData<Boolean>()
    private val _ordering = MutableLiveData<ApiRequest.Order>()
    private val _photos: MediatorLiveData<List<Photo>> = MediatorLiveData()

    private val repositoryLiveData = Transformations.switchMap(request) { request ->
        repository.getPhotos(request, scope).getLiveData()
    }

    init {
        addMediatorSources()
        requestPhotos(paging.fromStart(), ApiRequest.Order.LATEST)
    }

    val photos: LiveData<List<Photo>> get() = _photos
    val refreshing: LiveData<Boolean> get() = _refreshing
    val error: LiveData<ErrorMessage> get() = _error
    val toast: LiveData<Message> get() = _toast
    val errorPlaceholder: LiveData<ErrorMessage> get() = _errorPlaceholder
    val ordering: LiveData<ApiRequest.Order> get() = _ordering

    fun refresh() {
        requestPhotos(paging.fromStart(), getOrderingOrDefault())
    }

    fun loadMore() {
        requestPhotos(paging.nextPage(), getOrderingOrDefault())
    }

    fun orderBy(ordering: ApiRequest.Order) {
        _ordering.value = ordering
    }

    private fun addMediatorSources() {
        request.addSource(_ordering) { ordering ->
            requestPhotos(paging.fromStart(), ordering)
        }

        _photos.addSource(repositoryLiveData) { response ->
            proceedFetching(response)
        }
    }

    private fun requestPhotos(page: Int, order: ApiRequest.Order) {
        request.value = ApiRequest.Photos(page, order)
    }

    private fun proceedFetching(response: Resource<List<Photo>>) {
        if (needToHidePlaceholder) {
            hidePlaceholderError()
        }

        if (isFreshFetching) {
            handleFreshFetch(response)
        } else {
            handleLoadMoreFetch(response)
        }
    }

    private val needToHidePlaceholder get() = isNoItemsVisible && _errorPlaceholder.value?.isVisible == true

    private val isFreshFetching get() = paging.currentPage == paging.startPage

    private fun handleFreshFetch(response: Resource<List<Photo>>) {
        when (response) {
            is Resource.Success -> {
                _photos.value = response.data
                _toast.value = Message(R.string.latest_message_data_updated)
            }
            is Resource.Loading -> {
                _refreshing.value = response.isLoading
            }
            is Resource.Error -> {
                _refreshing.value = false
                riseError(response)
            }
        }
    }

    private fun handleLoadMoreFetch(response: Resource<List<Photo>>) {
        when (response) {
            is Resource.Success -> {
                _photos.apply { value = value?.plus(response.data) }
            }
            is Resource.Error -> {
                paging.prevPage()
                riseError(response)
            }
        }
    }

    private fun hidePlaceholderError() {
        _errorPlaceholder.value = ErrorMessage.empty()
    }

    private val isNoItemsVisible get() = _photos.value.isNullOrEmpty()

    private fun riseError(response: Resource.Error) {
        val errorMessage = getErrorMessage(response)

        if (isNoItemsVisible) {
            _errorPlaceholder.value = errorMessage
        } else {
            _error.value = errorMessage
        }
    }

    private fun getErrorMessage(response: Resource.Error): ErrorMessage {
        return when (response.cause) {
            Cause.NO_INTERNET_CONNECTION -> ErrorMessage(true, Message(R.string.error_message_no_internet), R.drawable.ic_no_internet_connection)
            Cause.NOT_AUTHENTICATED, Cause.UNEXPECTED -> ErrorMessage(true, Message(R.string.error_message_try_again_later), R.drawable.img_unexpected_error)
        }
    }

    private fun getOrderingOrDefault(): ApiRequest.Order = _ordering.value ?: ApiRequest.Order.LATEST
}
