package com.netchar.wallpaperify.ui.home

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.netchar.poweradapter.item.IRecyclerItem
import com.netchar.poweradapter.item.ItemRenderer
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.data.remote.dto.Photo
import kotlinx.android.synthetic.main.raw_photo.view.*


class PhotosRenderer(val listener: (Photo) -> Unit) : ItemRenderer() {

    override val renderKey: String = this::class.java.name

    override fun layoutRes() = R.layout.raw_photo

    private lateinit var photo: NewPhotoRecyclerItem

    override fun onSetListeners(itemView: View) {
        itemView.setOnClickListener {
            if (::photo.isInitialized) {
                listener(photo.data)
            }
        }
    }

    override fun bind(itemView: View, item: IRecyclerItem) {
        photo = item as NewPhotoRecyclerItem

        itemView.row_photo_iv.fitWidth(photo.data.width, photo.data.height)

        Glide.with(itemView.context)
            .load(photo.data.urls.regular)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(itemView.row_photo_iv)
    }

    private fun ImageView.fitWidth(imageWidth: Int, imageHeight: Int) {
        val scaleFactor = imageWidth.toFloat() / imageHeight
        minimumHeight = (resources.displayMetrics.widthPixels / scaleFactor).toInt()
    }
}