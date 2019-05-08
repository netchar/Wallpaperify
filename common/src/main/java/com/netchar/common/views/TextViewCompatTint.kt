package com.netchar.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.DrawableCompat
import com.netchar.common.R

class TextViewCompatTint @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.textViewStyle) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewCompatTint, defStyleAttr, 0)

        if (typedArray.hasValue(R.styleable.TextViewCompatTint_drawableTint)) {
            val color = typedArray.getColor(R.styleable.TextViewCompatTint_drawableTint, 0)

            val drawables = compoundDrawablesRelative

            for (drawable in drawables) {
                if (drawable == null) continue
                DrawableCompat.setTint(DrawableCompat.wrap(drawable).mutate(), color)
            }
        }

        typedArray.recycle()
    }
}