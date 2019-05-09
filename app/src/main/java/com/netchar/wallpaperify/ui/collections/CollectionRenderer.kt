package com.netchar.wallpaperify.ui.collections

import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.netchar.common.extensions.fitWidth
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer
import com.netchar.common.utils.formatDate
import com.netchar.common.utils.getThemeAttrColor
import com.netchar.common.utils.parseColor
import com.netchar.models.Collection
import com.netchar.models.Photo
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.App
import kotlinx.android.synthetic.main.row_collection.view.*

/**
 * Created by Netchar on 09.05.2019.
 * e.glushankov@gmail.com
 */
class CollectionRenderer(private val glide: RequestManager, val onClickListener: (Collection) -> Unit) : ItemRenderer() {

    companion object {
        val fetchedColors = hashMapOf<String, Int>()
    }

    override val renderKey: String = this::class.java.name

    override fun layoutRes() = R.layout.row_collection

    private lateinit var collection: CollectionRecyclerItem

    override fun onSetListeners(itemView: View) {
        itemView.setOnClickListener {
            if (::collection.isInitialized) {
                val data = collection.data
                onClickListener(data)
            }
        }
    }

    override fun bind(itemView: View, item: IRecyclerItem) = with(itemView) {
        collection = item as CollectionRecyclerItem

        val data = collection.data
        val photo = data.coverPhoto
        val color = getPhotoColor(photo.color)

        collections_content_container.setBackgroundColor(color)
        setupImage(this, photo)

        val date = formatDate(data.publishedAt, "MMM d, YYYY")
        collection_published_txt.text = date
        collection_photo_count_text.text = context.getString(R.string.collection_item_photo_count_postfix, data.totalPhotos)
        collections_author_txt.text = context.getString(R.string.collection_item_author_prefix, data.user.name)
        collections_title_txt.text = data.title
    }

    private fun setupImage(itemView: View, coverPhoto: Photo) {
        val shimmer = getShimmer()

        with(itemView.collections_image) {
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

    private fun getShimmer(): ShimmerDrawable {
        return ShimmerDrawable().apply {
            Shimmer.AlphaHighlightBuilder()
                .setHighlightAlpha(0.85f)
                .setBaseAlpha(0.8f)
                .setAutoStart(true)
                .setDuration(1500)
                .setIntensity(0.2f)
                .build()
                .also { setShimmer(it) }
        }
    }

    private fun getPhotoColor(stringColor: String): Int {
        return fetchedColors.getOrPut(stringColor) {
            stringColor.parseColor(0x40, getThemeAttrColor(App.get(), R.attr.colorPrimary))
        }
    }
}