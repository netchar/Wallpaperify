package com.netchar.wallpaperify.ui.latest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.injectViewModel
import com.netchar.common.poweradapter.adapter.RecyclerAdapter
import com.netchar.common.poweradapter.adapter.RecyclerDataSource
import com.netchar.models.Photo
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_latest.*
import javax.inject.Inject


class LatestFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = LatestFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: LatestViewModel

    override val layoutResId: Int = R.layout.fragment_latest

    private val dataSource by lazy {
        RecyclerDataSource(listOf(LatestPhotosRenderer(::onItemClick)))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main_recycler.setHasFixedSize(true)
        main_recycler.adapter = RecyclerAdapter(dataSource)
        viewModel = injectViewModel(viewModelFactory)
        latest_swipe.setOnRefreshListener { viewModel.refresh() }
        observe()
    }

    private fun observe() {
        viewModel.photos.observe(viewLifecycleOwner, Observer { photos ->
            photos?.let {
                dataSource.setData(it.toRecyclerItem())
            }
        })

        viewModel.refreshing.observe(viewLifecycleOwner, Observer { refreshing ->
            with(latest_swipe) {
                post { this.isRefreshing = refreshing }
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { message ->
            view?.let {
                Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
            }
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        })
    }

    private fun onItemClick(model: Photo) {
        findNavController().navigate(R.id.photoDetailsFragment)
    }
}

fun List<Photo>.toRecyclerItem() = map { LatestPhotoRecyclerItem(it) }