package com.netchar.wallpaperify.ui.collections

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.*
import com.netchar.common.poweradapter.adapter.EndlessRecyclerAdapter
import com.netchar.common.poweradapter.adapter.EndlessRecyclerDataSource
import com.netchar.models.Collection
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_collections.*
import javax.inject.Inject

class CollectionsFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: CollectionsViewModel

    override val layoutResId: Int = R.layout.fragment_collections

    private val dataSource: EndlessRecyclerDataSource by lazy {
        val renderer = CollectionRenderer(Glide.with(this), ::onItemClick)
        EndlessRecyclerDataSource(mutableListOf(renderer), ::onLoadMoreItems)
    }

    private val adapter: EndlessRecyclerAdapter by lazy {
        EndlessRecyclerAdapter(dataSource)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)

        setupViews()
        observe()
    }

    private fun setupViews() {
        collections_recycler.setHasFixedSize(true)
        collections_recycler.adapter = adapter
        collections_swipe.setOnRefreshListener { viewModel.refresh() }
    }

    private fun onLoadMoreItems() {
        viewModel.loadMore()
    }

    private fun observe() {
        viewModel.collections.observe(viewLifecycleOwner, Observer { photos ->
            photos?.let {
                dataSource.setData(it.asRecyclerItems())
            }
        })

        viewModel.refreshing.observe(viewLifecycleOwner, Observer {
            collections_swipe.postAction { isRefreshing = it }
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
        collections_recycler.inverseBooleanVisibility(error.isVisible)

        with(collections_error) {
            booleanVisibility(error.isVisible)

            if (isVisible()) {
                message = getStringSafe(error.errorMessage.messageRes)
                imageResource = error.errorImageRes
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        collections_recycler.adapter = null
    }

    private fun onItemClick(model: Collection) {
        findNavController().navigate(R.id.collectionDetailsFragment)
    }
}

fun List<Collection>.asRecyclerItems() = map { CollectionRecyclerItem(it) }
