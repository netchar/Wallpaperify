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

package com.netchar.wallpaperify.ui.base

import androidx.lifecycle.MediatorLiveData
import com.netchar.common.utils.SingleLiveData
import com.netchar.remote.apirequest.Paging
import com.netchar.remote.enums.Cause
import com.netchar.repository.pojo.ErrorMessage
import com.netchar.repository.pojo.Message
import com.netchar.repository.pojo.Resource
import com.netchar.wallpaperify.R

class BasicEndlessListViewModel<TModel> {
    val paging = Paging()
    val items: MediatorLiveData<List<TModel>> = MediatorLiveData()
    val refreshing: SingleLiveData<Boolean> = SingleLiveData()
    val error: SingleLiveData<ErrorMessage> = SingleLiveData()
    val toast: SingleLiveData<Message> = SingleLiveData()
    val errorPlaceholder: SingleLiveData<ErrorMessage> = SingleLiveData()

    fun proceedFetching(response: Resource<List<TModel>>) {
        if (shouldHidePlaceholders()) {
            hidePlaceholderError()
        }

        if (isInitialFetching()) {
            handleFreshFetch(response)
        } else {
            handleLoadMoreFetch(response)
        }
    }

    private fun shouldHidePlaceholders() = isNoItemsVisible() && errorPlaceholder.value?.isVisible == true

    private fun isNoItemsVisible() = items.value.isNullOrEmpty()

    private fun isInitialFetching() = paging.currentPage == paging.startPage

    private fun handleFreshFetch(response: Resource<List<TModel>>) {
        when (response) {
            is Resource.Success -> {
                items.value = response.data
            }
            is Resource.Loading -> {
                refreshing.value = response.isLoading
            }
            is Resource.Error -> {
                refreshing.value = false
                riseError(response)
            }
        }
    }

    private fun handleLoadMoreFetch(response: Resource<List<TModel>>) {
        when (response) {
            is Resource.Success -> {
                items.apply { value = value?.plus(response.data) }
            }
            is Resource.Error -> {
                paging.prevPage()
                riseError(response)
            }
        }
    }

    private fun hidePlaceholderError() {
        errorPlaceholder.value = ErrorMessage.empty()
    }

    private fun riseError(response: Resource.Error) {
        val errorMessage = getErrorMessage(response)

        if (isNoItemsVisible()) {
            errorPlaceholder.value = errorMessage
        } else {
            error.value = errorMessage
        }
    }

    private fun getErrorMessage(response: Resource.Error): ErrorMessage {
        return when (response.cause) {
            Cause.NO_INTERNET_CONNECTION -> ErrorMessage(true, Message(R.string.message_error_no_internet), R.drawable.ic_no_internet_connection)
            Cause.NOT_AUTHENTICATED, Cause.UNEXPECTED -> ErrorMessage(true, Message(R.string.error_message_try_again_later), R.drawable.img_unexpected_error)
        }
    }
}