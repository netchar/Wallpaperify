package com.netchar.wallpaperify.ui.photosdetails

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.netchar.common.base.BaseFragment
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.fragment_photo_details.*

class PhotoDetailsFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_photo_details

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        view.setOnApplyWindowInsetsListener { view2, insets ->
////            var consumed = false
////            (view2 as ViewGroup).forEach { child ->
////                // Dispatch the insets to the child
////                val childResult = child.dispatchApplyWindowInsets(insets)
////                // If the child consumed the insets, record it
////                if (childResult.isConsumed) {
////                    consumed = true
////                }
////            }
////
////            // If any of the children consumed the insets, return
////            // an appropriate value
////            if (consumed) insets.consumeSystemWindowInsets() else insets
////        }

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

//        ViewCompat.setOnApplyWindowInsetsListener(view.toolbar) { v, insets ->
//            v.updatePadding(top = insets.systemWindowInsetTop, bottom = 0)
////            v.requestLayout()
//            insets
//        }

        arguments?.let {
            val safeArgs = PhotoDetailsFragmentArgs.fromBundle(it)
            postponeEnterTransition()
            photo_details_image.transitionName = safeArgs.imageTransitionName

            Glide.with(this)
                .load(safeArgs.photoUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }
                })
                .into(photo_details_image)

//            photo_details_image.setOnApplyWindowInsetsListener { view, windowInsets -> windowInsets }
//            ViewCompat.requestApplyInsets(view)

//            view.toolbar.setOnApplyWindowInsetsListener { v, windowInsets ->
//                v.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
//                windowInsets
//            }
        }
    }

}
