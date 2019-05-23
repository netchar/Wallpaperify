package com.netchar.wallpaperify.ui.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.models.Photo
import com.netchar.models.apirequest.ApiRequest
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.models.uimodel.Message
import com.netchar.remote.Resource
import com.netchar.repository.photos.IPhotosRepository
import com.netchar.wallpaperify.ui.base.BasicEndlessListViewModel
import javax.inject.Inject

/**
 * Created by Netchar on 26.04.2019.
 * e.glushankov@gmail.com
 */

class PhotosViewModel @Inject constructor(
        private val repository: IPhotosRepository,
        dispatchers: CoroutineDispatchers
) : BaseViewModel(dispatchers) {

    companion object {
        val defaultOrdering = ApiRequest.Order.LATEST
    }

    private val request = MediatorLiveData<ApiRequest.Photos>()
    private val listViewModel = BasicEndlessListViewModel<Photo>()
    private val _ordering = MutableLiveData<ApiRequest.Order>()

    private val repositoryLiveData: LiveData<Resource<List<Photo>>> = Transformations.switchMap(request) { request ->
        repository.getPhotos(request, scope).getLiveData()
    }

    init {
        addMediatorSources()

        // will trigger to fetch data
        orderBy(defaultOrdering)
    }

    val photos: LiveData<List<Photo>> get() = listViewModel.items
    val refreshing: LiveData<Boolean> get() = listViewModel.refreshing
    val error: LiveData<ErrorMessage> get() = listViewModel.error
    val toast: LiveData<Message> get() = listViewModel.toast
    val errorPlaceholder: LiveData<ErrorMessage> get() = listViewModel.errorPlaceholder
    val ordering: LiveData<ApiRequest.Order> get() = _ordering

    fun refresh() {
        requestPhotos(listViewModel.paging.fromStart(), getOrderingOrDefault())
    }

    fun loadMore() {
        requestPhotos(listViewModel.paging.nextPage(), getOrderingOrDefault())
    }

    fun orderBy(ordering: ApiRequest.Order) {
        _ordering.value = ordering
    }

    private fun addMediatorSources() {
        request.addSource(_ordering) { ordering ->
            requestPhotos(listViewModel.paging.fromStart(), ordering)
        }

        listViewModel.items.addSource(repositoryLiveData) { response ->
            listViewModel.proceedFetching(response)
        }
    }

    private fun requestPhotos(page: Int, order: ApiRequest.Order) {
        request.value = ApiRequest.Photos(page, order)
    }

    private fun getOrderingOrDefault(): ApiRequest.Order = _ordering.value ?: ApiRequest.Order.LATEST
}
