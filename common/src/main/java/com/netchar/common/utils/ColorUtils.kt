package com.netchar.common.utils

import android.graphics.Color


/**
 * Created by Netchar on 09.05.2019.
 * e.glushankov@gmail.com
 */

fun String?.parseColor(/*from = 0, to = 255*/ alpha: Int, defaultColor: Int): Int {
    if (this.isNullOrEmpty()) {
        return defaultColor
    }

    val hsv = FloatArray(3)
    Color.colorToHSV(Color.parseColor(this), hsv)
    return Color.HSVToColor(alpha, hsv)
}