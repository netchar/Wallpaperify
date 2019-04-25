package com.netchar.wallpaperify.ui.latest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.netchar.common.poweradapter.adapter.RecyclerAdapter
import com.netchar.remote.Resource
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import com.netchar.common.extensions.injectViewModel
import com.netchar.common.base.BaseFragment
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
        com.netchar.common.poweradapter.adapter.RecyclerDataSource(listOf(LatestPhotosRenderer(::onItemClick)))
    }

    private fun onItemClick(model: com.netchar.models.Photo) {
        findNavController().navigate(R.id.photoDetailsFragment)
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
}
