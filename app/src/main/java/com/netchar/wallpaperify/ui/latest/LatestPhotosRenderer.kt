package com.netchar.wallpaperify.ui.latest

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import com.netchar.models.Photo
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.raw_photo.view.*


class LatestPhotosRenderer(private val glide: RequestManager, val listener: (Photo) -> Unit) : ItemRenderer() {

    override val renderKey: String = this::class.java.name

    override fun layoutRes() = R.layout.raw_photo

    private lateinit var photo: LatestPhotoRecyclerItem

    override fun onSetListeners(itemView: View) {
        itemView.setOnClickListener {
            if (::photo.isInitialized) {
                listener(photo.data)
            }
        }
    }

    override fun bind(itemView: View, item: IRecyclerItem) {
        photo = item as LatestPhotoRecyclerItem

        itemView.row_photo_iv.fitWidth(photo.data.width, photo.data.height)

        val shimmer = Shimmer.ColorHighlightBuilder()
            .setBaseColor(Color.parseColor(photo.data.color))
            .setHighlightColor(Color.parseColor(photo.data.color))
            .setHighlightAlpha(0.8f)
            .setBaseAlpha(0.9f)
            .setAutoStart(true)
            .setDuration(2000)
            .build()

        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }

        itemView.row_photo_iv.background = shimmerDrawable

        glide.load(photo.data.urls?.raw)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    shimmerDrawable.stopShimmer()
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    shimmerDrawable.stopShimmer()
                    return false
                }
            })
            .into(itemView.row_photo_iv)
    }

    private fun ImageView.fitWidth(imageWidth: Int, imageHeight: Int) {
        val scaleFactor = imageWidth.toFloat() / imageHeight
        minimumHeight = (resources.displayMetrics.widthPixels / scaleFactor).toInt()
    }
}