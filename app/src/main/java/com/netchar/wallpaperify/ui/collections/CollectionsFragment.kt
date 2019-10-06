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

package com.netchar.wallpaperify.ui.collections

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.netchar.common.base.BaseFragment
import com.netchar.common.connectUnsplashUtmParameters
import com.netchar.common.extensions.*
import com.netchar.common.poweradapter.adapter.EndlessRecyclerDataSource
import com.netchar.common.poweradapter.adapter.RecyclerAdapter
import com.netchar.repository.pojo.CollectionPOJO
import com.netchar.repository.pojo.ErrorMessage
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import com.netchar.wallpaperify.di.modules.GlideApp
import com.netchar.wallpaperify.ui.home.HomeFragmentDirections
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_collections.*
import javax.inject.Inject

class CollectionsFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: CollectionsViewModel

    override val layoutResId: Int = R.layout.fragment_collections

    private val dataSource: EndlessRecyclerDataSource by lazy {
        val renderer = CollectionRenderer(GlideApp.with(this), ::onItemClick)
        EndlessRecyclerDataSource(mutableListOf(renderer), ::onLoadMoreItems)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        setupViews()
        observe()
    }

    private fun setupViews() {
        collections_recycler.setHasFixedSize(true)
        collections_recycler.adapter = RecyclerAdapter(dataSource)
        collections_recycler.onLoadMore = ::onLoadMoreItems
        collections_swipe.setOnRefreshListener { viewModel.refresh() }
    }

    private fun onLoadMoreItems() {
        viewModel.loadMore()
    }

    private fun observe() {
        viewModel.collections.observe { photos ->
            dataSource.setData(photos.asRecyclerItems())
        }

        viewModel.refreshing.observe {
            collections_swipe.postAction { isRefreshing = it }
        }

        viewModel.error.observe {
            dataSource.applyState(EndlessRecyclerDataSource.State.ERROR)
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
        collections_recycler.inverseBooleanVisibility(error.isVisible)

        with(collections_error) {
            booleanVisibility(error.isVisible)

            if (isVisible()) {
                message = getStringSafe(error.errorMessage.messageRes)
                imageResource = error.errorImageRes
            }
        }
    }

    private fun onItemClick(model: CollectionPOJO, imageView: ImageView, authorNameView: TextView, photosCountView: TextView, titleView: TextView) {
        val action = HomeFragmentDirections.actionHomeFragmentToCollectionDetailsFragment(
                model.id,
                model.coverPhoto.urls.regular,
                model.user.profileImage.large,
                model.user.name,
                model.totalPhotos,
                model.title,
                model.description,
                CollectionDetailsTransitionModel(imageView.transitionName, authorNameView.transitionName, photosCountView.transitionName),
                model.links.html.connectUnsplashUtmParameters(),
                model.user.links.html.connectUnsplashUtmParameters()
        )

        val extras = FragmentNavigatorExtras(
                imageView to imageView.transitionName,
                authorNameView to authorNameView.transitionName,
                photosCountView to photosCountView.transitionName,
                titleView to titleView.transitionName
        )
        findNavController().navigate(action, extras)
    }
}

fun List<CollectionPOJO>.asRecyclerItems() = map { CollectionRecyclerItem(it) }

@SuppressLint("ParcelCreator")
@Parcelize
data class CollectionDetailsTransitionModel(
        val authorNameViewTransitionName: String,
        val totalCountViewTransitionName: String,
        val titleViewTransitionName: String
) : Parcelable