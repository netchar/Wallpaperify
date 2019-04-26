package com.netchar.wallpaperify.ui.latest

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.raw_photo.view.*


class LatestPhotosRenderer(val listener: (com.netchar.models.Photo) -> Unit) : com.netchar.common.poweradapter.item.ItemRenderer() {

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

    override fun bind(itemView: View, item: com.netchar.common.poweradapter.item.IRecyclerItem) {
        photo = item as LatestPhotoRecyclerItem

        itemView.row_photo_iv.fitWidth(photo.data.width, photo.data.height)

        Glide.with(itemView.context)
            .load(photo.data.urls?.regular)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(itemView.row_photo_iv)
    }

    private fun ImageView.fitWidth(imageWidth: Int, imageHeight: Int) {
        val scaleFactor = imageWidth.toFloat() / imageHeight
        minimumHeight = (resources.displayMetrics.widthPixels / scaleFactor).toInt()
    }
}