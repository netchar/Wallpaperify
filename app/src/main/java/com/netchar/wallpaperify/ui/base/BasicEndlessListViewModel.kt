package com.netchar.wallpaperify.ui.base

import androidx.lifecycle.MediatorLiveData
import com.netchar.common.utils.SingleLiveData
import com.netchar.models.apirequest.Paging
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.models.uimodel.Message
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.wallpaperify.R

/**
 * Created by Netchar on 08.05.2019.
 * e.glushankov@gmail.com
 */

class BasicEndlessListViewModel<TModel> {
    val paging = Paging(startPage = 1)
    val items: MediatorLiveData<List<TModel>> = MediatorLiveData()
    val refreshing: SingleLiveData<Boolean> = SingleLiveData()
    val error: SingleLiveData<ErrorMessage> = SingleLiveData()
    val toast: SingleLiveData<Message> = SingleLiveData()
    val errorPlaceholder: SingleLiveData<ErrorMessage> = SingleLiveData()

    fun proceedFetching(response: Resource<List<TModel>>) {
        if (needToHidePlaceholder) {
            hidePlaceholderError()
        }

        if (isFreshFetching) {
            handleFreshFetch(response)
        } else {
            handleLoadMoreFetch(response)
        }
    }

    private val needToHidePlaceholder get() = isNoItemsVisible && errorPlaceholder.value?.isVisible == true

    private val isNoItemsVisible get() = items.value.isNullOrEmpty()

    private val isFreshFetching get() = paging.currentPage == paging.startPage

    private fun handleFreshFetch(response: Resource<List<TModel>>) {
        when (response) {
            is Resource.Success -> {
                items.value = response.data
                toast.value = Message(R.string.latest_message_data_updated)
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

        if (isNoItemsVisible) {
            errorPlaceholder.value = errorMessage
        } else {
            error.value = errorMessage
        }
    }

    private fun getErrorMessage(response: Resource.Error): ErrorMessage {
        return when (response.cause) {
            Cause.NO_INTERNET_CONNECTION -> ErrorMessage(true, Message(R.string.error_message_no_internet), R.drawable.ic_no_internet_connection)
            Cause.NOT_AUTHENTICATED, Cause.UNEXPECTED -> ErrorMessage(
                    true,
                    Message(R.string.error_message_try_again_later),
                    R.drawable.img_unexpected_error
            )
        }
    }
}