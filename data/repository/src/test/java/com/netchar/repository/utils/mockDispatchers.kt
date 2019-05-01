package com.netchar.repository.utils

import com.netchar.common.utils.CoroutineDispatchers
import kotlinx.coroutines.Dispatchers

val mockDispatchers = CoroutineDispatchers(
    Dispatchers.Unconfined,
    Dispatchers.Unconfined,
    Dispatchers.Unconfined,
    Dispatchers.Unconfined
)