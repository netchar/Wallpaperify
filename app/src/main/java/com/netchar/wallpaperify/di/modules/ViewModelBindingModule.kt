package com.netchar.wallpaperify.di.modules

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.netchar.wallpaperify.di.ViewModelKey
import com.netchar.wallpaperify.di.factories.ViewModelFactory
import com.netchar.wallpaperify.home.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelBindingModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}