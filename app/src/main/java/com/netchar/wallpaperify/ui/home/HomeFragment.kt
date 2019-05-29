/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.wallpaperify.ui.home

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.getScreenshot
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.collections.CollectionsFragment
import com.netchar.wallpaperify.ui.photos.PhotosFragment
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_home

    private var viewScreenshot: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyWindowInsetsForToolbarOnly()


        pager.adapter = HomeFragmentPagerAdapter(childFragmentManager).also {
            it.addFragment(PhotosFragment(), getString(R.string.photos_fragment_title))
            it.addFragment(CollectionsFragment(), getString(R.string.collections_fragment_title))
        }
        tabs.setupWithViewPager(pager)
    }

    private fun applyWindowInsetsForToolbarOnly() {
        this.fragmentToolbar?.setOnApplyWindowInsetsListener { toolbar, windowInsets ->
            toolbar.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
            // consuming insets will stop propagating them to other children
            windowInsets.consumeSystemWindowInsets()
        }
    }

    override fun onPause() {
        super.onPause()
        viewScreenshot = view?.getScreenshot()
    }

    override fun onDestroyView() {
        home_container.background = BitmapDrawable(resources, viewScreenshot)
        viewScreenshot = null
        super.onDestroyView()
    }
}
