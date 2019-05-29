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

package com.netchar.wallpaperify.ui.collections

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.netchar.common.extensions.fitWidth
import com.netchar.common.extensions.toGone
import com.netchar.common.extensions.toVisible
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import com.netchar.common.utils.ShimmerFactory
import com.netchar.common.utils.formatDate
import com.netchar.common.utils.getThemeAttrColor
import com.netchar.common.utils.parseColor
import com.netchar.repository.pojo.CollectionPOJO
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.App
import kotlinx.android.synthetic.main.row_collection.view.*

class CollectionRenderer(private val glide: RequestManager, val onClickListener: (CollectionPOJO) -> Unit) : ItemRenderer() {

    companion object {
        val fetchedColors = hashMapOf<String, Int>()
    }

    override val renderKey: String = this::class.java.name

    override fun layoutRes() = R.layout.row_collection

    private lateinit var collection: CollectionRecyclerItem

    override fun bind(itemView: View, item: IRecyclerItem) = with(itemView) {
        collection = item as CollectionRecyclerItem

        val data = collection.data
        val photo = data.coverPhoto
        val color = getPhotoColor(photo.color)
        val date = formatDate(data.publishedAt, "MMM d, YYYY")

        row_collection_content_container.setBackgroundColor(color)
        row_collection_published_txt.text = date
        row_collection_photo_count_txt.text = context.getString(R.string.collection_item_photo_count_postfix, data.totalPhotos)
        row_collection_author_txt.text = context.getString(R.string.collection_item_author_prefix, data.user.name)
        row_collection_title_txt.text = data.title
        row_collection_gradient_overlay.toGone()

        setupImage(this, photo)

        itemView.setOnClickListener {
            onClickListener(data)
        }
    }

    private fun setupImage(itemView: View, coverPhoto: PhotoPOJO) {
        val shimmer = ShimmerFactory.getShimmer()

        with(itemView.row_collection_image) {
            fitWidth(coverPhoto.width, coverPhoto.height)
            background = shimmer

            glide.load(coverPhoto.urls.regular)
                    .fitCenter()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            return releaseShimmer()
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            itemView.row_collection_gradient_overlay.toVisible()
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