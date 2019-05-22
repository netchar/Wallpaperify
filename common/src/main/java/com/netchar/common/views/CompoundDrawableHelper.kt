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
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import com.netchar.common.R

class CompoundDrawableHelper(private val mContext: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : IChangebleCompoundDrawable {

    private var drawableWidth: Int
    private var drawableHeight: Int
    private var drawableStartResId: Int
    private var drawableTopResId: Int
    private var drawableEndResId: Int
    private var drawableBottomResId: Int

    @ColorInt
    private var drawableTintColor: Int

    init {

        val array = mContext.obtainStyledAttributes(attrs, R.styleable.CompoundDrawableTextView, defStyleAttr, defStyleRes)

        try {
            drawableWidth = array.getDimensionPixelSize(R.styleable.CompoundDrawableTextView_compoundDrawableWidth, IChangebleCompoundDrawable.UNDEFINED)
            drawableHeight = array.getDimensionPixelSize(R.styleable.CompoundDrawableTextView_compoundDrawableHeight, IChangebleCompoundDrawable.UNDEFINED)
            drawableStartResId = array.getResourceId(R.styleable.CompoundDrawableTextView_drawableStartVector, IChangebleCompoundDrawable.UNDEFINED)
            drawableTopResId = array.getResourceId(R.styleable.CompoundDrawableTextView_drawableTopVector, IChangebleCompoundDrawable.UNDEFINED)
            drawableEndResId = array.getResourceId(R.styleable.CompoundDrawableTextView_drawableEndVector, IChangebleCompoundDrawable.UNDEFINED)
            drawableBottomResId = array.getResourceId(R.styleable.CompoundDrawableTextView_drawableBottomVector, IChangebleCompoundDrawable.UNDEFINED)
            drawableTintColor = array.getColor(R.styleable.CompoundDrawableTextView_drawableTint, IChangebleCompoundDrawable.UNDEFINED)
        } finally {
            array.recycle()
        }
    }

    fun apply(textView: TextView) {
        if (isCompoundRequested()) {
            initCompoundDrawables(textView, drawableStartResId, drawableTopResId, drawableEndResId, drawableBottomResId)
        }
    }

    private fun isCompoundRequested() = drawableWidth > 0
            || drawableHeight > 0
            || drawableStartResId > 0
            || drawableTopResId > 0
            || drawableEndResId > 0
            || drawableBottomResId > 0

    private fun initCompoundDrawables(
            textView: TextView,
            drawableStartVectorId: Int,
            drawableTopVectorId: Int,
            drawableEndVectorId: Int,
            drawableBottomVectorId: Int
    ) {
        val drawables = textView.compoundDrawables

        inflateDrawables(textView, drawableStartVectorId, drawableTopVectorId, drawableEndVectorId, drawableBottomVectorId, drawables)
        scale(drawables)
        tint(drawables)

        textView.setCompoundDrawables(drawables[LEFT_DRAWABLE_INDEX], drawables[TOP_DRAWABLE_INDEX], drawables[RIGHT_DRAWABLE_INDEX], drawables[BOTTOM_DRAWABLE_INDEX])
    }

    private fun inflateDrawables(
            textView: TextView,
            drawableStartVectorId: Int,
            drawableTopVectorId: Int,
            drawableEndVectorId: Int,
            drawableBottomVectorId: Int,
            drawables: Array<Drawable?>
    ) {
        val rtl = ViewCompat.getLayoutDirection(textView) == ViewCompat.LAYOUT_DIRECTION_RTL

        if (drawableStartVectorId != IChangebleCompoundDrawable.UNDEFINED) {
            drawables[if (rtl) RIGHT_DRAWABLE_INDEX else LEFT_DRAWABLE_INDEX] = getVectorDrawable(drawableStartVectorId)
        }
        if (drawableTopVectorId != IChangebleCompoundDrawable.UNDEFINED) {
            drawables[TOP_DRAWABLE_INDEX] = getVectorDrawable(drawableTopVectorId)
        }
        if (drawableEndVectorId != IChangebleCompoundDrawable.UNDEFINED) {
            drawables[if (rtl) LEFT_DRAWABLE_INDEX else RIGHT_DRAWABLE_INDEX] = getVectorDrawable(drawableEndVectorId)
        }
        if (drawableBottomVectorId != IChangebleCompoundDrawable.UNDEFINED) {
            drawables[BOTTOM_DRAWABLE_INDEX] = getVectorDrawable(drawableBottomVectorId)
        }
    }

    private fun scale(drawables: Array<Drawable?>) {
        if (drawableHeight > 0 || drawableWidth > 0) {
            for (drawable in drawables) {
                if (drawable == null) {
                    continue
                }

                val realBounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                var actualDrawableWidth = realBounds.width().toFloat()
                var actualDrawableHeight = realBounds.height().toFloat()
                val actualDrawableRatio = actualDrawableHeight / actualDrawableWidth

                // check if both width and height defined then adjust drawable size according to the ratio
                val scale = if (drawableHeight > 0 && drawableWidth > 0) {
                    val placeholderRatio = drawableHeight / drawableWidth.toFloat()
                    if (placeholderRatio > actualDrawableRatio) {
                        drawableWidth / actualDrawableWidth
                    } else {
                        drawableHeight / actualDrawableHeight
                    }
                } else if (drawableHeight > 0) { // only height defined
                    drawableHeight / actualDrawableHeight
                } else { // only width defined
                    drawableWidth / actualDrawableWidth
                }

                actualDrawableWidth *= scale
                actualDrawableHeight *= scale

                realBounds.right = realBounds.left + Math.round(actualDrawableWidth)
                realBounds.bottom = realBounds.top + Math.round(actualDrawableHeight)

                drawable.bounds = realBounds
            }
        } else {
            for (drawable in drawables) {
                if (drawable == null) {
                    continue
                }

                drawable.bounds = Rect(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            }
        }
    }

    private fun tint(drawables: Array<Drawable?>) {

        if (drawableTintColor == IChangebleCompoundDrawable.UNDEFINED) {
            return
        }

        for (i in drawables.indices) {
            if (drawables[i] == null) {
                continue
            }

            val tintedDrawable = DrawableCompat.wrap(drawables[i]!!)
            DrawableCompat.setTint(tintedDrawable.mutate(), drawableTintColor)

            drawables[i] = tintedDrawable
        }
    }

    private fun getVectorDrawable(@DrawableRes resId: Int): Drawable? = ResourcesCompat.getDrawable(mContext.resources, resId, mContext.theme)

    override fun getCompoundDrawableHeight(): Int = drawableHeight

    override fun getCompoundDrawableWidth(): Int = drawableWidth

    override fun setDrawableStartVectorId(@DrawableRes id: Int) {
        drawableStartResId = id
    }

    override fun setDrawableEndVectorId(@DrawableRes id: Int) {
        drawableEndResId = id
    }

    override fun setDrawableTopVectorId(@DrawableRes id: Int) {
        drawableTopResId = id
    }

    override fun setDrawableBottomVectorId(@DrawableRes id: Int) {
        drawableBottomResId = id
    }

    companion object {
        private const val LEFT_DRAWABLE_INDEX = 0
        private const val TOP_DRAWABLE_INDEX = 1
        private const val RIGHT_DRAWABLE_INDEX = 2
        private const val BOTTOM_DRAWABLE_INDEX = 3
    }
}