package com.netchar.common.extensions

import android.content.Context
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.fragment.app.Fragment

@Dimension(unit = Dimension.DP)
fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

@Dimension(unit = Dimension.DP)
fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

//return sp dimension value in pixels
@Dimension(unit = Dimension.SP)
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()

@Dimension(unit = Dimension.SP)
fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

//converts px value into dip or sp
@Dimension(unit = Dimension.DP)
fun Context.px2dip(px: Int): Float = px.toFloat() / resources.displayMetrics.density

@Dimension(unit = Dimension.SP)
fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)


@Dimension(unit = Dimension.DP)
fun Fragment.dip(value: Int): Int = context?.dip(value) ?: 0

@Dimension(unit = Dimension.DP)
fun Fragment.dip(value: Float): Int = context?.dip(value) ?: 0

//return sp dimension value in pixels
@Dimension(unit = Dimension.SP)
fun Fragment.sp(value: Int): Int = context?.sp(value) ?: 0

@Dimension(unit = Dimension.SP)
fun Fragment.sp(value: Float): Int = context?.sp(value) ?: 0

//converts px value into dip or sp
@Dimension(unit = Dimension.DP)
fun Fragment.px2dip(px: Int): Float = context?.px2dip(px) ?: 0f

@Dimension(unit = Dimension.SP)
fun Fragment.px2sp(px: Int): Float = context?.px2dip(px) ?: 0f

fun Fragment.dimen(@DimenRes resource: Int): Int = context?.dimen(resource) ?: 0