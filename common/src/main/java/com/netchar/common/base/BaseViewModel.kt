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

package com.netchar.common.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.netchar.common.utils.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext


/**
 * Created by Netchar on 26.04.2019.
 * e.glushankov@gmail.com
 */

open class BaseViewModel(val dispatchers: CoroutineDispatchers) : ViewModel(), CoroutineScope {
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = job + dispatchers.main

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancel()
    }

    protected fun <TMediator, TResponse> MediatorLiveData<TMediator>.observe(liveData: LiveData<TResponse>, observeFunction: (value: TResponse) -> Unit) {
        this.addSource(liveData, observeFunction)
    }
}