package com.netchar.wallpaperify.ui.collections

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.models.Collection
import com.netchar.models.apirequest.ApiRequest
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.models.uimodel.Message
import com.netchar.repository.collection.ICollectionRepository
import com.netchar.wallpaperify.ui.base.BasicEndlessListViewModel
import javax.inject.Inject


class CollectionsViewModel @Inject constructor(
        dispatchers: CoroutineDispatchers,
        private val repository: ICollectionRepository
) : BaseViewModel(dispatchers) {
    private val request = MediatorLiveData<ApiRequest.Collections>()
    private val listViewModel = BasicEndlessListViewModel<Collection>()

    private val repositoryLiveData = Transformations.switchMap(request) { request ->
        repository.getCollections(request, scope).getLiveData()
    }

    init {
        addMediatorSources()
        requestPhotos(listViewModel.paging.fromStart())
    }

    val collections: LiveData<List<Collection>> get() = listViewModel.items
    val refreshing: LiveData<Boolean> get() = listViewModel.refreshing
    val error: LiveData<ErrorMessage> get() = listViewModel.error
    val toast: LiveData<Message> get() = listViewModel.toast
    val errorPlaceholder: LiveData<ErrorMessage> get() = listViewModel.errorPlaceholder

    fun refresh() {
        requestPhotos(listViewModel.paging.fromStart())
    }

    fun loadMore() {
        requestPhotos(listViewModel.paging.nextPage())
    }

    private fun addMediatorSources() {
        listViewModel.items.addSource(repositoryLiveData) { response ->
            listViewModel.proceedFetching(response)
        }
    }

    private fun requestPhotos(page: Int) {
        request.value = ApiRequest.Collections(page)
    }
}