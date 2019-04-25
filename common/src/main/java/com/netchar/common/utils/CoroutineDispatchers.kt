package com.netchar.common.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


/**
 * Created by Netchar on 22.03.2019.
 * e.glushankov@gmail.com
 */

data class CoroutineDispatchers(
        val database: CoroutineDispatcher = Dispatchers.IO,
        val disk: CoroutineDispatcher = Dispatchers.IO,
        val network: CoroutineDispatcher = Dispatchers.IO,
        val main: CoroutineDispatcher = Dispatchers.Main,
        val default: CoroutineDispatcher = Dispatchers.Default
)