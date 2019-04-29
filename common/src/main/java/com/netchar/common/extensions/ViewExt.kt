package com.netchar.common.extensions

import android.content.res.Resources
import android.view.View
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

fun Fragment.showSnackbar(message: String, timeLength: Int) {
    activity?.let { Snackbar.make(it.findViewById<View>(android.R.id.content), message, timeLength).show() }
}

val Int.dp get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px get() = (this * Resources.getSystem().displayMetrics.density).toInt()