package com.netchar.wallpaperify.ui.photosdetails

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.transition.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.netchar.common.base.BaseFragment
import com.netchar.common.extensions.toVisible
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.fragment_photo_details.*

class PhotoDetailsFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_photo_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val transition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
//        exitTransition= TransitionInflater.from(context).inflateTransition(android.R.transition.explode)
        transition.addListener(object : TransitionListenerAdapter() {

            override fun onTransitionEnd(transition: Transition) {

                toolbar?.let {
                    val toolbarSet = TransitionSet().apply {
                        addTransition(Fade())
                        addTarget(it)
                        addTarget(photo_details_floating_action_btn)
//                        addTarget(photo_details_bottom_sheet_layout)
                    }
                    TransitionManager.beginDelayedTransition(photo_details_coordinator, toolbarSet)
                    it.toVisible()
                    photo_details_floating_action_btn.toVisible()
//                    photo_details_bottom_sheet_layout.toVisible()
                }

//
//                val set = TransitionSet().apply {
//                    addTransition(Scale(0.3f))
//                    addTransition(Fade())
//                    addTarget(photo_details_bottom_sheet)
//                }
//                TransitionManager.beginDelayedTransition(photo_details_bottom_sheet, set)
//                photo_details_bottom_sheet.toVisible()
            }
        })
    }

//    inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
//        if (layoutParams is T) block(layoutParams as T)
//    }
//
//    fun View.margin(left: Float? = null, top: Float? = null, right: Float? = null, bottom: Float? = null) {
//        layoutParams<ViewGroup.MarginLayoutParams> {
//            left?.run { leftMargin = this) }
//            top?.run { topMargin = dpToPx(this) }
//            right?.run { rightMargin = dpToPx(this) }
//            bottom?.run { bottomMargin = dpToPx(this) }
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
//        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        val flags = activity?.window?.decorView?.systemUiVisibility!!
        activity?.window?.decorView?.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        activity?.window?.navigationBarColor = getThemeAttrColor(context!!, android.R.attr.navigationBarColor)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        var flags = activity?.window?.decorView?.systemUiVisibility!!
        flags = flags xor View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        activity?.window?.decorView?.systemUiVisibility = flags

//        activity?.window?.decorView?.systemUiVisibility  = flags
//        activity?.window?.navigationBarColor = Color.BLACK
        photo_details_coordinator.setOnApplyWindowInsetsListener { _, windowInsets ->
            toolbar?.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
//            photo_details_bottom_sheet_layout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
//                setMargins(0,0,0, windowInsets.systemWindowInsetBottom)
//            }
//            photo_details_floating_action_btn.updatePadding(bottom = 0)
            windowInsets.consumeSystemWindowInsets()
        }

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

//            val bottomSheetBehavior = BottomSheetBehavior.from(photo_details_bottom_sheet)
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    when (newState) {
//                        BottomSheetBehavior.STATE_EXPANDED -> {
////                            textBottom.text = getString(R.string.slide_down)
//                            //textFull.visibility = View.VISIBLE
//                        }
//                        BottomSheetBehavior.STATE_COLLAPSED -> {
////                            textBottom.text = getString(R.string.slide_up)
//                            //textFull.visibility = View.GONE
//                        }
//                    }
//                }
//
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//                }
//            })
        }
    }

//    private fun AnimateEnter() {
//        val constraintSet = ConstraintSet()
//        constraintSet.clone(context!!, R.layout.fragment_photo_details)
//    }

}
