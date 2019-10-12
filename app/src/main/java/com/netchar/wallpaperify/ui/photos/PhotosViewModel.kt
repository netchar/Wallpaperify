/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.wallpaperify.ui.photos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.apirequest.ApiRequest
import com.netchar.repository.pojo.ErrorMessage
import com.netchar.repository.pojo.Message
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.repository.pojo.Resource
import com.netchar.repository.usecase.IPhotoUseCase
import com.netchar.wallpaperify.ui.base.BasicEndlessListViewModel
import javax.inject.Inject


class PhotosViewModel @Inject constructor(
        private val useCase: IPhotoUseCase,
        dispatchers: CoroutineDispatchers
) : BaseViewModel(dispatchers) {

    companion object {
        val defaultOrdering = ApiRequest.Order.LATEST
    }

    private val request = MediatorLiveData<ApiRequest.Photos>()
    private val listViewModel = BasicEndlessListViewModel<PhotoPOJO>()
    private val _ordering = MutableLiveData<ApiRequest.Order>()

    private val repositoryLiveData: LiveData<Resource<List<PhotoPOJO>>> = Transformations.switchMap(request) { request ->
        useCase.getPhotos(request, this).getLiveData()
    }

    init {
        addMediatorSources()

        // will trigger to fetch data
        orderBy(defaultOrdering)
    }

    val photos: LiveData<List<PhotoPOJO>> get() = listViewModel.items
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
