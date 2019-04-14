package com.netchar.wallpaperify.ui.home


import android.os.Bundle
import android.view.View
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.base.BaseFragment

class HomeFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //toolbar!!.setupWithNavController(findNavController())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        (baseActivity as MainActivity).setupNavigation(toolbar!!)
//        toolbar!!.setupWithNavController(findNavController())
    }
}
