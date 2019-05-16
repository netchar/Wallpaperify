package com.netchar.wallpaperify.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.viewpager.widget.ViewPager
import com.netchar.common.base.BaseFragment
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.collections.CollectionsFragment
import com.netchar.wallpaperify.ui.photos.PhotosFragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyWindowInsetsForToolbarOnly()

        val viewpager = view.findViewById<ViewPager>(R.id.pager)
        viewpager.adapter = HomeFragmentPagerAdapter(childFragmentManager).also {
            it.addFragment(PhotosFragment(), getString(R.string.photos_fragment_title))
            it.addFragment(CollectionsFragment(), getString(R.string.collections_fragment_title))
        }
        tabs.setupWithViewPager(viewpager)
    }

    private fun applyWindowInsetsForToolbarOnly() {
        this.toolbar?.setOnApplyWindowInsetsListener { toolbar, windowInsets ->
            toolbar.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
            // consuming insets will stop propagating them to other children
            windowInsets.consumeSystemWindowInsets()
        }
    }
}
