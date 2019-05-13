package com.netchar.wallpaperify.ui.photosdetails

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.netchar.common.base.BaseFragment
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.fragment_photo_details.*

class PhotoDetailsFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_photo_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val safeArgs = PhotoDetailsFragmentArgs.fromBundle(it)
            photo_details_image.transitionName = safeArgs.imageTransitionName
            Glide.with(this)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .load(safeArgs.photoUrl)
                    .thumbnail(0.2f)
                    .dontAnimate()
                    .into(photo_details_image)
        }
    }
}
