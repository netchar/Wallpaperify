package com.netchar.common.di

import com.netchar.common.utils.CoroutineDispatchers
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object CommonModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideDispatchers(): CoroutineDispatchers = CoroutineDispatchers()
}
