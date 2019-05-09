package com.netchar.wallpaperify.ui.photos

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.*
import com.netchar.common.poweradapter.adapter.EndlessRecyclerAdapter
import com.netchar.common.poweradapter.adapter.EndlessRecyclerDataSource
import com.netchar.models.Photo
import com.netchar.models.apirequest.ApiRequest
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_photos.*
import javax.inject.Inject

class PhotosFragment : BaseFragment() {

    init {
        setHasOptionsMenu(true)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: PhotosViewModel

    override val layoutResId: Int = R.layout.fragment_photos

    private val dataSource: EndlessRecyclerDataSource by lazy {
        val photoRenderer = PhotosRenderer(Glide.with(this), ::onItemClick)
        EndlessRecyclerDataSource(mutableListOf(photoRenderer), ::onLoadMoreItems)
    }

    private val adapter: EndlessRecyclerAdapter by lazy {
        EndlessRecyclerAdapter(dataSource)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_latest, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.latest_menu_item_latest -> viewModel.orderBy(ApiRequest.Order.LATEST)
            R.id.latest_menu_item_oldest -> viewModel.orderBy(ApiRequest.Order.OLDEST)
            R.id.latest_menu_item_popular -> viewModel.orderBy(ApiRequest.Order.POPULAR)
            else -> ApiRequest.Order.LATEST
        }

        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)

        setupViews()
        observe()
    }

    private fun setupViews() {
        latest_recycler.setHasFixedSize(true)
        latest_recycler.adapter = adapter
        latest_swipe.setOnRefreshListener { viewModel.refresh() }
    }

    private fun onLoadMoreItems() {
        viewModel.loadMore()
    }

    private fun observe() {
        viewModel.photos.observe(viewLifecycleOwner, Observer { photos ->
            photos?.let {
                dataSource.setData(it.asRecyclerItems())
            }
        })

        viewModel.refreshing.observe(viewLifecycleOwner, Observer {
            latest_swipe.postAction { isRefreshing = it }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            dataSource.showRetryItem()
            showSnackbar(getStringSafe(it.errorMessage.messageRes), Snackbar.LENGTH_LONG)
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            showToast(getStringSafe(it.messageRes))
        })

        viewModel.errorPlaceholder.observe(viewLifecycleOwner, Observer {
            toggleError(it)
        })
    }

    private fun toggleError(error: ErrorMessage) {
        latest_recycler.inverseBooleanVisibility(error.isVisible)

        with(latest_error) {
            booleanVisibility(error.isVisible)

            if (isVisible()) {
                message = getStringSafe(error.errorMessage.messageRes)
                imageResource = error.errorImageRes
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        latest_recycler.adapter = null
    }

    private fun onItemClick(model: Photo) {
        findNavController().navigate(R.id.photoDetailsFragment)
    }
}

fun List<Photo>.asRecyclerItems() = map { PhotoRecyclerItem(it) }