package com.netchar.common.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.netchar.common.base.BaseFragment
import java.io.File


fun File.notExist() = !this.exists()

inline fun <reified TViewModel : ViewModel> AppCompatActivity.injectViewModel(factory: ViewModelProvider.Factory): TViewModel {
    return ViewModelProviders.of(this, factory)[TViewModel::class.java]
}

inline fun <reified TViewModel : ViewModel> Fragment.injectViewModel(factory: ViewModelProvider.Factory): TViewModel {
    return ViewModelProviders.of(this, factory)[TViewModel::class.java]
}

inline fun <reified TViewModel : ViewModel> injectViewModelOf(context: AppCompatActivity, factory: ViewModelProvider.Factory): TViewModel {
    return ViewModelProviders.of(context, factory)[TViewModel::class.java]
}

inline fun <reified TViewModel : ViewModel> injectViewModelOf(context: Fragment, factory: ViewModelProvider.Factory): TViewModel {
    return ViewModelProviders.of(context, factory)[TViewModel::class.java]
}

fun BaseFragment.setSupportActionBar(toolbar: Toolbar) = (activity as AppCompatActivity).setSupportActionBar(toolbar)