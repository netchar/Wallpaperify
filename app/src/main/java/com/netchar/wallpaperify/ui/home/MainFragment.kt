package com.netchar.wallpaperify.ui.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.base.BaseFragment
import com.netchar.wallpaperify.di.factories.ViewModelFactory
import com.netchar.wallpaperify.infrastructure.extensions.injectViewModel
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

class MainFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        main_recycler.setHasFixedSize(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = injectViewModel(viewModelFactory)
        viewModel.getPhotos().observe { photos ->
            photos?.let {
                // adapter.updateData(it)
                Toast.makeText(this.context, photos.toString(), Toast.LENGTH_LONG).show()
            }
        }

        viewModel.onError.observe { error -> Toast.makeText(this.context, error, Toast.LENGTH_LONG).show()}
        viewModel.onLoading.observe { loading -> Toast.makeText(this.context, "LOADING", Toast.LENGTH_LONG).show()}
    }

    //fun <T> LiveData<T>.observe(function: (T) -> Unit) = this.observe(viewLifecycleOwner, Observer { data -> data?.let { function(it) } })

    private fun <T> LiveData<T>.observe(observe: (T?) -> Unit) = observe(viewLifecycleOwner, Observer { observe(it) })
}
