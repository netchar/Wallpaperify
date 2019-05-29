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
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.view.animation.LinearInterpolator
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
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.*
import com.netchar.common.utils.ShimmerFactory
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

    val durationShort by lazy(LazyThreadSafetyMode.NONE) { resources.getInteger(android.R.integer.config_shortAnimTime).toLong() }
    val durationMedium by lazy(LazyThreadSafetyMode.NONE) { resources.getInteger(android.R.integer.config_shortAnimTime).toLong() }
    val durationLong by lazy(LazyThreadSafetyMode.NONE) { resources.getInteger(android.R.integer.config_shortAnimTime).toLong() }
    val interpolatorLinear by lazy(LazyThreadSafetyMode.NONE) { LinearInterpolator() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        sharedElementEnterTransition = inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Should play enter animation only on view creating and return ready view on Pop()
        var view: ViewGroup? = viewGroup
        return if (view == null) {
            view = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
            (view as View).background = ColorDrawable(Color.TRANSPARENT)
            initFab(view)
            initViews(view)
            view.also { viewGroup = it }
        } else {
            view
        }
    }

    private fun initFab(v: View) = with(v) {
        photo_details_fab.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val translationY = (photo_details_fab.measuredHeight / 2f) + 5.dpToPx()
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

    private fun initViews(v: View) = with(v) {
        photo_details_iv_photo.setOnClickListener {
            navigateToOriginalPhoto()
        }

        val shimmer = ShimmerFactory.getShimmer(autoStart = true)
        v.background = shimmer
        photo_details_iv_photo.background = shimmer
        photo_details_iv_photo.transitionName = safeArguments.imageTransitionName

        if (safeArguments.photoDescription.isEmpty()) {
            photo_details_shimmer_description.toGone()
        }

        photo_details_constraint_bottom_panel.alpha = 0f

        Glide.with(this)
                .load(safeArguments.photoUrl)
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        shimmer.stopShimmer()
                        v.background = null
                        startEnterAnimation()
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        shimmer.stopShimmer()
                        v.background = null
                        startEnterAnimation()
                        return false
                    }
                })
                .into(photo_details_iv_photo)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = injectViewModel(viewModelFactory)

        setTransparentStatusBars(true)
        hideToolbarTitle()
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
            Glide.with(this)
                    .load(photo.user.profileImage.small)
                    .transform(CircleCrop())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_person)
                    .into(photo_details_author_img)

            photo_details_tv_photo_by.text = getString(R.string.collection_item_author_prefix, photo.user.name)
            photo_details_tv_description.text = photo.description
            photo_details_tv_likes.text = photo.likes.toString()
            photo_details_tv_total_downloads.text = photo.downloads.toString()

            photo_details_tv_description.goneIfEmpty()

            photo_details_constraint_bottom_panel.animate().withStartAction {
                photo_details_constraint_bottom_panel.toVisible()
            }.alpha(1f).setDuration(750).start()
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            toast(getStringSafe(error.messageRes))
        })

        viewModel.loading.observe(viewLifecycleOwner, Observer { loading ->
            handleShimmer(loading)
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

        viewModel.wallpaper.observe(viewLifecycleOwner, Observer { uri ->
            // todo: find out why some times it's calling multiple times.
            setWallpaper(uri)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        setTransparentStatusBars(false)
    }

    private fun setWallpaper(uri: Uri) {
        try {
            Timber.d("Set wallpaper via WallpaperManager. Uri: $uri")
            val wallpaperManager = WallpaperManager.getInstance(this.context)
            wallpaperManager.getCropAndSetWallpaperIntent(uri)
                    .apply {
                        setDataAndType(uri, "image/*")
                        putExtra("mimeType", "image/*")
                    }.also {
                        startActivityForResult(it, 13451)
                    }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Timber.d("Set wallpaper via Chooser. Uri: $uri")

            val intent = Intent(Intent.ACTION_ATTACH_DATA).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                setDataAndType(uri, "image/*")
                putExtra("mimeType", "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, getString(R.string.titile_set_wallpaper_as)))
        }
    }

    private fun handleShimmer(loading: Boolean) {
        if (loading) {
            startShimmer()
        } else {
            stopShimmer()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_photo_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.photo_details_share_menu_item -> consume { toast("Share") }
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

    private fun startEnterAnimation() {
        val contentTransition = inflateTransition(R.transition.photo_details_transition_content_enter)
        contentTransition.onTransitionEnd {
            //            viewModel.fetchPhoto(safeArguments.photoId)
        }

        TransitionManager.beginDelayedTransition(photo_details_coordinator, contentTransition)

        photo_details_bottom_panel_background_overlay.toVisible()
        photo_details_fab.toVisible()
        fragmentToolbar?.toVisible()
    }

    private fun navigateToOriginalPhoto() {
        val photo = viewModel.photo.value
        if (photo != null) {
            val action = PhotoDetailsFragmentDirections.actionPhotoDetailsFragmentToPhotoRawFragment()
            action.photoUrl = photo.urls.raw
            findNavController().navigate(action)
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
            }.alpha(0f).setDuration(durationShort).start()
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
}
