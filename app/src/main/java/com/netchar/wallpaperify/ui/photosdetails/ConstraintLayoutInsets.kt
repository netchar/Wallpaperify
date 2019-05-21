package com.netchar.wallpaperify.ui.photosdetails

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.forEach

class ConstraintLayoutInsets @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {


        var consumed = false
        this.forEach { child ->
            // Dispatch the insets to the child
            val childResult = child.dispatchApplyWindowInsets(insets)
            // If the child consumed the insets, record it
            if (childResult.isConsumed) {
                consumed = true
            }
        }

        // If any of the children consumed the insets, return
        // an appropriate value

        return if (consumed) insets.consumeSystemWindowInsets() else insets
    }


}
