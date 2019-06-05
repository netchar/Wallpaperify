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

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.*
import android.view.animation.Animation
import androidx.core.text.buildSpannedString
import androidx.core.text.underline
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.netchar.common.UNSPLASH_URL
import com.netchar.common.UNSPLASH_UTM_PARAMETERS
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.*
import com.netchar.common.utils.ShimmerFactory
import com.netchar.common.utils.share
import com.netchar.repository.pojo.PhotoPOJO
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_photo_details.*
import kotlinx.android.synthetic.main.fragment_photo_details.view.*
import kotlinx.android.synthetic.main.view_photo_details_shimmer.view.*
import timber.log.Timber
import javax.inject.Inject


class PhotoDetailsFragment : BaseFragment() {
    private var viewGroup: ViewGroup? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: PhotoDetailsViewModel

    override val layoutResId: Int = R.layout.fragment_photo_details

    private val safeArguments: PhotoDetailsFragmentArgs by lazy(LazyThreadSafetyMode.NONE) {
        PhotoDetailsFragmentArgs.fromBundle(arguments!!)
    }

    private val downloadDialog: DownloadDialogFragment by lazy(LazyThreadSafetyMode.NONE) {
        DownloadDialogFragment().apply {
            onDialogCancel = { viewModel.cancelDownloading() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Should play enter animation only on view creating and return ready view on Pop()
        var view: ViewGroup? = viewGroup
        return if (view == null) {
            view = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
            initViews(view)
            view.also { viewGroup = it }
        } else {
            view
        }
    }

    private fun initViews(contentView: View) = with(contentView) {
        initFabMenu(contentView)

        val shimmer = ShimmerFactory.getShimmer(autoStart = true)
        contentView.background = shimmer
        photo_details_iv_photo.setOnClickListener {
            navigateToOriginalPhoto()
        }

        if (safeArguments.photoDescription.isEmpty()) {
            photo_details_shimmer_description.toGone()
        }

        Glide.with(this)
            .load(safeArguments.photoUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    shimmer.stopShimmer()
                    contentView.background = null
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    shimmer.stopShimmer()
                    contentView.background = null
                    startPostponedEnterTransition()
                    return false
                }
            })
            .into(photo_details_iv_photo)
    }

    private fun initFabMenu(viewContainer: View) = with(viewContainer) {
        photo_details_fab.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val translationY = (photo_details_fab.measuredHeight / 2f) + dip(5)
        photo_details_fab.translationY = translationY
        photo_details_fab.setupWithOverlay(fab_overlay)
        photo_details_fab.addFabOption(R.drawable.ic_aspect_ratio, getString(R.string.photo_details_floating_label_title_raw)) {
            navigateToOriginalPhoto()
        }
        photo_details_fab.addFabOption(R.drawable.ic_wallpaper, getString(R.string.photo_details_floating_label_title_wallpaper)) {
            runWithPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE) {
                viewModel.downloadWallpaper()
            }
        }
        photo_details_fab.addFabOption(R.drawable.ic_file_download, getString(R.string.photo_details_floating_label_title_download)) {
            runWithPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE) {
                viewModel.downloadImage()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = injectViewModel(viewModelFactory)
        activity.setTransparentStatusBars(true)
        activity.setLightStatusBar(false)
        activity.setDisplayShowTitleEnabled(false)
        applyWindowsInsets(view)
        observe()
    }

    private fun applyWindowsInsets(v: View) = with(v) {
        photo_details_coordinator.setOnApplyWindowInsetsListener { _, windowInsets ->
            fragmentToolbar?.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
            photo_details_constraint_bottom_panel.updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = windowInsets.systemWindowInsetBottom }
            windowInsets.consumeSystemWindowInsets()
        }
    }

    private fun observe() {
        viewModel.photo.observe(viewLifecycleOwner, Observer { photo ->
            updateUiByPhotoDetails(photo)
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            toast(getStringSafe(error.messageRes))
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { loading ->
            if (!loading) {
                stopShimmer()
            }
        })

        viewModel.downloadDialog.observe(viewLifecycleOwner, Observer { dialogState ->
            if (dialogState.show) {
                downloadDialog.show(childFragmentManager, DownloadDialogFragment::class.java.simpleName)
            } else {
                downloadDialog.isDownloadFinished = !dialogState.isCanceled
                downloadDialog.dismiss()
            }
        })

        viewModel.downloadProgress.observe(viewLifecycleOwner, Observer { progress ->
            if (progress > 0 && downloadDialog.dialog?.isShowing == true) {
                downloadDialog.setProgress(progress)
            }
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer { message ->
            message.messageRes?.let { toast(it) }
        })

        viewModel.overrideDialog.observe(viewLifecycleOwner, Observer { dialogState ->
            if (dialogState.show) {
                overrideDialog.show()
            } else {
                overrideDialog.dismiss()
            }
        })
    }

    private fun updateUiByPhotoDetails(photo: PhotoPOJO) {
        Glide.with(this)
                .load(photo.user.profileImage.small)
                .transform(CircleCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_person)
                .into(photo_details_author_img)


        val photoByText = buildSpannedString {
            append("${getString(R.string.photo_details_author_prefix)} ")
            underline { append(photo.user.name) }.withClickableSpan(photo.user.name) {
                context?.openWebPage(photo.user.links.profileLink)
            }
            append(" ${getString(R.string.photo_details_author_middle_part)} ")
            underline { append(getString(R.string.label_unsplash)) }.withClickableSpan(getString(R.string.label_unsplash)) {
                context?.openWebPage(UNSPLASH_URL + UNSPLASH_UTM_PARAMETERS)
            }
        }

        photo_details_tv_photo_by.text = photoByText
        photo_details_tv_photo_by.movementMethod = LinkMovementMethod.getInstance()
        photo_details_tv_description.text = photo.description
        photo_details_tv_likes.text = photo.likes.toString()
        photo_details_tv_total_downloads.text = photo.downloads.toString()
        photo_details_tv_description.goneIfEmpty()
        photo_details_constraint_bottom_panel.run { animate().withStartAction { toVisible() }.alpha(1f).setDuration(450).start() }

        TransitionManager.beginDelayedTransition(photo_details_coordinator, inflateTransition(R.transition.photo_details_fab_transition))
        photo_details_fab.toVisible()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_photo_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.photo_details_share_menu_item -> consume {
                viewModel.photo.value?.let {
                    activity?.share(it.photoShareLink, "Photo by ${it.user.name}")
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed(): Boolean {
        if (photo_details_fab.isMenuOpen) {
            photo_details_fab.closeFabMenu()
            return true
        }

        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        viewGroup?.removeAllViews()
        viewGroup = null
    }

    override fun onEnterAnimationComplete() {
        startEnterAnimation()
    }

    private fun startEnterAnimation() {
        photo_details_coordinator?.run {
            val contentTransition = inflateTransition(R.transition.photo_details_transition_content_enter)
            contentTransition.onTransitionEnd {
                viewModel.fetchPhoto(safeArguments.photoId)
            }

            TransitionManager.beginDelayedTransition(photo_details_coordinator, contentTransition)

            photo_details_bottom_panel_background_overlay.toVisible()
            fragmentToolbar?.toVisible()
            startShimmer()
        }
    }

    private fun navigateToOriginalPhoto() {
        val photo = viewModel.photo.value

        if (photo != null) {
            val action = PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToPhotoRawFragment()
            action.photoUrl = photo.urls.raw

            try {
                findNavController().navigate(action)
            } catch (ex: IllegalArgumentException) {
                Timber.e(ex)
            }
        }
    }

    private fun startShimmer() {
        photo_details_loading_shimmer.toVisible()
        photo_details_loading_shimmer.startShimmer()
    }

    private fun stopShimmer() {
        photo_details_loading_shimmer?.let {
            it.animate().withEndAction {
                it.toGone()
                it.stopShimmer()
            }.alpha(0f).setDuration(350).start()
        }
    }

    private val overrideDialog: AlertDialog by lazy {
        AlertDialog.Builder(activity).apply {
            setTitle(getString(R.string.message_dialog_error_title_photo_exists))
            setMessage(getString(R.string.message_dialog_photo_already_exists))
            setPositiveButton(getString(R.string.label_override)) { _, _ ->
                viewModel.overrideDownloadedPhoto()
            }
            setNegativeButton(getString(R.string.label_cancel), null)
        }.create()
    }

    /**
     * Temporary workaround.
     *
     * Navigation library exit transition have an incorrect Z ordering
     * When a FragmentTransaction is executed, the FragmentManger removes the current fragment view
     * and adds the new fragment view to the container. Normally when a view is removed, it won't be
     * drawn. However, when removing a View, if there is an animation currently playing on it, ViewGroup
     * will add it to a special list of disappearing views
     * (https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/view/ViewGroup.java#4713)
     *
     * During dispatchDraw, ViewGroup draws disappearing views at the end
     * (https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/android/view/ViewGroup.java#3531)
     * This ignore the original z order of views and always draws exiting views on top.
     *
     * This behavior makes it impossible to do a modal animation where a new Fragment slides up
     * over the existing content because the existing content will be drawn on top of the new Fragment.
     *
     * However, there is no such problem when popping the modal because you want the exiting fragment
     * to draw on top.
     *
     * Issue already tracked by google: https://issuetracker.google.com/issues/79443865
     */
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animation: Animation? = super.onCreateAnimation(transit, enter, nextAnim)

        view?.let {
            if (nextAnim == R.anim.anim_fragment_details_enter || nextAnim == R.anim.anim_fragment_details_pop_exit) {
                ViewCompat.setTranslationZ(it, 1f)
            } else {
                ViewCompat.setTranslationZ(it, 0f)
            }
        }
        return animation
    }
}
