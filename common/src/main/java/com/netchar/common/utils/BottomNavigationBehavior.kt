package com.netchar.common.utils

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import kotlin.math.max
import kotlin.math.min

class BottomNavigationBehavior<V : View>(context: Context, attrs: AttributeSet) :
    androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior<V>(context, attrs) {

    @ViewCompat.NestedScrollType
    private var lastStartedType: Int = 0

    private var offsetAnimator: ValueAnimator? = null

    var isSnappingEnabled = false

    override fun layoutDependsOn(parent: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, dependency: View): Boolean {
        if (dependency is Snackbar.SnackbarLayout) {
            updateSnackbar(child, dependency)
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onStartNestedScroll(
        coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, directTargetChild: View, target: View, axes: Int, type: Int
    ): Boolean {
        if (axes != ViewCompat.SCROLL_AXIS_VERTICAL)
            return false

        lastStartedType = type

        offsetAnimator?.cancel()

        return true
    }

    override fun onNestedPreScroll(
        coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, target: View, dx: Int, dy: Int, consumed: IntArray, type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        child.translationY = max(0f, min(child.height.toFloat(), child.translationY + dy))
    }

    override fun onStopNestedScroll(coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout, child: V, target: View, type: Int) {
        if (!isSnappingEnabled)
            return

        // add snap behaviour
        // Logic here borrowed from AppBarLayout onStopNestedScroll code
        if (lastStartedType == ViewCompat.TYPE_TOUCH || type == ViewCompat.TYPE_NON_TOUCH) {
            // find nearest seam
            val currTranslation = child.translationY
            val childHalfHeight = child.height * 0.5f

            // translate down
            if (currTranslation >= childHalfHeight) {
                animateBarVisibility(child, isVisible = false)
            }
            // translate up
            else {
                animateBarVisibility(child, isVisible = true)
            }
        }
    }

    private fun animateBarVisibility(child: View, isVisible: Boolean) {
        if (offsetAnimator == null) {
            offsetAnimator = ValueAnimator().apply {
                interpolator = DecelerateInterpolator()
                duration = 150L
            }

            offsetAnimator?.addUpdateListener {
                child.translationY = it.animatedValue as Float
            }
        } else {
            offsetAnimator?.cancel()
        }

        val targetTranslation = if (isVisible) 0f else child.height.toFloat()
        offsetAnimator?.setFloatValues(child.translationY, targetTranslation)
        offsetAnimator?.start()
    }

    private fun updateSnackbar(child: View, snackbarLayout: Snackbar.SnackbarLayout) {
        if (snackbarLayout.layoutParams is androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams) {
            val params = snackbarLayout.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams

            params.anchorId = child.id
            params.anchorGravity = Gravity.TOP
            params.gravity = Gravity.TOP
            snackbarLayout.layoutParams = params
        }
    }
}