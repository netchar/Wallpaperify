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

package com.netchar.common.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.updatePadding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.netchar.common.R
import com.netchar.common.extensions.dpToPx
import com.netchar.common.extensions.toGone
import com.netchar.common.extensions.toVisible
import kotlinx.android.synthetic.main.view_fab_container.view.*
import kotlinx.android.synthetic.main.view_fab_menu_item.view.*
import java.util.*
import kotlin.collections.ArrayList


class FloatingActionMenuButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    interface OnMenuOptionsListener {
        fun onOpenMenuOptions()

        fun onCloseMenuOptions()
    }

    private val DEFAULT_TRANSLATION_TIME = 250
    private val MINI_FAB_FADE_IN_TIME_OFFSET = 20
    private val MAIN_FAB_SIZE = 56f
    private val MINI_FAB_SIZE = 40f
    private val MINI_FAB_OFFSET = 2f

    private var menuTranslationTimeOpen = DEFAULT_TRANSLATION_TIME
    private var menuTranslationTimeHide = DEFAULT_TRANSLATION_TIME
    private var menuTimeFadeIn = DEFAULT_TRANSLATION_TIME
    private var menuTimeFadeOut = DEFAULT_TRANSLATION_TIME
    private var mainFabRotationTime = DEFAULT_TRANSLATION_TIME
    private var mainFabRotationDegrees = 90
    private var mainFabMarginRight = 16
    private var mainFabMarginBottom = 16
    private var optionFabMargin = 10
    private var mainFabColorBackground: Int = R.color.colorAccent
    private var mainFabColorIcon: Int = android.R.color.white
    private var mainFabIconStart = 0
    private var mainFabIconEnd = 0
    private var optionFabColorBackground: Int = android.R.color.white
    private var optionFabColorIcon: Int = android.R.color.black
    private var optionFabTextSize = 14f
    private var optionFabTextColor: Int = android.R.color.black
    private var optionFabTextBackground = 0

    private lateinit var inflater: LayoutInflater
    private lateinit var mainFab: FloatingActionButton
    private lateinit var mainFabAction: () -> Unit
    private lateinit var mainFabContainer: ConstraintLayout
    private var optionFabContainers = LinkedList<View>()
    private var optionFabs = ArrayList<View>()
    private var optionFabTitles = ArrayList<View>()
    private var optionFabActions = ArrayList<() -> Unit>()
    private var isFabOptionsOpen = false

//    constructor(context: Context) : this(context, null)
//
//    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
//
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
//        context.withStyledAttributes(attrs, R.styleable.FloatingActionMenuButton, defStyleAttr, 0) {
//            menuTranslationTimeOpen = getInt(R.styleable.FloatingActionMenuButton_menu_translation_time_open, menuTranslationTimeOpen)
//            menuTranslationTimeHide = getInt(R.styleable.FloatingActionMenuButton_menu_translation_time_hide, menuTranslationTimeHide)
//            menuTimeFadeIn = getInt(R.styleable.FloatingActionMenuButton_menu_time_fade_in, menuTimeFadeIn)
//            menuTimeFadeOut = getInt(R.styleable.FloatingActionMenuButton_menu_time_fade_out, menuTimeFadeOut)
//            mainFabRotationTime = getInt(R.styleable.FloatingActionMenuButton_main_fab_rotation_time, mainFabRotationTime)
//            mainFabRotationDegrees = getInt(R.styleable.FloatingActionMenuButton_main_fab_rotation_degrees, mainFabRotationDegrees)
//
//            mainFabColorBackground = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_color_background, mainFabColorBackground)
//            mainFabColorIcon = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_color_icon, mainFabColorIcon)
//            mainFabIconStart = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_icon_start, mainFabIconStart)
//            mainFabIconEnd = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_icon_end, mainFabIconEnd)
//
//            optionFabColorBackground = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_color_background, optionFabColorBackground)
//            optionFabColorIcon = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_color_icon, optionFabColorIcon)
//            optionFabTextSize = getDimensionPixelSize(R.styleable.FloatingActionMenuButton_option_fab_text_size, optionFabTextSize.dpToPx().toInt()).toFloat()
//            optionFabTextColor = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_text_color, optionFabTextColor)
//            optionFabTextBackground = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_text_background, optionFabTextBackground)
//
//            mainFabMarginRight = getDimensionPixelSize(R.styleable.FloatingActionMenuButton_main_fab_margin_right, mainFabMarginRight.dpToPx().toInt())
//            mainFabMarginBottom = getDimensionPixelSize(R.styleable.FloatingActionMenuButton_main_fab_margin_bottom, mainFabMarginBottom.dpToPx().toInt())
//            optionFabMargin = getDimensionPixelSize(R.styleable.FloatingActionMenuButton_option_fab_margin, optionFabMargin.dpToPx().toInt())
//        }
//
//        init()
//    }


    init {
        context.withStyledAttributes(attrs, R.styleable.FloatingActionMenuButton, defStyleAttr, 0) {
            menuTranslationTimeOpen = getInt(R.styleable.FloatingActionMenuButton_menu_translation_time_open, menuTranslationTimeOpen)
            menuTranslationTimeHide = getInt(R.styleable.FloatingActionMenuButton_menu_translation_time_hide, menuTranslationTimeHide)
            menuTimeFadeIn = getInt(R.styleable.FloatingActionMenuButton_menu_time_fade_in, menuTimeFadeIn)
            menuTimeFadeOut = getInt(R.styleable.FloatingActionMenuButton_menu_time_fade_out, menuTimeFadeOut)
            mainFabRotationTime = getInt(R.styleable.FloatingActionMenuButton_main_fab_rotation_time, mainFabRotationTime)
            mainFabRotationDegrees = getInt(R.styleable.FloatingActionMenuButton_main_fab_rotation_degrees, mainFabRotationDegrees)

            mainFabColorBackground = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_color_background, mainFabColorBackground)
            mainFabColorIcon = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_color_icon, mainFabColorIcon)
            mainFabIconStart = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_icon_start, mainFabIconStart)
            mainFabIconEnd = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_icon_end, mainFabIconEnd)

            optionFabColorBackground = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_color_background, optionFabColorBackground)
            optionFabColorIcon = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_color_icon, optionFabColorIcon)
            optionFabTextSize = getDimensionPixelSize(R.styleable.FloatingActionMenuButton_option_fab_text_size, optionFabTextSize.dpToPx().toInt()).toFloat()
            optionFabTextColor = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_text_color, optionFabTextColor)
            optionFabTextBackground = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_text_background, optionFabTextBackground)

            mainFabMarginRight = getDimensionPixelSize(R.styleable.FloatingActionMenuButton_main_fab_margin_right, mainFabMarginRight.dpToPx().toInt())
            mainFabMarginBottom = getDimensionPixelSize(R.styleable.FloatingActionMenuButton_main_fab_margin_bottom, mainFabMarginBottom.dpToPx().toInt())
            optionFabMargin = getDimensionPixelSize(R.styleable.FloatingActionMenuButton_option_fab_margin, optionFabMargin.dpToPx().toInt())
        }

        init()
    }

    var onMenuOptionsListener: OnMenuOptionsListener? = null

    private fun init() {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mainFabContainer = inflater.inflate(R.layout.view_fab_container, this, true) as ConstraintLayout

        //init mainFab
        mainFab = mainFabContainer.findViewById(R.id.mainFab) as FloatingActionButton
//        val mainFabParams = mainFab.layoutParams as FrameLayout.LayoutParams
//        //default margins are 16dp
//        mainFabParams.rightMargin = mainFabMarginRight
//        mainFabParams.bottomMargin = mainFabMarginBottom
//        mainFab.layoutParams = mainFabParams
        setFabBackground(mainFab, mainFabColorBackground)
        setFabIcon(mainFab, mainFabIconStart, mainFabColorIcon)
        mainFab.setOnClickListener {
            if (isFabOptionsOpen) {
                onMenuOptionsListener?.onCloseMenuOptions()
                closeFabOptions()
            } else {
                if (hasFabOptions()) {
                    onMenuOptionsListener?.onOpenMenuOptions()
                    showFabOptions()
                } else {
                    mainFabAction()
                }
            }
        }
    }


    private fun setFabIcon(fab: FloatingActionButton, iconRes: Int, iconColor: Int) {
        fab.setImageResource(iconRes)
//        fab.setImageDrawable(getDrawableWithTintColor(context, iconRes, ContextCompat.getColor(context, iconColor)))
    }

    /**
     * Set the main fab icon.  This will override what was set in XML.
     */
    fun setFabIcon(iconRes: Int, iconColor: Int) {
        mainFab.setImageResource(iconRes)
//        mainFab.setImageDrawable(getDrawableWithTintColor(context, iconRes, ContextCompat.getColor(context, iconColor)))
        mainFabIconStart = iconRes
    }

    private fun setFabBackground(fab: FloatingActionButton, iconColor: Int) {
        fab.backgroundTintList = (ColorStateList.valueOf(ContextCompat.getColor(context, iconColor)))
    }

    private fun hasFabOptions() = optionFabContainers.size > 0

    //mainFabAction called only if there are no fab menu items
    fun addMainAction(mainFabAction: () -> Unit): FloatingActionMenuButton {
        this.mainFabAction = mainFabAction
        return this
    }

    fun addFabOption(optionIcon: Int, optionTitle: String?, optionAction: () -> Unit): FloatingActionMenuButton {
        val optionFabContainer = inflater.inflate(R.layout.view_fab_menu_item, this, false)

//        mainFabContainer.updatePadding(bottom = mainFabContainer.paddingBottom + optionFabContainer.measuredHeight)

//        val optionFabParams = optionFabContainer.layoutParams as LinearLayout.LayoutParams
//        //center icon vertically above mainFab. icon has rightMargin and bottomMargin = 10 dp to have full shadow
//        //with default values we need to set margin for icon = 16 + 8 -> 24. because of shadow margin we move to 16 + 10 - 2 -> 24
//        optionFabParams.rightMargin = mainFabMarginRight - MINI_FAB_OFFSET.dpToPx().toInt()
//        //set margin for the first fab to position it under main fab
//        //the next fabs have offset depending on position
//        optionFabParams.bottomMargin = mainFabMarginBottom + MAIN_FAB_SIZE.dpToPx().toInt() +
//                (optionFabContainers.size * MINI_FAB_SIZE.dpToPx().toInt() + optionFabMargin)
//        optionFabContainer.layoutParams = optionFabParams
//
//        optionFabContainer.updateLayoutParams<MarginLayoutParams> {
//            bottomMargin = mainFabMarginBottom + MAIN_FAB_SIZE.dpToPx().toInt() +
//                    (optionFabContainers.size * MINI_FAB_SIZE.dpToPx().toInt() + optionFabMargin)
//        }


        optionFabContainers.add(optionFabContainer)
        optionFabActions.add(optionAction)

        val optionFab = optionFabContainer.findViewById(R.id.fab_menu_mini) as FloatingActionButton
        mainFab.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        optionFab.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val paddingRight = (mainFab.measuredWidth + paddingStart + paddingEnd - optionFab.measuredWidth + optionFab.paddingStart + optionFab.paddingEnd) / 2
        optionFabContainer.updatePadding(right = paddingRight)

        optionFabs.add(optionFab)

        val optionPosition = optionFabActions.size - 1
        optionFab.tag = optionPosition
        setFabBackground(optionFab, optionFabColorBackground)
        setFabIcon(optionFab, optionIcon, optionFabColorIcon)

        optionFab.setOnClickListener { view ->
            closeFabOptions()
            optionFabActions[view.tag as Int]()
        }
        val optionFabTitle = optionFabContainer.findViewById(R.id.fab_menu_title) as TextView
        if (optionFabTextBackground > 0) {
            optionFabTitle.setBackgroundResource(optionFabTextBackground)
        }
        optionFabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, optionFabTextSize)
        optionFabTitles.add(optionFabTitle)
        optionFabTitle.setTextColor(ContextCompat.getColor(context, optionFabTextColor))
        if (TextUtils.isEmpty(optionTitle)) {
            optionFabTitle.visibility = View.GONE
        } else {
            optionFabTitle.text = optionTitle
        }
        val optionCard = optionFabContainer.findViewById(R.id.fab_menu_item_container) as View
        optionCard.tag = optionPosition
        optionCard.setOnClickListener { view ->
            closeFabOptions()
            optionFabActions[view.tag as Int]()
        }
//        addView(optionFabContainer, optionPosition)


        optionFabContainer.translationY = initialFabTranslationY
        optionFabContainer.fab_menu_mini.alpha = 0f
        optionFabContainer.fab_menu_title.alpha = 0f
        optionFabContainer.fab_menu_title.translationX = initialFabLabelTranslationX


        main_fab_items_container.addView(optionFabContainer, optionPosition)
        return this
    }

    private fun showFabOptions() {
        isFabOptionsOpen = true
        rotateMainFab(0f, -mainFabRotationDegrees.toFloat(), mainFabIconEnd)
        optionFabContainers.forEach { container ->
            animateFabMenuItemOpen(container.fab_menu_mini, container.fab_menu_title, container)
        }
    }

    private val interpolator = OvershootInterpolator()
    private val initialFabTranslationY = 100f
    private val initialFabLabelTranslationX = 100f

    private fun animateFabMenuItemOpen(fab: FloatingActionButton, label: TextView, container: View) {
        container.animate().withStartAction { container.toVisible() }.translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()
        label.animate().setStartDelay(0).translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start()
        fab.animate().setStartDelay(0).alpha(1f).setInterpolator(interpolator).setDuration(300).start()
    }

    private fun closeFabOptions() {
        isFabOptionsOpen = false
        rotateMainFab(mainFabRotationDegrees.toFloat(), 0f, mainFabIconStart)

        optionFabContainers.forEach { container ->
            animateFabMenuItemClose(container.fab_menu_mini, container.fab_menu_title, container)
        }
    }

    private fun animateFabMenuItemClose(fab: FloatingActionButton, label: TextView, container: View) {
        container.animate().withEndAction { container.toGone() }.translationY(initialFabTranslationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start()
        label.animate().translationX(initialFabLabelTranslationX).alpha(0f).setInterpolator(interpolator).setDuration(300).start()
        fab.animate().alpha(0f).setInterpolator(interpolator).setDuration(300).start()
    }

    private fun rotateMainFab(fromDegrees: Float, toDegrees: Float, endImage: Int) {
        if (mainFabIconEnd <= 0) {
            return
        }
        val mainFabAnimator = ObjectAnimator.ofFloat(mainFab, "rotation", fromDegrees, toDegrees)
        mainFabAnimator.duration = mainFabRotationTime.toLong()
        //change main icon while rotating
        val valueAnimator = ValueAnimator.ofInt(0, 1).setDuration(DEFAULT_TRANSLATION_TIME.toLong())
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener {
            setFabIcon(mainFab, endImage, mainFabColorIcon)
        }

        val animatorSet = AnimatorSet()
        animatorSet.play(mainFabAnimator).with(valueAnimator)
        animatorSet.start()
    }

//    private fun startOptionShowAnim(position: Int) = startOptionAnim(
//            position,
//            0f,
//            1f,
//            menuTimeFadeIn.toLong(),
//            //set transition delay depending op position
//            (position * MINI_FAB_FADE_IN_TIME_OFFSET).toLong()
//    )
//
//    private fun startOptionHideAnim(position: Int) = startOptionAnim(
//            position,
//            1f,
//            0f,
//            menuTimeFadeOut.toLong(),
//            0L
//    )
//
//    private fun startOptionAnim(position: Int, fromAlpha: Float, toAlpha: Float, fadeTime: Long, translationDelay: Long) {
//        val optionContainer = optionFabContainers[position]
//        val alphaAnimator = ObjectAnimator.ofFloat(optionContainer, "alpha", fromAlpha, toAlpha)
//        alphaAnimator.duration = fadeTime
//        alphaAnimator.startDelay = translationDelay
//        alphaAnimator.addListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(animator: Animator) {
//                if (toAlpha == 1f) {
//                    optionContainer.visibility = View.VISIBLE
//                }
//            }
//
//            override fun onAnimationCancel(animator: Animator) {}
//            override fun onAnimationRepeat(animator: Animator) {}
//            override fun onAnimationEnd(animator: Animator) {
//                if (toAlpha == 0f) {
//                    optionContainer.visibility = View.GONE
//                }
//            }
//        })
//
//        val animatorSet = AnimatorSet()
//        //add scale animators only when show menu
//        if (toAlpha == 1f) {
//            val fabView = optionFabs[position]
//            val fabScaleXAnimator = ObjectAnimator.ofFloat(fabView, "scaleX", 0f, 1f)
//            fabScaleXAnimator.duration = fadeTime
//            val fabScaleYAnimator = ObjectAnimator.ofFloat(fabView, "scaleY", 0f, 1f)
//            fabScaleYAnimator.duration = fadeTime
//
//            val titleView = optionFabTitles[position]
//            val titleScaleXAnimator = ObjectAnimator.ofFloat(titleView, "scaleX", 0f, 1f)
//            titleScaleXAnimator.duration = fadeTime
//            val titleScaleYAnimator = ObjectAnimator.ofFloat(titleView, "scaleY", 0f, 1f)
//            titleScaleYAnimator.duration = fadeTime
//
//            animatorSet
//                    .play(alphaAnimator)
//                    .with(fabScaleXAnimator)
//                    .with(fabScaleYAnimator)
//                    .with(titleScaleXAnimator)
//                    .with(titleScaleYAnimator)
//        } else {
//            animatorSet.play(alphaAnimator)
//        }
//
//        animatorSet.start()
//    }
}