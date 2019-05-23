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
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.*
import com.netchar.common.utils.getThemeAttrColor
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_photo_details.*
import javax.inject.Inject

class PhotoDetailsFragment : BaseFragment() {

    private var isMenuOpen: Boolean = false
    private var interpolator = OvershootInterpolator()
    private val initialFabTranslationY = 100f
    private val initialFabLabelTranslationX = 100f

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: PhotoDetailsViewModel

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            val safeArgs = PhotoDetailsFragmentArgs.fromBundle(args)

            startShimmer()

            viewModel = injectViewModel(viewModelFactory)

            setTransparentStatusBars()
            applyWindowsInsets()
            disableToolbarTitle()
            initFabs()
            initViews(safeArgs)
            observe()

            viewModel.fetchPhoto(safeArgs.photoId)
        }
    }

    private fun initViews(safeArgs: PhotoDetailsFragmentArgs) {
        postponeEnterTransition()

        photo_details_likes_txt.toGone()
        photo_details_total_downloads_txt.toGone()
        photo_details_photo_by_txt.toGone()
        photo_details_description.toGone()
        photo_details_author_img.toInvisible()

        photo_details_image.transitionName = safeArgs.imageTransitionName
        Glide.with(this)
                .load(safeArgs.photoUrl)
                .listener(photoTransitionRequestListener)
                .into(photo_details_image)

    }

    private fun startShimmer() {
        photo_details_loading_shimmer.toVisible()
        photo_details_loading_shimmer.startShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()

        sharedElementEnterTransitionSet.removeListener(transitionListenerAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        restoreStatusBarsThemeColors()
        stopShimmer()
    }

    private fun observe() {
        viewModel.photo.observe(viewLifecycleOwner, Observer { photo ->
            Glide.with(this)
                    .load(photo.user.profileImage.small)
                    .transform(CircleCrop())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_person)
                    .into(photo_details_author_img)

            TransitionManager.beginDelayedTransition(photo_details_main_constraint)

            stopShimmer()

            photo_details_photo_by_txt.text = getString(R.string.collection_item_author_prefix, photo.user.name)
            photo_details_description.apply { text = photo.description }
            photo_details_likes_txt.text = photo.likes.toString()
            photo_details_total_downloads_txt.text = photo.downloads.toString()

            photo_details_likes_txt.toVisible()
            photo_details_total_downloads_txt.toVisible()
            photo_details_photo_by_txt.toVisible()
            photo_details_author_img.toVisible()

            if (photo_details_description.text.isNotBlank()) {
                photo_details_description.toVisible()
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            showToast(getStringSafe(error.messageRes))
        })
    }

    private fun disableToolbarTitle() {
        fragmentToolbar?.title = ""
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

    override fun onBackPressed(): Boolean {

        if (isMenuOpen) {
            closeMenu()
            return true
        }

        return false
    }

    private val transitionListenerAdapter = object : TransitionListenerAdapter() {

        override fun onTransitionEnd(transition: Transition) {

            fragmentToolbar?.let { toolbar ->
                val transitionSet = TransitionInflater.from(context).inflateTransition(R.transition.photo_details_transition_content_enter)
                TransitionManager.beginDelayedTransition(photo_details_coordinator, transitionSet)

                photo_details_bottom_panel_background_overlay.toVisible()
                photo_details_bottom_panel_constraint.toVisible()
                photo_details_floating_main.toVisible()
                toolbar.toVisible()
            }
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
            photo_details_bottom_panel_constraint.updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = windowInsets.systemWindowInsetBottom }
            windowInsets.consumeSystemWindowInsets()
        }
    }

    private fun initFabs() {
        photo_details_floating_download.alpha = 0f
        photo_details_floating_raw.alpha = 0f
        photo_details_floating_wallpaper.alpha = 0f

        photo_details_floating_label_download.alpha = 0f
        photo_details_floating_label_raw.alpha = 0f
        photo_details_floating_label_wallpaper.alpha = 0f

        photo_details_floating_label_download.translationX = initialFabLabelTranslationX
        photo_details_floating_label_raw.translationX = initialFabLabelTranslationX
        photo_details_floating_label_wallpaper.translationX = initialFabLabelTranslationX

        photo_details_floating_download.translationY = initialFabTranslationY
        photo_details_floating_raw.translationY = initialFabTranslationY
        photo_details_floating_wallpaper.translationY = initialFabTranslationY

        photo_details_floating_download.toGone()
        photo_details_floating_raw.toGone()
        photo_details_floating_wallpaper.toGone()

        photo_details_floating_label_download.toGone()
        photo_details_floating_label_raw.toGone()
        photo_details_floating_label_wallpaper.toGone()

        val fabClickListener = View.OnClickListener {

            when (it.id) {
                R.id.photo_details_floating_download,
                R.id.photo_details_floating_label_download -> {
                    showToast("Download")
                }
                R.id.photo_details_floating_raw,
                R.id.photo_details_floating_label_raw -> {
                    showToast("Raw")
                }
                R.id.photo_details_floating_wallpaper,
                R.id.photo_details_floating_label_wallpaper -> {
                    showToast("Wallpaper")
                }
            }

            closeMenu()
        }

        photo_details_floating_download.setOnClickListener(fabClickListener)
        photo_details_floating_raw.setOnClickListener(fabClickListener)
        photo_details_floating_wallpaper.setOnClickListener(fabClickListener)

        photo_details_floating_label_download.setOnClickListener(fabClickListener)
        photo_details_floating_label_raw.setOnClickListener(fabClickListener)
        photo_details_floating_label_wallpaper.setOnClickListener(fabClickListener)

        photo_details_floating_main.setOnClickListener {
            if (isMenuOpen) {
                closeMenu()
            } else {
                openMenu()
            }
        }

        fab_overlay.setOnClickListener { closeMenu() }
        fab_overlay.toGone()
    }

    private fun openMenu() {
        isMenuOpen = !isMenuOpen

        animateRotateMainFab(180f)
        animateFabMenuItemOpen(photo_details_floating_download, photo_details_floating_label_download)
        animateFabMenuItemOpen(photo_details_floating_raw, photo_details_floating_label_raw)
        animateFabMenuItemOpen(photo_details_floating_wallpaper, photo_details_floating_label_wallpaper)
        animateOverlayShow()
    }

    private fun closeMenu() {
        isMenuOpen = !isMenuOpen

        animateRotateMainFab(0f)
        animateFabMenuItemClose(photo_details_floating_download, photo_details_floating_label_download)
        animateFabMenuItemClose(photo_details_floating_raw, photo_details_floating_label_raw)
        animateFabMenuItemClose(photo_details_floating_wallpaper, photo_details_floating_label_wallpaper)
        animateOverlayHide()
    }

    private fun animateOverlayShow() {
        TransitionManager.beginDelayedTransition(photo_details_main_constraint)
        fab_overlay.toVisible()
    }

    private fun animateRotateMainFab(angle: Float) {
        photo_details_floating_main.animate().setInterpolator(interpolator).rotation(angle).setDuration(300).start()
    }

    private fun animateOverlayHide() {
        TransitionManager.beginDelayedTransition(photo_details_main_constraint)
        fab_overlay.toGone()
    }

    private fun animateFabMenuItemOpen(fab: FloatingActionButton, label: TextView) {
        fab.animate().withStartAction { fab.toVisible() }.translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()
        label.animate().withStartAction { label.toVisible() }.setStartDelay(0).translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()
    }

    private fun animateFabMenuItemClose(fab: FloatingActionButton, label: TextView) {
        fab.animate().withEndAction { fab.toGone() }.translationY(initialFabTranslationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start()
        label.animate().withEndAction { label.toGone() }.translationX(initialFabLabelTranslationX).alpha(0f).setInterpolator(interpolator).setDuration(300).start()
    }

    private fun stopShimmer() {
        if (photo_details_loading_shimmer.isShimmerStarted) {
            photo_details_loading_shimmer.toGone()
            photo_details_loading_shimmer.stopShimmer()
        }
    }

    private val photoTransitionRequestListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            startPostponedEnterTransition()
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            startPostponedEnterTransition()
            return false
        }
    }
}
