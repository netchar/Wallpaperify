package com.netchar.common.utils

import android.graphics.Color
import timber.log.Timber


/**
 * Created by Netchar on 09.05.2019.
 * e.glushankov@gmail.com
 */

fun String?.parseColor(/*from = 0, to = 255*/ alpha: Int, defaultColor: Int): Int {
    if (this.isNullOrEmpty()) {
        return defaultColor
    }

    return try {
        val hsv = FloatArray(3)
        Color.colorToHSV(Color.parseColor(this), hsv)
        Color.HSVToColor(alpha, hsv)
    } catch (e: NumberFormatException) {
        Timber.e(e)
        defaultColor
    }
}