package com.netchar.wallpaperify.ui.latest

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.SingleLiveData
import com.netchar.models.Photo
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.repository.IPhotosRepository
import com.netchar.repository.IPhotosRepository.PhotosApiRequest
import com.netchar.repository.IPhotosRepository.PhotosApiRequest.OrderBy
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.App
import javax.inject.Inject

/**
 * Created by Netchar on 26.04.2019.
 * e.glushankov@gmail.com
 */
class LatestViewModel @Inject constructor(
        private val repository: IPhotosRepository,
        dispatchers: CoroutineDispatchers,
        context: Context
) : BaseViewModel(dispatchers, context as App) {
    data class OopsPlaceholder(val isVisible: Boolean, val message: String)

    private var page = 1
    private val _oopsPlaceholder = SingleLiveData<OopsPlaceholder>()
    private val _toast = SingleLiveData<String>()
    private val _error = SingleLiveData<String>()
    private val _refreshing = SingleLiveData<Boolean>()
    private val _ordering = MutableLiveData<String>()
    private val _photos = MediatorLiveData<List<Photo>>().apply {
        addSource(_ordering) { orderBy ->
            fetchPhotos(PhotosApiRequest(1, PhotosApiRequest.ITEMS_PER_PAGE, orderBy, true))
        }
    }

    init {
        fetchPhotos(PhotosApiRequest(1, PhotosApiRequest.ITEMS_PER_PAGE, orderingOrDefault(), false))
    }


    fun refresh() {
        fetchPhotos(PhotosApiRequest(1, PhotosApiRequest.ITEMS_PER_PAGE, orderingOrDefault(), true))
    }

    fun loadMore() {
        fetchPhotos(PhotosApiRequest(++page, PhotosApiRequest.ITEMS_PER_PAGE, orderingOrDefault(), true))
    }

    fun orderBy(@OrderBy ordering: String) {
        _ordering.value = ordering
    }

    val photos: LiveData<List<Photo>> get() = _photos
    val refreshing: LiveData<Boolean> get() = _refreshing
    val error: LiveData<String> get() = _error
    val toast: LiveData<String> get() = _toast
    val oopsPlaceholder: LiveData<OopsPlaceholder> = _oopsPlaceholder

    private fun fetchPhotos(request: PhotosApiRequest) {
        hidePlaceholders()

        val repoLiveData = repository.getPhotos(request, scope).getLiveData()
        _photos.removeSource(repoLiveData)
        _photos.addSource(repoLiveData) { response ->
            val isRefreshing = request.page <= 1
            if (isRefreshing) {
                onFetch(response)
            } else {
                onFetchNextPageItems(response)
            }
        }
    }

    private fun onFetch(response: Resource<List<Photo>>) {
        when (response) {
            is Resource.Success -> {
                _photos.value = response.data
                _toast.value = getStringRes(R.string.latest_message_data_updated)
            }
            is Resource.Loading -> _refreshing.value = response.isLoading
            is Resource.Error -> {
                _refreshing.value = false
                raiseErrorMessage(response)
            }
        }
    }

    private fun onFetchNextPageItems(response: Resource<List<Photo>>) {
        when (response) {
            is Resource.Success -> _photos.apply { value = value?.plus(response.data) }
            is Resource.Error -> raiseErrorMessage(response)
        }
    }

    private fun hidePlaceholders() {
        _oopsPlaceholder.value = OopsPlaceholder(false, "")
    }

    private fun raiseErrorMessage(response: Resource.Error) {
        val message = when (response.cause) {
            Cause.NO_INTERNET_CONNECTION -> getApplication<App>().getString(R.string.error_message_no_internet)
            Cause.NOT_AUTHENTICATED, Cause.UNEXPECTED -> getApplication<App>().getString(R.string.error_message_try_again_later)
        }

        if (_photos.value == null) {
            _oopsPlaceholder.value = OopsPlaceholder(true, message)
        } else {
            _error.value = message
        }
    }

//    private fun buildRequest(page: Int, forceFetching: Boolean): PhotosApiRequest {
//        return PhotosApiRequest(page, PhotosApiRequest.ITEMS_PER_PAGE, orderingOrDefault(), forceFetching)
//    }

    private fun orderingOrDefault() = _ordering.value ?: PhotosApiRequest.LATEST

    override fun onCleared() {
        super.onCleared()
    }
}
