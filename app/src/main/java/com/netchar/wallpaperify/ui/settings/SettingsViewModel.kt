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

package com.netchar.wallpaperify.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.netchar.common.base.BaseViewModel
import com.netchar.common.services.IPhotoCacheService
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.SingleLiveData
import com.netchar.repository.pojo.Message
import com.netchar.wallpaperify.R
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
        coroutineDispatchers: CoroutineDispatchers,
        private val cacheService: IPhotoCacheService
) : BaseViewModel(coroutineDispatchers) {

    private val _cacheSize: MutableLiveData<Long> = MutableLiveData()
    private val _toast: SingleLiveData<Message> = SingleLiveData()

    init {
        fetchCacheSizeAsync()
    }

    val cacheSize: LiveData<Long> get() = _cacheSize

    val toast: LiveData<Message> get() = _toast

    fun clearCacheAsync() = launch {
        cacheService.clearDiskCacheAsync()
        fetchCacheSizeAsync()
        _toast.value = Message(R.string.preference_message_cache_cleared)
    }

    private fun fetchCacheSizeAsync() = launch {
        _cacheSize.value = cacheService.getCacheSizeMbAsync()
    }
}