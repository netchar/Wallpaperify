package com.netchar.wallpaperify.ui.photosdetails

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updatePadding
import androidx.transition.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.toVisible
import com.netchar.common.utils.getThemeAttrColor
import com.netchar.wallpaperify.R
import com.transitionseverywhere.extra.Scale
import kotlinx.android.synthetic.main.fragment_photo_details.*

class PhotoDetailsFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_photo_details

    private val sharedElementEnterTransitionSet: Transition by lazy {
        TransitionInflater.from(context).inflateTransition(android.R.transition.move).apply {
            duration = 200
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition = sharedElementEnterTransitionSet
        sharedElementEnterTransitionSet.addListener(transitionListenerAdapter)
    }

    override fun onDestroy() {
        super.onDestroy()

        sharedElementEnterTransitionSet.removeListener(transitionListenerAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        restoreStatusBarsThemeColors()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTransparentStatusBars()
        applyWindowsInsets()

        arguments?.let { args ->
            postponeEnterTransition()
            val safeArgs = PhotoDetailsFragmentArgs.fromBundle(args)
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
        }
    }

    private val transitionListenerAdapter = object : TransitionListenerAdapter() {

        override fun onTransitionEnd(transition: Transition) {

            fragmentToolbar?.let { toolbar ->

                val set = getPreparedTransition(toolbar)

                TransitionManager.beginDelayedTransition(photo_details_coordinator, set)

                photo_details_bottom_panel_constraint.toVisible()
                photo_details_floating_action_btn.toVisible()
                toolbar.toVisible()
            }
        }

        private fun getPreparedTransition(toolbar: Toolbar): TransitionSet {

            val titleAndBottomPanelTransition = TransitionSet().apply {
                addTransition(Fade())
                duration = 250
                interpolator = LinearInterpolator()
                addTarget(photo_details_bottom_panel_constraint)
                addTarget(toolbar)
            }

            val fabButtonTransition = TransitionSet().apply {
                addTransition(Scale(0.2f))
                addTransition(Fade())
                addTarget(photo_details_floating_action_btn)
            }

            return TransitionSet()
                    .setOrdering(TransitionSet.ORDERING_SEQUENTIAL)
                    .addTransition(titleAndBottomPanelTransition)
                    .addTransition(fabButtonTransition)
        }
    }

    private fun restoreStatusBarsThemeColors() {
        activity?.let {
            it.window.statusBarColor = getThemeAttrColor(it, android.R.attr.statusBarColor)
            it.window.navigationBarColor = getThemeAttrColor(it, android.R.attr.navigationBarColor)
        }
    }

    private fun setTransparentStatusBars() {
        activity?.let {
            it.window.statusBarColor = Color.TRANSPARENT
            it.window.navigationBarColor = Color.TRANSPARENT
        }
    }

    private fun applyWindowsInsets() {
        photo_details_coordinator.setOnApplyWindowInsetsListener { _, windowInsets ->
            fragmentToolbar?.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
            photo_details_bottom_panel_constraint.updatePadding(bottom = windowInsets.systemWindowInsetBottom)
            windowInsets.consumeSystemWindowInsets()
        }
    }
}
