package com.netchar.wallpaperify.ui.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.netchar.common.base.BaseFragment
import com.netchar.common.base.callbacs.IOnDropdownSelectedListener
import com.netchar.common.extensions.*
import com.netchar.common.poweradapter.adapter.EndlessRecyclerAdapter
import com.netchar.common.poweradapter.adapter.EndlessRecyclerDataSource
import com.netchar.models.Photo
import com.netchar.models.apirequest.ApiRequest
import com.netchar.models.uimodel.ErrorMessage
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
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
        val photoRenderer = PhotosRenderer(Glide.with(this), ::onItemClick)
        EndlessRecyclerDataSource(mutableListOf(photoRenderer), ::onLoadMoreItems)
    }

    private val adapter: EndlessRecyclerAdapter by lazy {
        EndlessRecyclerAdapter(dataSource)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        exitTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.fade)
        return super.onCreateView(inflater, container, savedInstanceState)
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
        latest_swipe.setOnRefreshListener {
            viewModel.refresh()
        }
        photos_filter_spinner.setOnDropdownItemSelectedListener(object : IOnDropdownSelectedListener {
            override fun onDropdownItemSelected(position: Int, id: Long) {
                val newOrder = ApiRequest.Order.getBy(position)
                val oldOrder = viewModel.ordering.value

                if (oldOrder.isNullOrSame(newOrder)) {
                    return
                }

                viewModel.orderBy(newOrder)
            }
        })
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

        viewModel.ordering.observe(viewLifecycleOwner, Observer {
            photos_filter_spinner.setSelection(it.ordinal)
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
        // removing listeners from EndlessRecyclerAdapter
        latest_recycler.detachAdapter()
    }

    private fun onItemClick(model: Photo, imageView: ImageView) {
        val extras = FragmentNavigatorExtras(
                imageView to imageView.transitionName
        )
        val action = HomeFragmentDirections.actionHomeFragmentToPhotoDetailsFragment(model.urls.regular, imageView.transitionName)
        action.photoId = model.id
        findNavController().navigate(action, extras)
    }
}

fun ApiRequest.Order?.isNullOrSame(newOrder: ApiRequest.Order) = this == null || this == newOrder
fun List<Photo>.asRecyclerItems() = map { PhotoRecyclerItem(it) }