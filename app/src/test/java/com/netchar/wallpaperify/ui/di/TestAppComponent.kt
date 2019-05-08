package com.netchar.wallpaperify.ui.di

import com.netchar.common.di.BaseUrl
import com.netchar.remote.di.ApiModule
import com.netchar.repository.di.RepositoryModule
import com.netchar.test.di.TestCommonModule
import com.netchar.wallpaperify.ui.photos.PhotosViewModelTest
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

    fun inject(baseTest: PhotosViewModelTest)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun baseUrl(@BaseUrl url: String): Builder

        fun build(): TestAppComponent
    }
}