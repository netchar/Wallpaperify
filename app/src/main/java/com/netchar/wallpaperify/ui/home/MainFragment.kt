package com.netchar.wallpaperify.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.netchar.poweradapter.adapter.RecyclerAdapter
import com.netchar.poweradapter.adapter.RecyclerDataSource
import com.netchar.poweradapter.item.IRecyclerItem
import com.netchar.poweradapter.item.ItemRenderer
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.data.models.Resource
import com.netchar.wallpaperify.data.remote.dto.Photo
import com.netchar.wallpaperify.di.factories.ViewModelFactory
import com.netchar.wallpaperify.infrastructure.extensions.injectViewModel
import com.netchar.wallpaperify.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.raw_photo.view.*
import javax.inject.Inject


class MainFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    override val layoutResId: Int = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // todo: speed up optimizations for layout manager
        main_recycler.setHasFixedSize(true)
        main_recycler.adapter = RecyclerAdapter(dataSource)
    }

    val renderers = mapOf<String, ItemRenderer<IRecyclerItem>>("Photo" to PhotosRenderer { model -> Toast.makeText(this.context, "Clicked: ${model.user.name}", Toast.LENGTH_LONG).show() } as ItemRenderer<IRecyclerItem>)

    private val dataSource by lazy {
        RecyclerDataSource(renderers)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        viewModel.photos.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    dataSource.setData(response.data)
                }
                is Resource.Loading -> {
                    Toast.makeText(this.context, response.isLoading.toString(), Toast.LENGTH_LONG).show()
                }
                is Resource.Error -> {
                    Toast.makeText(this.context, response.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_main_refresh) {
            viewModel.requestPhotos()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed(): Boolean {
        viewModel.cancelFetchingPhotos()
        return true
    }

    class PhotosRenderer(val listener: (Photo) -> Unit) : ItemRenderer<Photo>() {

        // todo: refactor model var using
        private lateinit var model: Photo

        override fun onSetListeners(itemView: View) {
            itemView.setOnClickListener {
                if (::model.isInitialized) {
                    listener(model)
                }
            }
        }

        override fun onBind(itemView: View, model: Photo) {
            this.model = model
            Glide.with(itemView.context)
                .load(model.urls.regular)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.row_photo_iv)
        }

        override fun layoutRes() = R.layout.raw_photo
    }

//    class PhotoAdapter(private val glide: RequestManager) : GenericAdapter<Photo>() {
//        override fun getLayoutId(position: Int, obj: Photo): Int = R.layout.raw_photo
//
//        override fun getViewHolder(view: View, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder = ViewHolder(glide, view)
//
//        class ViewHolder(private val glide: RequestManager, view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view), GenericAdapter.Binder<Photo> {
//            override fun bind(data: Photo) {
//
//                glide
//                    .load(data.urls.thumb)
//                    .fitCenter()
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .into(itemView.row_photo_iv)
//            }
//        }
//    }
}
