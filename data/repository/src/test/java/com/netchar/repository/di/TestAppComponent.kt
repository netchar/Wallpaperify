package com.netchar.repository.di

import com.netchar.common.di.BaseUrl
import com.netchar.remote.di.ApiModule
import com.netchar.repository.photos.PhotosRepositoryTest
import com.netchar.repository.utils.BaseMockServerTest
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


/**
 * Created by Netchar on 01.05.2019.
 * e.glushankov@gmail.com
 */

@Singleton
@Component(
    modules = [
        TestCommonModule::class,
        ApiModule::class,
        RepositoryModule::class,
        TestNetworkModule::class
    ]
)
interface TestAppComponent {

    fun inject(baseTest: BaseMockServerTest)
    fun inject(photoRepo: PhotosRepositoryTest)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun baseUrl(@BaseUrl url: String): Builder

        fun build(): TestAppComponent
    }
}