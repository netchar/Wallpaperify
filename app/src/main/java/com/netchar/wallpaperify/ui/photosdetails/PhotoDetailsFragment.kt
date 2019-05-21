package com.netchar.wallpaperify.ui.photosdetails

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
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
import com.transitionseverywhere.extra.Scale
import kotlinx.android.synthetic.main.fragment_photo_details.*

class PhotoDetailsFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_photo_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val transition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
//        transition.duration = 2000
//        transition.interpolator = BounceInterpolator()
        val transition = TransitionSet()
        transition.duration = 250
        transition.addTransition(ChangeBounds())
        transition.addTransition(ChangeTransform())
        transition.addTransition(ChangeClipBounds())
        transition.addTransition(ChangeImageTransform())

        sharedElementEnterTransition = transition
//        val enterTrans = Fade()
//        enterTrans.duration = 1000
//        enterTrans.interpolator = AccelerateDecelerateInterpolator()
//        enterTransition = enterTrans

//        exitTransition= TransitionInflater.from(context).inflateTransition(android.R.transition.explode)
        transition.addListener(object : TransitionListenerAdapter() {

            override fun onTransitionEnd(transition: Transition) {

                toolbar?.let {
                    //                    val toolbarSet = TransitionSet().apply {
//                        addTransition(Fade())
//                        addTarget(it)
//                        addTarget(photo_details_bottom_sheet_layout)
//                    }
//                    TransitionManager.beginDelayedTransition(photo_details_coordinator, toolbarSet)
//                    it.toVisible()
//                    photo_details_bottom_sheet_layout.toVisible()

                    val set = TransitionSet()

                    val first = Scale(0.3f)
                    first.addTarget(photo_details_floating_action_btn)

                    val second = Fade()
                    second.addTarget(it)


                    val third = ChangeBounds()
                    third.addTarget(photo_details_bottom_sheet_layout)

                    set.setOrdering(TransitionSet.ORDERING_TOGETHER)
                            .addTransition(first)
                            .addTransition(second)
                            .addTransition(third)

                    TransitionManager.beginDelayedTransition(photo_details_coordinator, set)

                    photo_details_floating_action_btn.toVisible()
                    it.toVisible()
                    photo_details_bottom_sheet_layout.translationY = 0f

//                    val ss = ConstraintSet()
//                    ss.setTranslationY(photo_details_bottom_sheet_layout.id, -400f)
//                    ss.applyTo(photo_details_bottom_sheet_layout)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        val flags = activity?.window?.decorView?.systemUiVisibility!!
//        activity?.window?.decorView?.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        activity?.window?.navigationBarColor = getThemeAttrColor(context!!, android.R.attr.navigationBarColor)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        view.setOnApplyWindowInsetsListener { view, insets ->
//            var consumed = false
//
//            (view as ViewGroup).forEach { child ->
//                // Dispatch the insets to the child
//                val childResult = child.dispatchApplyWindowInsets(insets)
//                // If the child consumed the insets, record it
//                if (childResult.isConsumed) {
//                    consumed = true
//                }
//            }
//
//            // If any of the children consumed the insets, return
//            // an appropriate value
//            if (consumed) insets.consumeSystemWindowInsets() else insets
//        }


//        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        photo_details_coordinator.setOnApplyWindowInsetsListener { v, insets ->
//            insets
//        }
//        photo_details_coordinator.requestApplyInsetsWhenAttached()

//        var flags = activity?.window?.decorView?.systemUiVisibility!!
//        flags = flags xor View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        activity?.window?.decorView?.systemUiVisibility = flags

//        activity?.window?.decorView?.systemUiVisibility  = flags
//        activity?.window?.navigationBarColor = Color.BLACK
        photo_details_coordinator.setOnApplyWindowInsetsListener { _, windowInsets ->
            toolbar?.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
            photo_details_bottom_sheet_layout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                setMargins(0, 0, 0, windowInsets.systemWindowInsetBottom)
            }
//            photo_details_floating_action_btn.updatePadding(bottom = 0)
            windowInsets.consumeSystemWindowInsets()
        }

//
//        photo_details_bottom_sheet.getConstraintSet(R.id.start)?.let { startConstraintSet ->
//            startConstraintSet.constrainMinHeight(R.id.photo_details_bottom_sheet_layout, 500.dp)
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


//            view.dispatchApplyWindowInsets(activity!!.window.decorView.rootWindowInsets)

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
