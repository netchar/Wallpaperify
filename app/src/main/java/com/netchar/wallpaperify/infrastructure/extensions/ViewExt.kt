package com.netchar.wallpaperify.infrastructure.extensions

import android.view.View


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