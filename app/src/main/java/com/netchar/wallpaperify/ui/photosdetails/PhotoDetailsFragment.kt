/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.wallpaperify.ui.photosdetails

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updatePadding
import androidx.transition.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.showToast
import com.netchar.common.extensions.toGone
import com.netchar.common.extensions.toVisible
import com.netchar.common.utils.getThemeAttrColor
import com.netchar.wallpaperify.R
import com.transitionseverywhere.extra.Scale
import kotlinx.android.synthetic.main.fragment_photo_details.*


class PhotoDetailsFragment : BaseFragment() {

    private var isMenuOpen: Boolean = false
    var interpolator = OvershootInterpolator()

    override val layoutResId: Int = R.layout.fragment_photo_details

    private val sharedElementEnterTransitionSet: Transition by lazy {
        TransitionInflater.from(context).inflateTransition(android.R.transition.move).apply {
            duration = 200
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
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

    private val initialFabTranslationY = 100f

    fun initFabs() {
        photo_details_floating_download.alpha = 0f
        photo_details_floating_raw.alpha = 0f
        photo_details_floating_wallpaper.alpha = 0f
        textView3.alpha = 0f
        textView4.alpha = 0f
        textView5.alpha = 0f

        textView3.translationX = initialFabTranslationY * 2
        textView4.translationX = initialFabTranslationY * 2
        textView5.translationX = initialFabTranslationY * 2

        photo_details_floating_download.translationY = initialFabTranslationY
        photo_details_floating_raw.translationY = initialFabTranslationY
        photo_details_floating_wallpaper.translationY = initialFabTranslationY

        photo_details_floating_download.setOnClickListener(fabClickListener)
        photo_details_floating_raw.setOnClickListener(fabClickListener)
        photo_details_floating_wallpaper.setOnClickListener(fabClickListener)

        photo_details_floating_action_btn.setOnClickListener(fabClickListener)
        fab_overlay.setOnClickListener(fabClickListener)

        fab_overlay.toGone()
    }

    private fun openMenu() {
        isMenuOpen = !isMenuOpen

        photo_details_floating_action_btn.animate().setInterpolator(interpolator).rotation(180f).setDuration(300).start()

        photo_details_floating_download.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()
        textView3.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()

        photo_details_floating_raw.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()
        textView4.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()

        photo_details_floating_wallpaper.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()
        textView5.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()

        TransitionManager.beginDelayedTransition(photo_details_main_constraint)
        fab_overlay.toVisible()
    }

    private fun closeMenu() {
        isMenuOpen = !isMenuOpen

        photo_details_floating_action_btn.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start()

        photo_details_floating_download.animate().translationY(initialFabTranslationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start()
        textView3.animate().translationX(initialFabTranslationY * 2).alpha(0f).setInterpolator(interpolator).setDuration(300).start()

        photo_details_floating_raw.animate().translationY(initialFabTranslationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start()
        textView4.animate().translationX(initialFabTranslationY * 2).alpha(0f).setInterpolator(interpolator).setDuration(300).start()

        photo_details_floating_wallpaper.animate().translationY(initialFabTranslationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start()
        textView5.animate().translationX(initialFabTranslationY * 2).alpha(0f).setInterpolator(interpolator).setDuration(300).start()

        TransitionManager.beginDelayedTransition(photo_details_main_constraint)
        fab_overlay.toGone()
    }

    private val fabClickListener = View.OnClickListener {
        //        when (it.id) {
//            R.id.photo_details_menu_item_wallpaper -> {
//                showToast("Wallpaper")
//            }
//            R.id.photo_details_menu_item_raw -> {
//                showToast("Raw")
//            }
//            R.id.photo_details_menu_item_download -> {
//                showToast("Download")
//            }
//        }
        if (isMenuOpen) {
            closeMenu()
        } else {
            openMenu()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTransparentStatusBars()
        applyWindowsInsets()
        initFabs()

        arguments?.let { args ->
            postponeEnterTransition()
            val safeArgs = PhotoDetailsFragmentArgs.fromBundle(args)
            photo_details_image.transitionName = safeArgs.imageTransitionName

//            photo_details_floating_action_btn.inflate(R.menu.menu_photo_details_fab)
//            photo_details_floating_action_btn.setOnActionSelectedListener { actionItem ->
//                when (actionItem.id) {
//                    R.id.photo_details_menu_item_wallpaper -> {
//                        showToast("Wallpaper")
//                    }
//                    R.id.photo_details_menu_item_raw -> {
//                        showToast("Raw")
//                    }
//                    R.id.photo_details_menu_item_download -> {
//                        showToast("Download")
//                    }
//                }
//
//                photo_details_floating_action_btn.close(true)
//                true
//            }

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_photo_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.photo_details_share_menu_item -> {
                showToast("Share")
            }
        }
        return super.onOptionsItemSelected(item)
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
