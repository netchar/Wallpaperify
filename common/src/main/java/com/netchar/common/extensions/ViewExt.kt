package com.netchar.common.extensions

import android.content.res.Resources
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


/**
 * Created by Netchar on 17.03.2019.
 * e.glushankov@gmail.com
 */

fun View.toVisible() {
    visibility = View.VISIBLE
}

fun View.toInvisible() {
    visibility = View.INVISIBLE
}

fun View.toGone() {
    visibility = View.GONE
}

fun View.booleanVisibility(visible: Boolean) = if (visible) toVisible() else toGone()

fun View.inverseBooleanVisibility(visible: Boolean) = booleanVisibility(!visible)

fun View.isVisible() = visibility == View.VISIBLE

fun Fragment.showSnackbar(message: String, timeLength: Int) {
    activity?.let { Snackbar.make(it.findViewById<View>(android.R.id.content), message, timeLength).show() }
}

fun Fragment.showToast(message: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this.context, message, length).show()
}

val Int.dp get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px get() = (this * Resources.getSystem().displayMetrics.density).toInt()

inline fun <reified T : View> T.postAction(action: T.() -> Unit) {
    action()
}

fun Fragment.getStringSafe(@StringRes res: Int?): String = res?.let { getString(it) } ?: ""