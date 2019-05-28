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

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.updatePadding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.netchar.common.R
import com.netchar.common.extensions.*
import kotlinx.android.synthetic.main.view_fab_container.view.*
import kotlinx.android.synthetic.main.view_fab_menu_item.view.*
import java.util.*
import kotlin.collections.ArrayList

private const val DEFAULT_TRANSLATION_TIME = 250

class FloatingActionMenuButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var menuTranslationTimeOpen = DEFAULT_TRANSLATION_TIME
    private var menuTranslationTimeHide = DEFAULT_TRANSLATION_TIME
    private var mainFabRotationTime = DEFAULT_TRANSLATION_TIME
    private var mainFabRotationDegrees = 180
    private var mainFabColorBackgroundRes: Int = android.R.color.white
    private var mainFabColorIconRes: Int = android.R.color.black
    private var mainFabIconStart = 0
    private var mainFabIconEnd = 0
    private var optionFabColorBackground: Int = android.R.color.white
    private var optionFabColorIconRes: Int = android.R.color.black
    private var optionFabTextSize = 14f
    private var optionFabTextColorRes: Int = android.R.color.black
    private var optionFabTextBackground = 0

    private var optionFabContainers = LinkedList<View>()
    private var optionFabActions = ArrayList<() -> Unit>()
    private val interpolator = OvershootInterpolator()
    private val initialFabTranslationY = 100f
    private val initialFabLabelTranslationX = 100f

    private var onMenuOptionsListener: OnMenuOptionsListener? = null
    private var overlay: View? = null

    private lateinit var inflater: LayoutInflater
    private lateinit var mainFabAction: () -> Unit

    init {
        context.withStyledAttributes(attrs, R.styleable.FloatingActionMenuButton, defStyleAttr, 0) {
            menuTranslationTimeOpen = getInt(R.styleable.FloatingActionMenuButton_menu_translation_time_open, menuTranslationTimeOpen)
            menuTranslationTimeHide = getInt(R.styleable.FloatingActionMenuButton_menu_translation_time_hide, menuTranslationTimeHide)
            mainFabRotationTime = getInt(R.styleable.FloatingActionMenuButton_main_fab_rotation_time, mainFabRotationTime)
            mainFabRotationDegrees = getInt(R.styleable.FloatingActionMenuButton_main_fab_rotation_degrees, mainFabRotationDegrees)

            mainFabColorBackgroundRes = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_color_background, mainFabColorBackgroundRes)
            mainFabColorIconRes = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_color_icon, mainFabColorIconRes)
            mainFabIconStart = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_icon_start, mainFabIconStart)
            mainFabIconEnd = getResourceId(R.styleable.FloatingActionMenuButton_main_fab_icon_end, mainFabIconEnd)

            optionFabColorBackground = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_color_background, optionFabColorBackground)
            optionFabColorIconRes = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_color_icon, optionFabColorIconRes)
            optionFabTextSize = getDimensionPixelSize(R.styleable.FloatingActionMenuButton_option_fab_text_size, optionFabTextSize.dpToPx().toInt()).toFloat()
            optionFabTextColorRes = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_text_color, optionFabTextColorRes)
            optionFabTextBackground = getResourceId(R.styleable.FloatingActionMenuButton_option_fab_text_background, optionFabTextBackground)
        }

        init()
    }

    private fun init() {
        inflater = LayoutInflater.from(context).also {
            it.inflate(R.layout.view_fab_container, this, true)
        }

        setFabBackground(fab, mainFabColorBackgroundRes)
        setFabIcon(mainFabIconStart, mainFabColorIconRes)

        fab.setOnClickListener {
            if (isMenuOpen) {
                onMenuOptionsListener?.onCloseMenuOptions()
                closeFabMenu()
            } else {
                if (hasFabOptions()) {
                    onMenuOptionsListener?.onOpenMenuOptions()
                    openFabMenu()
                } else {
                    mainFabAction()
                }
            }
        }
    }

    private fun setFabIcon(optionFab: FloatingActionButton, iconRes: Int, @ColorRes iconColorRes: Int) {
        val drawable = context.getDrawableCompat(iconRes).tint(context, iconColorRes)
        optionFab.setImageDrawable(drawable)
    }

    /**
     * Set the main fab icon.  This will override what was set in XML.
     */
    fun setFabIcon(iconRes: Int, @ColorRes iconColorRes: Int) {
        val drawable = context.getDrawableCompat(iconRes).tint(context, iconColorRes)
        fab.setImageDrawable(drawable)
        mainFabIconStart = iconRes
    }

    private fun setFabBackground(fab: FloatingActionButton, @ColorRes iconColorRes: Int) {
        val color = context.getColorCompat(iconColorRes)
        fab.backgroundTintList = (ColorStateList.valueOf(color))
    }

    private fun hasFabOptions() = optionFabContainers.size > 0

    // mainFabAction called only if there are no fab menu items
    fun addMainAction(mainFabAction: () -> Unit): FloatingActionMenuButton {
        this.mainFabAction = mainFabAction
        return this
    }

    fun setupWithOverlay(overlay: View) {
        this.overlay = overlay.apply {
            toGone()
            alpha = 0f
            isClickable = true
            setOnClickListener {
                closeFabMenu()
            }
        }
    }

    fun setOnMenuOptionsListener(listener: OnMenuOptionsListener) {
        onMenuOptionsListener = listener
    }

    var isMenuOpen = false
        private set

    fun addFabOption(optionIcon: Int, optionTitle: String?, optionAction: () -> Unit): FloatingActionMenuButton {
        val optionFabContainer = inflater.inflate(R.layout.view_fab_menu_item, this, false)
        val optionFab = optionFabContainer.fab_menu_option
        val optionFabTitle = optionFabContainer.fab_menu_title

        optionFabContainers.add(optionFabContainer)
        optionFabActions.add(optionAction)

        centerFabMenuItemToMainFab(optionFab, optionFabContainer)

        val optionPosition = optionFabActions.size - 1
        optionFab.tag = optionPosition

        setFabBackground(optionFab, optionFabColorBackground)
        setFabIcon(optionFab, optionIcon, optionFabColorIconRes)

        if (optionFabTextBackground > 0) {
            optionFabTitle.setBackgroundResource(optionFabTextBackground)
        }

        optionFabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, optionFabTextSize)
        optionFabTitle.setTextColor(ContextCompat.getColor(context, optionFabTextColorRes))
        optionFabTitle.text = optionTitle
        optionFabTitle.goneIfEmpty()

        optionFabContainer.tag = optionPosition
        optionFabContainer.setOnClickListener { view ->
            closeFabMenu()
            optionFabActions[view.tag as Int]()
        }

        setInitialAnimationState(optionFabContainer)

        fab_menu_container.addView(optionFabContainer, optionPosition)
        return this
    }

    private fun centerFabMenuItemToMainFab(optionFab: FloatingActionButton, optionFabContainer: View) {
        fab.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        optionFab.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val measureWidthDiff = fab.measuredWidth - optionFab.measuredWidth
        val horizontalPadding = (fab.paddingStart + fab.paddingEnd) / 2 - (optionFab.paddingStart + optionFab.paddingEnd) / 2
        val paddingRight = (measureWidthDiff + horizontalPadding) / 2
        optionFabContainer.updatePadding(right = paddingRight)
    }

    fun openFabMenu() {
        isMenuOpen = true
        animateRotationMainFab(fab, mainFabRotationDegrees.toFloat())
        optionFabContainers.forEach { container ->
            animateFabMenuItemOpen(container.fab_menu_option, container.fab_menu_title, container)
        }

        overlay?.fabOverlayAction {
            animate().withStartAction { toVisible() }.alpha(1f).setDuration(menuTranslationTimeOpen.toLong()).start()
        }
    }

    fun closeFabMenu() {
        isMenuOpen = false
        animateRotationMainFab(fab, 0f)
        optionFabContainers.forEach { container ->
            animateFabMenuItemClose(container.fab_menu_option, container.fab_menu_title, container)
        }

        overlay?.fabOverlayAction {
            animate().withEndAction { toGone() }.alpha(0f).setDuration(menuTranslationTimeHide.toLong()).start()
        }
    }

    private fun animateFabMenuItemOpen(fab: FloatingActionButton, label: TextView, container: View) {
        val openAnimTime = menuTranslationTimeOpen.toLong()
        container.animate().withStartAction { container.toVisible() }.translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(openAnimTime).start()
        label.animate().translationX(0f).alpha(1f).setInterpolator(interpolator).setDuration(openAnimTime).start()
        fab.animate().alpha(1f).setInterpolator(interpolator).setDuration(openAnimTime).start()
    }

    private fun animateFabMenuItemClose(fab: FloatingActionButton, label: TextView, container: View) {
        val closeAnimTime = menuTranslationTimeHide.toLong()
        container.animate().withEndAction { container.toGone() }.translationY(initialFabTranslationY).alpha(0f).setInterpolator(interpolator).setDuration(closeAnimTime).start()
        label.animate().translationX(initialFabLabelTranslationX).alpha(0f).setInterpolator(interpolator).setDuration(closeAnimTime).start()
        fab.animate().alpha(0f).setInterpolator(interpolator).setDuration(closeAnimTime).start()
    }

    private fun animateRotationMainFab(fab: FloatingActionButton, angle: Float) {
        fab.animate().setInterpolator(interpolator).rotation(angle).setDuration(mainFabRotationTime.toLong()).start()
    }

    private fun setInitialAnimationState(optionFabContainer: View) {
        optionFabContainer.translationY = initialFabTranslationY
        optionFabContainer.fab_menu_option.alpha = 0f
        optionFabContainer.fab_menu_title.alpha = 0f
        optionFabContainer.fab_menu_title.translationX = initialFabLabelTranslationX
    }

    interface OnMenuOptionsListener {
        fun onOpenMenuOptions()

        fun onCloseMenuOptions()
    }
}

private inline fun View?.fabOverlayAction(action: View.() -> Unit) = this?.let {
    val p = it.parent
    if (p is ViewGroup) {
        action()
    }
}