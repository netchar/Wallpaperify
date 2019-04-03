package com.netchar.wallpaperify.ui.home

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.netchar.poweradapter.item.ItemRenderer
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.data.remote.dto.Photo
import kotlinx.android.synthetic.main.raw_photo.view.*

class PhotosRenderer(val listener: (Photo) -> Unit) : ItemRenderer<NewPhotoRecyclerItem>() {

    override val renderKey: String = this::class.java.name

    override fun layoutRes() = R.layout.raw_photo

    private lateinit var photo: Photo

    override fun onSetListeners(itemView: View) {
        itemView.setOnClickListener {
            if (::photo.isInitialized) {
                listener(photo)
            }
        }
    }

    override fun onBind(itemView: View, model: NewPhotoRecyclerItem) {
        photo = model.data
        Glide.with(itemView.context)
            .load(photo.urls.regular)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(itemView.row_photo_iv)
    }
}