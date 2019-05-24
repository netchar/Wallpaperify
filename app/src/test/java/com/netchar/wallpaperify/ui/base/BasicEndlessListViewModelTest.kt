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

import androidx.lifecycle.Observer
import com.netchar.remote.Resource
import com.netchar.remote.enums.Cause
import com.netchar.repository.pojo.ErrorMessage
import com.netchar.repository.pojo.Message
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.wallpaperify.ui.InstantTaskExecutorExtension
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Created by Netchar on 5/11/2019.
 * e.glushankov@gmail.com
 */
@ExtendWith(InstantTaskExecutorExtension::class)
class BasicEndlessListViewModelTest {

    private lateinit var itemsObserver: Observer<List<PhotoPOJO>>
    private lateinit var refreshingObserver: Observer<Boolean>
    private lateinit var errorObserver: Observer<ErrorMessage>
    private lateinit var toastObserver: Observer<Message>
    private lateinit var errorPlaceholderObserver: Observer<ErrorMessage>

    @BeforeEach
    fun onBeforeEach() {
        itemsObserver = mockk(relaxed = true)
        refreshingObserver = mockk(relaxed = true)
        errorObserver = mockk(relaxed = true)
        toastObserver = mockk(relaxed = true)
        errorPlaceholderObserver = mockk(relaxed = true)
    }

    private val successValue: MutableList<PhotoPOJO> = mutableListOf(
            spyk { every { id } returns "1" },
            spyk { every { id } returns "2" })

    private val successLoadMoreValue: List<PhotoPOJO> = listOf(
            spyk { every { id } returns "3" },
            spyk { every { id } returns "4" })


    private val errorValue = Cause.UNEXPECTED
    private val loadingStartValue = true
    private val loadingEndValue = false

    private val successResponseMock = Resource.Success(successValue)
    private val successLoadMoreResponseMock = Resource.Success(successLoadMoreValue)
    private val errorResponseMock = Resource.Error(errorValue)
    private val loadingStartResponseMock = Resource.Loading(loadingStartValue)
    private val loadingEndResponseMock = Resource.Loading(loadingEndValue)

    @Test
    fun `On proceed when success should emit items LiveData`() {

        val model = BasicEndlessListViewModel<PhotoPOJO>()
        model.proceedFetching(successResponseMock)
        model.items.observeForever(itemsObserver)

        verify {
            itemsObserver.onChanged(successValue)
        }

        confirmVerified(itemsObserver)
    }

    @Test
    fun `On proceed when error should emit error placeholder`() {

        val model = BasicEndlessListViewModel<PhotoPOJO>()
        model.proceedFetching(errorResponseMock)
        model.items.observeForever(itemsObserver)
        model.errorPlaceholder.observeForever(errorPlaceholderObserver)
        model.error.observeForever(errorObserver)

        verify {
            errorPlaceholderObserver.onChanged(any())
        }

        verify {
            itemsObserver wasNot called
            errorObserver wasNot called
        }

        confirmVerified(itemsObserver, errorPlaceholderObserver, errorObserver)
    }

    @Test
    fun `On proceed when error and items exist should emit error toast`() {
        val model = BasicEndlessListViewModel<PhotoPOJO>()

        model.items.value = successValue

        model.proceedFetching(errorResponseMock)
        model.items.observeForever(itemsObserver)
        model.errorPlaceholder.observeForever(errorPlaceholderObserver)
        model.error.observeForever(errorObserver)

        verify {
            errorObserver.onChanged(any())
            errorPlaceholderObserver wasNot called
        }

        confirmVerified(errorPlaceholderObserver, errorObserver)
    }

    @Test
    fun `On proceed when start loading should emit refreshing`() {

        val latestViewModel = BasicEndlessListViewModel<PhotoPOJO>()
        latestViewModel.items.observeForever(itemsObserver)
        latestViewModel.refreshing.observeForever(refreshingObserver)
        latestViewModel.proceedFetching(loadingStartResponseMock)

        verify {
            refreshingObserver.onChanged(loadingStartValue)
            itemsObserver wasNot called
        }

        confirmVerified(itemsObserver, refreshingObserver)
    }

    @Test
    fun `On proceed when end loading should emit refreshing`() {

        val latestViewModel = BasicEndlessListViewModel<PhotoPOJO>()
        latestViewModel.items.observeForever(itemsObserver)
        latestViewModel.refreshing.observeForever(refreshingObserver)
        latestViewModel.proceedFetching(loadingEndResponseMock)

        verify {
            refreshingObserver.onChanged(loadingEndValue)
            itemsObserver wasNot called
        }

        confirmVerified(itemsObserver, refreshingObserver)
    }

    @Test
    fun `On proceed when error should emit refreshing to false`() {

        val latestViewModel = BasicEndlessListViewModel<PhotoPOJO>()
        latestViewModel.items.observeForever(itemsObserver)
        latestViewModel.refreshing.observeForever(refreshingObserver)
        latestViewModel.proceedFetching(errorResponseMock)

        verify {
            refreshingObserver.onChanged(loadingEndValue)
            itemsObserver wasNot called
        }

        confirmVerified(itemsObserver, refreshingObserver)
    }

    @Test
    fun `On proceed when loadMore and success should emit previous loaded photos plus photos next page`() {
        val latestViewModel = BasicEndlessListViewModel<PhotoPOJO>()

        // act
        latestViewModel.items.observeForever(itemsObserver)

        latestViewModel.proceedFetching(successResponseMock)
        latestViewModel.paging.nextPage()
        latestViewModel.proceedFetching(successLoadMoreResponseMock)

        verifyOrder {
            itemsObserver.onChanged(successValue)
            itemsObserver.onChanged(successValue + successLoadMoreValue)
        }

        confirmVerified(itemsObserver)
    }

    @Test
    fun `On proceed when loadMore and  error should emit error LiveData`() {
        val latestViewModel = BasicEndlessListViewModel<PhotoPOJO>()

        latestViewModel.items.observeForever(itemsObserver)
        latestViewModel.error.observeForever(errorObserver)

        latestViewModel.proceedFetching(successResponseMock)
        latestViewModel.paging.nextPage()
        latestViewModel.proceedFetching(errorResponseMock)

        verifyOrder {
            itemsObserver.onChanged(successValue)
            errorObserver.onChanged(ofType())
        }

        confirmVerified(itemsObserver, errorObserver)
    }

    @Test
    fun `On proceed when loadMore and  retry should run same page`() {
        val latestViewModel = BasicEndlessListViewModel<PhotoPOJO>()

        latestViewModel.items.observeForever(itemsObserver)
        latestViewModel.error.observeForever(errorObserver)

        latestViewModel.proceedFetching(successResponseMock)
        latestViewModel.paging.nextPage()
        latestViewModel.proceedFetching(successLoadMoreResponseMock)
        latestViewModel.paging.nextPage()
        latestViewModel.proceedFetching(errorResponseMock)
        latestViewModel.proceedFetching(successLoadMoreResponseMock)

        verifyOrder {
            itemsObserver.onChanged(successValue)
            itemsObserver.onChanged(successValue + successLoadMoreValue)
            errorObserver.onChanged(ofType())
            itemsObserver.onChanged(successValue + successLoadMoreValue + successLoadMoreValue)
        }

        confirmVerified(errorObserver, itemsObserver)
    }
}