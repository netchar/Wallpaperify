package com.netchar.wallpaperify.ui.photos

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
import com.netchar.common.utils.getThemeAttrColor
import com.netchar.models.Photo
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.App
import kotlinx.android.synthetic.main.raw_photo.view.*

class PhotosRenderer(private val glide: RequestManager, val listener: (Photo) -> Unit) : ItemRenderer() {

    companion object {
        val fetchedColors = hashMapOf<String, Int>()
    }

    override val renderKey: String = this::class.java.name

    override fun layoutRes() = R.layout.raw_photo

    private lateinit var photo: PhotoRecyclerItem

    override fun onSetListeners(itemView: View) {
        itemView.setOnClickListener {
            if (::photo.isInitialized) {
                val data = photo.data
                listener(data)
            }
        }
    }

    override fun bind(itemView: View, item: IRecyclerItem) {
        photo = item as PhotoRecyclerItem
        val model = photo.data

        val shimmer = ShimmerDrawable().apply {
            setShimmer(Shimmer.AlphaHighlightBuilder()
                    .setHighlightAlpha(0.85f)
                    .setBaseAlpha(0.8f)
                    .setAutoStart(true)
                    .setDuration(1500)
                    .setIntensity(0.2f)
                    .build())
        }

        val color = getCardBackgroundColor(model.color)
        itemView.row_photo_card.setBackgroundColor(color)

        with(itemView.row_photo_iv) {
            fitHeightToWidth(model.width, model.height)
            background = shimmer

            glide.load(model.urls?.regular)
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

    private fun getCardBackgroundColor(stringColor: String): Int {
        return fetchedColors.getOrPut(stringColor) {
            getColorWithAlpha(stringColor, 40)
        }
    }

    private fun getColorWithAlpha(stringColor: String?, /*from = 0, to = 255*/ alpha: Int): Int {
        return if (stringColor == null) {
            getThemeAttrColor(App.get(), R.attr.colorPrimary)
        } else {
            val hsv = FloatArray(3)
            Color.colorToHSV(Color.parseColor(stringColor), hsv)
            Color.HSVToColor(alpha, hsv)
        }
    }

    private fun ImageView.fitHeightToWidth(imageWidth: Int, imageHeight: Int) {
        val scaleFactor = imageWidth.toFloat() / imageHeight
        minimumHeight = (resources.displayMetrics.widthPixels / scaleFactor).toInt()
    }
}