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

package com.netchar.wallpaperify.ui.photorawview

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.setLightStatusBar
import com.netchar.common.extensions.toast
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.modules.GlideApp
import kotlinx.android.synthetic.main.fragment_photo_raw.*

class PhotoRawFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_photo_raw

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setLightStatusBar(false)
        applyPhotoWindowsInsets()
        arguments?.let {
            val safeArgs = PhotoRawFragmentArgs.fromBundle(it)
            photo_raw_progress.show()
            GlideApp.with(this)
                .load(safeArgs.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        toast(getString(R.string.error_message_failed_to_load_image))
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        photo_raw_progress.hide()
                        return false
                    }
                })
                .into(photo_raw_image)
        }
    }

    private fun applyPhotoWindowsInsets() {
        photo_raw_image.setOnApplyWindowInsetsListener { _, windowInsets ->
            fragmentToolbar?.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
            windowInsets.consumeSystemWindowInsets()
        }
    }
}