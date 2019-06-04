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
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.*
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.collections.CollectionsFragment
import com.netchar.wallpaperify.ui.photos.PhotosFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.math.absoluteValue

class HomeFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_home

    private var viewScreenshot: Bitmap? = null

    private val onAppBarScrollListener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        updateToolbarForegroundAlpha(verticalOffset)
    }

    private fun updateToolbarForegroundAlpha(verticalOffset: Int) {
        val inColorCode = (255 * verticalOffset.absoluteValue / 100).coerceIn(0..255)
        toolbar.foreground.alpha = inColorCode
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.setTransparentStatusBars(false)
        activity.setLightStatusBar(true)
        activity.setDisplayShowTitleEnabled(false)
        applyWindowInsetsForToolbarOnly()

        toolbar.foreground = ColorDrawable(view.context.getThemeAttrColor(R.attr.colorSurface))
        home_appbar.addOnOffsetChangedListener(onAppBarScrollListener)

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
        home_appbar.removeOnOffsetChangedListener(onAppBarScrollListener)
        home_container.background = BitmapDrawable(resources, viewScreenshot)
        viewScreenshot = null
        super.onDestroyView()
    }
}
