package com.netchar.wallpaperify.home

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.widget.Toast
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.base.BaseActivity
import com.netchar.wallpaperify.di.factories.ViewModelFactory
import com.netchar.wallpaperify.infrastructure.extensions.injectViewModel
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var factory: ViewModelFactory
    lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = injectViewModel(factory)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, MainFragment.newInstance())
                .commit()
        }
    }
}
