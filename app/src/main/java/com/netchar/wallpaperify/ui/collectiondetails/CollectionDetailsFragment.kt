package com.netchar.wallpaperify.ui.collectiondetails

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.snackbar.Snackbar
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.*
import com.netchar.common.poweradapter.adapter.EndlessRecyclerAdapter
import com.netchar.common.poweradapter.adapter.EndlessRecyclerDataSource
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import com.netchar.wallpaperify.di.modules.GlideApp
import com.netchar.wallpaperify.ui.home.HomeFragmentDirections
import com.netchar.wallpaperify.ui.photos.PhotosRenderer
import com.netchar.wallpaperify.ui.photos.asRecyclerItems
import kotlinx.android.synthetic.main.fragment_collection_details.*
import javax.inject.Inject

class CollectionDetailsFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: CollectionDetailsViewModel

    override val layoutResId: Int = R.layout.fragment_collection_details

    private val dataSource: EndlessRecyclerDataSource by lazy {
        val photoRenderer = PhotosRenderer(GlideApp.with(this), ::onItemClick)
        EndlessRecyclerDataSource(mutableListOf(photoRenderer), ::onLoadMoreItems)
    }

    private val adapter: EndlessRecyclerAdapter by lazy {
        EndlessRecyclerAdapter(dataSource)
    }

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = injectViewModel(viewModelFactory)
        activity.setDisplayShowTitleEnabled(false)

        applyWindowsInsets()
        setupViews()
        observe()
    }

    private fun applyWindowsInsets() = collection_details_coordinator.setOnApplyWindowInsetsListener { _, windowInsets ->
        fragmentToolbar?.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
        windowInsets.consumeSystemWindowInsets()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // removing listeners from EndlessRecyclerAdapter
        collection_details_recycler.detachAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_collection_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_share -> {
                toast("share")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViews() {
        arguments?.let { bundle ->
            val safeArguments = CollectionDetailsFragmentArgs.fromBundle(bundle)
            with(safeArguments) {
                GlideApp.with(this@CollectionDetailsFragment)
                    .load(authorPhotoUrl)
                    .transform(CircleCrop())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(collection_details_img_author)

                collection_details_recycler.setHasFixedSize(true)
                collection_details_recycler.adapter = adapter
                collection_details_txt_author.text = getString(R.string.collection_item_author_prefix, authorName)
                collection_details_txt_photos_count.text = getString(R.string.collection_item_photo_count_postfix, totalPhotos)
                collection_details_txt_title.text = collectionTitle
                collection_details_txt_title.goneIfEmpty()
                collection_details_txt_description.text = collectionDescription
                collection_details_txt_description.goneIfEmpty()


                viewModel.setCollectionId(collectionId)
            }
        }
    }

    private fun observe() {
        viewModel.photos.observe(viewLifecycleOwner, Observer { photos ->
            photos?.let {
                //todo: refactor asRecyclerItems
                dataSource.setData(it.asRecyclerItems())
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            dataSource.showRetryItem()
            snack(getStringSafe(it.errorMessage.messageRes), Snackbar.LENGTH_LONG)
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            toast(getStringSafe(it.messageRes))
        })
    }

    private fun onLoadMoreItems() {
        viewModel.loadMore()
    }

    private fun onItemClick(model: PhotoPOJO, imageView: ImageView) {
        val action = HomeFragmentDirections.actionGlobalPhotoDetailsFragment(model.urls.regular, "")
        action.photoId = model.id
        action.photoDescription = model.description ?: ""
        findNavController().navigate(action)
    }
}