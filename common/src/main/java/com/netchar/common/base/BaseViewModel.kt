package com.netchar.common.base

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.netchar.common.utils.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


/**
 * Created by Netchar on 26.04.2019.
 * e.glushankov@gmail.com
 */

open class BaseViewModel(protected val dispatchers: CoroutineDispatchers) : ViewModel() {
    protected val job = SupervisorJob()
    protected val scope = CoroutineScope(job + dispatchers.main)

}