package com.netchar.wallpaperify.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.netchar.common.di.ViewModelKey
import com.netchar.wallpaperify.di.ViewModelFactory
import com.netchar.wallpaperify.ui.latest.LatestViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelBindingModule {

    @Binds
    @IntoMap
    @ViewModelKey(LatestViewModel::class)
    abstract fun bindLatestViewModel(viewModel: LatestViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}