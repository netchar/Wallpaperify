package com.netchar.wallpaperify.infrastructure.extensions

import android.app.ActivityManager
import android.content.Context

fun Context.getActivityManager() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager