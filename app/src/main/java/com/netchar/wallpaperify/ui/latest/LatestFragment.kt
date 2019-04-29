package com.netchar.wallpaperify.ui.latest

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.injectViewModel
import com.netchar.common.extensions.showSnackbar
import com.netchar.common.extensions.toGone
import com.netchar.common.extensions.toVisible
import com.netchar.common.poweradapter.adapter.EndlessRecyclerAdapter
import com.netchar.common.poweradapter.adapter.EndlessRecyclerDataSource
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

    init {
        setHasOptionsMenu(true)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: LatestViewModel

    override val layoutResId: Int = R.layout.fragment_latest

    private val dataSource: EndlessRecyclerDataSource by lazy {
        val photoRenderer = LatestPhotosRenderer(Glide.with(this), ::onItemClick)
        EndlessRecyclerDataSource(mutableListOf(photoRenderer), ::onLoadMoreItems)
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
        main_recycler.setHasFixedSize(true)
        main_recycler.adapter = adapter
        latest_swipe.setOnRefreshListener { viewModel.refresh() }
    }

    private fun onLoadMoreItems() {
        viewModel.loadMore()
    }

    private fun observe() {
        viewModel.photos.observe(viewLifecycleOwner, Observer { photos ->
            photos?.let {
                dataSource.setData(it.toRecyclerItem())
            }
        })

        viewModel.refreshing.observe(viewLifecycleOwner, Observer { refreshing ->
            latest_swipe.post { latest_swipe.isRefreshing = refreshing }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { message ->
            dataSource.showRetryItem()
            showSnackbar(message, Snackbar.LENGTH_LONG)
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        })

        viewModel.oopsPlaceholder.observe(viewLifecycleOwner, Observer { message ->
            view?.let {
                val goesWrongView = it.findViewById<ConstraintLayout>(R.id.something_goes_wrong_view)
                toggleOppsView(message, goesWrongView)
            }
        })
    }

    private fun toggleOppsView(message: LatestViewModel.OopsPlaceholder, goesWrongView: ConstraintLayout) {
        if (message.isVisible) {
            goesWrongView.toVisible()
            goesWrongView.findViewById<TextView>(R.id.something_goes_wrong_text).text = message.message
            main_recycler.toGone()
        } else {
            goesWrongView.toGone()
            goesWrongView.findViewById<TextView>(R.id.something_goes_wrong_text).text = ""
            main_recycler.toVisible()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        main_recycler.adapter = null
    }

    private fun onItemClick(model: Photo) {
        findNavController().navigate(R.id.photoDetailsFragment)
    }
}

fun List<Photo>.toRecyclerItem() = map { LatestPhotoRecyclerItem(it) }