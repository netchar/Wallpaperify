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

package com.netchar.wallpaperify.ui.collectiondetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.netchar.common.base.BaseViewModel
import com.netchar.common.services.IExternalAppService
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.remote.apirequest.ApiRequest
import com.netchar.repository.collection.ICollectionRepository
import com.netchar.repository.pojo.ErrorMessage
import com.netchar.repository.pojo.Message
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.repository.pojo.Resource
import com.netchar.repository.preferences.IPreferences
import com.netchar.wallpaperify.ui.base.BasicEndlessListViewModel
import javax.inject.Inject

class CollectionDetailsViewModel @Inject constructor(
        coroutineDispatchers: CoroutineDispatchers,
        private val repository: ICollectionRepository,
        private val externalAppService: IExternalAppService,
        val preferences: IPreferences
) : BaseViewModel(coroutineDispatchers) {
    private val request = MediatorLiveData<ApiRequest.Collection>()
    private val listViewModel = BasicEndlessListViewModel<PhotoPOJO>()
    private val collectionId: MutableLiveData<Int> = MutableLiveData()

    private val repositoryLiveData: LiveData<Resource<List<PhotoPOJO>>> = Transformations.switchMap(request) { request ->
        repository.getCollectionPhotos(request, this).getLiveData()
    }

    init {
        addMediatorSources()
    }

    val photos: LiveData<List<PhotoPOJO>> get() = listViewModel.items
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

    fun setCollectionId(id: Int) {
        collectionId.value = id
        requestPhotos(listViewModel.paging.fromStart())
    }

    private fun requestPhotos(page: Int) {
        collectionId.value?.let { id ->
            request.value = ApiRequest.Collection(id, page)
        }
    }

    fun openPhotoAuthorPage(authorLink: String) {
        externalAppService.openWebPage(authorLink)
    }
}