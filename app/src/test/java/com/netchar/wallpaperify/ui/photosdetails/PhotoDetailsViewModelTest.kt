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

package com.netchar.wallpaperify.ui.photosdetails

import androidx.lifecycle.Observer
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.repository.photos.IPhotosRepository
import com.netchar.repository.pojo.ErrorMessage
import com.netchar.repository.pojo.Message
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.wallpaperify.ui.InstantTaskExecutorExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
class PhotoDetailsViewModelTest {

    private lateinit var photoObserver: Observer<PhotoPOJO>
    private lateinit var errorObserver: Observer<ErrorMessage>
    private lateinit var toastObserver: Observer<Message>
    private lateinit var repo: IPhotosRepository
    private lateinit var viewModel: PhotoDetailsViewModel

    private val dispatchersMock = CoroutineDispatchers(
            Dispatchers.Unconfined,
            Dispatchers.Unconfined,
            Dispatchers.Unconfined,
            Dispatchers.Unconfined
    )

    @BeforeEach
    fun onBeforeEach() {
        photoObserver = mockk(relaxed = true)
        errorObserver = mockk(relaxed = true)
        toastObserver = mockk(relaxed = true)
        repo = mockk(relaxed = true)
        viewModel = PhotoDetailsViewModel(dispatchersMock, repo)
    }

    @Test
    fun `On fetchData when success should emit photo observer`() {
        every { repo.getPhoto(any(), any()).getLiveData() } returns mockk(relaxed = true)

        viewModel.photo.observeForever(photoObserver)
        viewModel.fetchPhoto("1")

        verify { photoObserver.onChanged(mockk(relaxed = true)) }
    }

}