package com.netchar.wallpaperify.ui.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.SingleLiveData
import com.netchar.models.Collection
import com.netchar.models.apirequest.ApiRequest
import com.netchar.models.apirequest.Paging
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.models.uimodel.Message
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.repository.collection.ICollectionRepository
import com.netchar.wallpaperify.R
import javax.inject.Inject


/**
 * Created by Netchar on 08.05.2019.
 * e.glushankov@gmail.com
 */

class CollectionsViewModel @Inject constructor(
        dispatchers: CoroutineDispatchers,
        private val repository: ICollectionRepository
) : BaseViewModel(dispatchers) {
    private val paging = Paging(startPage = 1)
    private val request = MediatorLiveData<ApiRequest.Collections>()

    private val _errorPlaceholder = SingleLiveData<ErrorMessage>()
    private val _toast = SingleLiveData<Message>()
    private val _error = SingleLiveData<ErrorMessage>()
    private val _refreshing = SingleLiveData<Boolean>()
    private val _collections: MediatorLiveData<List<Collection>> = MediatorLiveData()

    private val repositoryLiveData = Transformations.switchMap(request) { request ->
        repository.getCollections(request, scope).getLiveData()
    }

    init {
        addMediatorSources()
        requestPhotos(paging.fromStart())
    }

    val collections: LiveData<List<Collection>> get() = _collections
    val refreshing: LiveData<Boolean> get() = _refreshing
    val error: LiveData<ErrorMessage> get() = _error
    val toast: LiveData<Message> get() = _toast
    val errorPlaceholder: LiveData<ErrorMessage> get() = _errorPlaceholder

    fun refresh() {
        requestPhotos(paging.fromStart())
    }

    fun loadMore() {
        requestPhotos(paging.nextPage())
    }

    private fun addMediatorSources() {
        _collections.addSource(repositoryLiveData) { response ->
            proceedFetching(response)
        }
    }

    private fun requestPhotos(page: Int) {
        request.value = ApiRequest.Collections(page)
    }

    private fun proceedFetching(response: Resource<List<Collection>>) {
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

    private fun handleFreshFetch(response: Resource<List<Collection>>) {
        when (response) {
            is Resource.Success -> {
                _collections.value = response.data
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

    private fun handleLoadMoreFetch(response: Resource<List<Collection>>) {
        when (response) {
            is Resource.Success -> {
                _collections.apply { value = value?.plus(response.data) }
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

    private val isNoItemsVisible get() = _collections.value.isNullOrEmpty()

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
}