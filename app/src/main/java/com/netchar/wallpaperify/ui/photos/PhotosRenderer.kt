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
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import com.netchar.common.utils.ShimmerFactory
import com.netchar.common.utils.getThemeAttrColor
import com.netchar.common.utils.parseColor
import com.netchar.models.Photo
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.App
import kotlinx.android.synthetic.main.row_photo.view.*

class PhotosRenderer(private val glide: RequestManager, val listener: (Photo, ImageView) -> Unit) : ItemRenderer() {

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

        itemView.isClickable = false
        itemView.row_photo_card.setBackgroundColor(color)
        setupImage(itemView, photo)

        itemView.setOnClickListener {
            listener(photo, itemView.row_photo_iv)
        }
    }

    private fun setupImage(itemView: View, photo: Photo) {
        val shimmer = ShimmerFactory.getShimmer()
        itemView.row_photo_iv.transitionName = photo.id
        with(itemView.row_photo_iv) {
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
                            itemView.isClickable = true
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
        stringColor.parseColor(0x40, getThemeAttrColor(App.get(), R.attr.colorPrimary))
    }
}