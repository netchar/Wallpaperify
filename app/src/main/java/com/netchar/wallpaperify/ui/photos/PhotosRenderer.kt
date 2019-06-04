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

package com.netchar.wallpaperify.ui.photos

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.netchar.common.extensions.fitWidth
import com.netchar.common.extensions.getThemeAttrColor
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import com.netchar.common.utils.ShimmerFactory
import com.netchar.common.utils.parseColor
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.App
import kotlinx.android.synthetic.main.row_photo.view.*

class PhotosRenderer(private val glide: RequestManager, val listener: (PhotoPOJO, ImageView) -> Unit) : ItemRenderer() {

    companion object {
        val fetchedColors = hashMapOf<String, Int>()
    }

    override val renderKey: String = this::class.java.name

    override fun layoutRes() = R.layout.row_photo

    private lateinit var photoItem: PhotoRecyclerItem

    override fun bind(itemView: View, item: IRecyclerItem) {
        photoItem = item as PhotoRecyclerItem

        val photo = photoItem.data
        val color = getPhotoColor(photo.color)

        itemView.row_photo_card.setBackgroundColor(color)
        setupImage(itemView, photo)

        itemView.setOnClickListener {
            listener(photo, itemView.row_photo_iv)
        }
    }

    private fun setupImage(itemView: View, photo: PhotoPOJO) {
        val shimmer = ShimmerFactory.getShimmer()

        with(itemView.row_photo_iv) {
            transitionName = photo.id
            fitWidth(photo.width, photo.height)
            background = shimmer

            glide.load(photo.urls.regular)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return releaseShimmer()
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            return releaseShimmer()
                        }

                        private fun releaseShimmer(): Boolean {
                            shimmer.stopShimmer()
                            background = null
                            return false
                        }
                    })
                    .into(this)
        }
    }

    private fun getPhotoColor(stringColor: String): Int = fetchedColors.getOrPut(stringColor) {
        stringColor.parseColor(0x40, App.get().getThemeAttrColor(R.attr.colorPrimary))
    }
}