package com.netchar.wallpaperify.ui.photorawview

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.toast
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.fragment_photo_raw.*

class PhotoRawFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_photo_raw

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyWindowsInsets()

        arguments?.let {
            val safeArgs = PhotoRawFragmentArgs.fromBundle(it)

            photo_raw_progress.show()
            Glide.with(this)
                .load(safeArgs.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        toast(getString(R.string.message_error_failed_to_load_image))
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        photo_raw_progress.hide()
                        return false
                    }
                })
                .into(photo_raw_image)
        }
    }

    private fun applyWindowsInsets() {
        photo_raw_image.setOnApplyWindowInsetsListener { _, windowInsets ->
            fragmentToolbar?.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
            windowInsets.consumeSystemWindowInsets()
        }
    }
}