package com.netchar.wallpaperify.ui.latest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.netchar.poweradapter.adapter.RecyclerAdapter
import com.netchar.poweradapter.adapter.RecyclerDataSource
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.data.models.Resource
import com.netchar.wallpaperify.data.remote.dto.Photo
import com.netchar.wallpaperify.di.factories.ViewModelFactory
import com.netchar.wallpaperify.infrastructure.extensions.injectViewModel
import com.netchar.wallpaperify.ui.base.BaseFragment
import com.netchar.wallpaperify.ui.home.MainViewModel
import kotlinx.android.synthetic.main.fragment_latest.*
import javax.inject.Inject


class LatestFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = LatestFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    override val layoutResId: Int = R.layout.fragment_latest

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main_recycler.setHasFixedSize(true)
        main_recycler.adapter = RecyclerAdapter(dataSource)
    }

    private val dataSource by lazy {
        RecyclerDataSource(listOf(LatestPhotosRenderer(::onItemClick)))
    }

    private fun onItemClick(model: Photo) {
        findNavController().navigate(R.id.photoDetailsFragment)
//        Toast.makeText(this.context, "Clicked: ${model.user.name}", Toast.LENGTH_LONG).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        viewModel.photos.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    dataSource.setData(response.data.map { LatestPhotoRecyclerItem(it) })
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

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_main, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.menu_main_refresh) {
//            viewModel.requestPhotos()
//        }
//        return super.onOptionsItemSelected(item)
//    }

//    override fun onBackPressed(): Boolean {
//        viewModel.cancelFetchingPhotos()
//        return true
//    }


}
