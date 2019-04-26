package com.netchar.wallpaperify.ui.latest

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.SingleLiveData
import com.netchar.models.Photo
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.repository.IPhotosRepository
import com.netchar.repository.IPhotosRepository.PhotosApiRequest
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
    private val _shinePlaceholder: SingleLiveData<Boolean> = SingleLiveData()
    private val _oopsPlaceholder: SingleLiveData<OopsPlaceholder> = SingleLiveData()
    private val _toast: SingleLiveData<String> = SingleLiveData()
    private val _error: SingleLiveData<String> = SingleLiveData()
    private val _refreshing: SingleLiveData<Boolean> = SingleLiveData()
    private val _photos = MediatorLiveData<List<Photo>>()

    init {
        fetchPhotos(buildRequest(1, false))
    }

    fun refresh() = fetchPhotos(buildRequest(1, true))

    fun loadMore() = fetchPhotos(buildRequest(++page, true))

    val photos: LiveData<List<Photo>> get() = _photos
    val refreshing: LiveData<Boolean> get() = _refreshing
    val error: LiveData<String> get() = _error
    val toast: LiveData<String> get() = _toast
    val shinePlaceholder: LiveData<Boolean> = _shinePlaceholder
    val oopsPlaceholder: LiveData<OopsPlaceholder> = _oopsPlaceholder

    private fun fetchPhotos(request: PhotosApiRequest) {
        hidePlaceholders()

        if (_photos.value == null) {
            _shinePlaceholder.value = true
        }

        val repoLiveData = repository.getPhotos(request, scope).getLiveData()
        _photos.addSource(repoLiveData) { response ->
            when (response) {
                is Resource.Success -> {
                    _photos.value = response.data
                    _shinePlaceholder.value = false
                    _toast.value = getApplication<App>().getString(R.string.latest_message_data_updated)
                }
                is Resource.Loading -> _refreshing.value = response.isLoading
                is Resource.Error -> {
                    _shinePlaceholder.value = false
                    _refreshing.value = false
                    raiseErrorMessage(response)
                }
            }
        }
    }

    private fun hidePlaceholders() {
        _shinePlaceholder.value = false
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

    private fun buildRequest(page: Int, forceFetching: Boolean): PhotosApiRequest {
        return PhotosApiRequest(page, PhotosApiRequest.ITEMS_PER_PAGE, PhotosApiRequest.LATEST, forceFetching)
    }
}
