package com.netchar.repository.di

import com.netchar.common.utils.CoroutineDispatchers
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


/**
 * Created by Netchar on 01.05.2019.
 * e.glushankov@gmail.com
 */
@Module
object TestCommonModule {
    @JvmStatic
    @Provides
    @Singleton
    fun provideDispatchers(): CoroutineDispatchers = CoroutineDispatchers(
        Dispatchers.Unconfined,
        Dispatchers.Unconfined,
        Dispatchers.Unconfined,
        Dispatchers.Unconfined
    )
}