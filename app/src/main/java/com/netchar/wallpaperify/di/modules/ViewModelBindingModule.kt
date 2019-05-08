package com.netchar.wallpaperify.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.netchar.common.di.ViewModelKey
import com.netchar.wallpaperify.di.ViewModelFactory
import com.netchar.wallpaperify.ui.collections.CollectionsViewModel
import com.netchar.wallpaperify.ui.photos.PhotosViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelBindingModule {

    @Binds
    @IntoMap
    @ViewModelKey(PhotosViewModel::class)
    abstract fun bindLatestViewModel(viewModel: PhotosViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CollectionsViewModel::class)
    abstract fun bindCollectionsViewModel(viewModel: CollectionsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}