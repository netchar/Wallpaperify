package com.netchar.wallpaperify.infrastructure.extensions

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager

fun Context.getActivityManager() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
fun Context.getConnectivityManager() =  getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager