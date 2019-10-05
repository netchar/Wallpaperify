/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.wallpaperify.ui.photos

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.*
import com.netchar.common.poweradapter.adapter.EndlessRecyclerDataSource
import com.netchar.common.poweradapter.adapter.RecyclerAdapter
import com.netchar.repository.pojo.ErrorMessage
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import com.netchar.wallpaperify.di.modules.GlideApp
import com.netchar.wallpaperify.ui.home.HomeFragmentDirections
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
        val photoRenderer = PhotosRenderer(GlideApp.with(this), ::onItemClick)
        EndlessRecyclerDataSource(mutableListOf(photoRenderer), ::onLoadMoreItems)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        setupViews()
        observe()
    }

    private fun setupViews() {
        latest_recycler.setHasFixedSize(true)
        latest_recycler.adapter = RecyclerAdapter(dataSource)
        latest_recycler.onLoadMore = ::onLoadMoreItems
        latest_swipe.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_photos_filter, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.photos_menu_filter_option -> consume {
                showFilterDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showFilterDialog() {
        val currentSortBy = viewModel.ordering.value
        val filterDialog = PhotosFilterDialogFragment.getInstance(currentSortBy)
        filterDialog.listener = { options ->
            options.sortBy?.let { sortBy -> viewModel.orderBy(sortBy) }
        }
        filterDialog.show(childFragmentManager, filterDialog::class.java.simpleName)
    }

    private fun onLoadMoreItems() {
        viewModel.loadMore()
    }

    private fun observe() {
        viewModel.photos.observe { photos ->
            dataSource.setData(photos.asRecyclerItems())
        }

        viewModel.refreshing.observe {
            latest_swipe.postAction { isRefreshing = it }
        }

        viewModel.error.observe {
            dataSource.setState(EndlessRecyclerDataSource.State.ERROR)
            snack(getStringSafe(it.errorMessage.messageRes), Snackbar.LENGTH_LONG)
        }

        viewModel.toast.observe {
            toast(getStringSafe(it.messageRes))
        }

        viewModel.errorPlaceholder.observe {
            toggleError(it)
        }
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

    private fun onItemClick(model: PhotoPOJO, imageView: ImageView) {
        val toolbar = activity!!.findViewById<Toolbar>(R.id.toolbar)
        val extras = FragmentNavigatorExtras(
                toolbar to toolbar.transitionName
        )
        val action = HomeFragmentDirections.actionGlobalPhotoDetailsFragment(model.urls.regular, "")
        action.photoId = model.id
        action.photoDescription = model.description ?: ""
        findNavController().navigate(action, extras)
    }
}

fun List<PhotoPOJO>.asRecyclerItems() = map { PhotoRecyclerItem(it) }