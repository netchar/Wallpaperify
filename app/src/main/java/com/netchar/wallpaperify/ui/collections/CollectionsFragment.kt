package com.netchar.wallpaperify.ui.collections

import android.os.Bundle
import android.view.View
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.injectViewModel
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import javax.inject.Inject

class CollectionsFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: CollectionsViewModel

    override val layoutResId: Int = R.layout.fragment_collections

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
    }

}
