package com.netchar.wallpaperify.ui.collectiondetails

import android.os.Bundle
import android.text.SpannedString
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.buildSpannedString
import androidx.core.text.underline
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.*
import com.netchar.common.poweradapter.adapter.EndlessRecyclerDataSource
import com.netchar.common.poweradapter.adapter.RecyclerAdapter
import com.netchar.common.utils.share
import com.netchar.repository.pojo.ErrorMessage
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import com.netchar.wallpaperify.di.modules.GlideApp
import com.netchar.wallpaperify.ui.home.HomeFragmentDirections
import com.netchar.wallpaperify.ui.photos.PhotosRenderer
import com.netchar.wallpaperify.ui.photos.asRecyclerItems
import kotlinx.android.synthetic.main.fragment_collection_details.*
import javax.inject.Inject
import kotlin.math.abs

class CollectionDetailsFragment : BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: CollectionDetailsViewModel
    private lateinit var dataSource: EndlessRecyclerDataSource

    private val safeArguments: CollectionDetailsFragmentArgs by lazy {
        CollectionDetailsFragmentArgs.fromBundle(arguments!!)
    }

    override val layoutResId: Int = R.layout.fragment_collection_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sharedElementEnterTransition = inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        activity.setDisplayShowTitleEnabled(false)
        registerAppbarScrollListener()
        applyWindowsInsets()
        setupViews()
        observe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterAppbarScrollListener()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_collection_details, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_share -> activity?.share(safeArguments.shareLink, "Collection by ${safeArguments.authorName}")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun registerAppbarScrollListener() = app_bar.addOnOffsetChangedListener(onAppBarScrollListener)

    private fun unregisterAppbarScrollListener() = app_bar.removeOnOffsetChangedListener(onAppBarScrollListener)

    private val onAppBarScrollListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        val alpha = 1.0f - abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())
        collection_details_txt_description.alpha = alpha
        collection_details_img_author.alpha = alpha
        collection_details_txt_author.alpha = alpha
        collection_details_txt_photos_count.alpha = alpha
    }

    private fun applyWindowsInsets() = collection_details_coordinator.setOnApplyWindowInsetsListener { _, windowInsets ->
        fragmentToolbar?.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
        space_header.updateLayoutParams<ConstraintLayout.LayoutParams> {
            this.height = toolbar.measuredHeight
        }
        windowInsets.consumeSystemWindowInsets()
    }

    private fun setupViews() {
        dataSource = getEndlessSource(safeArguments.totalPhotos)

        GlideApp.with(this@CollectionDetailsFragment)
            .load(safeArguments.authorPhotoUrl)
            .transform(CircleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(collection_details_img_author)

        collection_details_recycler.setHasFixedSize(true)
        collection_details_recycler.onLoadMore = ::onLoadMoreItems
        collection_details_recycler.adapter = RecyclerAdapter(dataSource)

        collection_details_txt_photos_count.text = getString(R.string.collection_item_photo_count_postfix, safeArguments.totalPhotos)
        collection_details_txt_title.text = safeArguments.collectionTitle
        collection_details_txt_description.text = safeArguments.collectionDescription

        collection_details_txt_title.goneIfEmpty()
        collection_details_txt_description.goneIfEmpty()

        collection_details_refresh.setOnRefreshListener {
            viewModel.refresh()
        }

        val photoByText = getAuthorClickableName()
        collection_details_txt_author.text = photoByText
        collection_details_txt_author.movementMethod = LinkMovementMethod.getInstance()
        collection_details_img_author.setOnClickListener { context?.openWebPage(safeArguments.authorLink) }

        viewModel.setCollectionId(safeArguments.collectionId)
    }

    private fun getAuthorClickableName(): SpannedString {
        return buildSpannedString {
            append("${getString(R.string.photo_details_author_prefix)} ")
            underline { append(safeArguments.authorName) }.withClickableSpan(safeArguments.authorName) {
                context?.openWebPage(safeArguments.authorLink)
            }
        }
    }

    private fun getEndlessSource(totalCount: Int): EndlessRecyclerDataSource {
        val photoRenderer = PhotosRenderer(GlideApp.with(this), ::onItemClick)
        return EndlessRecyclerDataSource(mutableListOf(photoRenderer), ::onLoadMoreItems, totalCount)
    }

    private fun observe() {
        viewModel.photos.observe { photos ->
            dataSource.setData(photos.asRecyclerItems())
        }

        viewModel.error.observe {
            dataSource.applyState(EndlessRecyclerDataSource.State.ERROR)
            snack(getStringSafe(it.errorMessage.messageRes), Snackbar.LENGTH_LONG)
        }

        viewModel.toast.observe {
            toast(getStringSafe(it.messageRes))
        }

        viewModel.refreshing.observe {
            collection_details_refresh.postAction { isRefreshing = it }
        }

        viewModel.errorPlaceholder.observe {
            toggleError(it)
        }
    }

    private fun toggleError(error: ErrorMessage) {
        collection_details_recycler.inverseBooleanVisibility(error.isVisible)

        with(collection_details_error) {
            booleanVisibility(error.isVisible)

            if (isVisible()) {
                message = getStringSafe(error.errorMessage.messageRes)
                imageResource = error.errorImageRes
            }
        }
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