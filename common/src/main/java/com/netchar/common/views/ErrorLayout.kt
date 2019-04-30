package com.netchar.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.netchar.common.R
import kotlinx.android.synthetic.main.view_error_layout.view.*

/**
 * TODO: document your custom view class.
 */
class ErrorLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_error_layout, this, true)
        attrs?.let {
            val styledAttributes = context.obtainStyledAttributes(it, R.styleable.ErrorLayout, 0, 0)
            try {
                message = styledAttributes.getString(R.styleable.ErrorLayout_message)
                imageResource = styledAttributes.getResourceId(R.styleable.ErrorLayout_icon, 0)

            } finally {
                styledAttributes.recycle()
            }
        }
    }

    var message: String?
        get() = error_view_text.text?.toString()
        set(value) {
            error_view_text.text = value
        }

    @DrawableRes
    var imageResource: Int? = 0
        set(value) {
            value?.let { error_view_image.setImageResource(value) }
        }
}
