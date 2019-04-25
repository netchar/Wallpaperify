package com.netchar.common.utils

import android.content.Context
import android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP
import com.netchar.common.extensions.getActivityManager

object Memory {

    //@FloatRange(from = 0.01, to = 1.0) todo: find out why not working
    @JvmStatic
    fun calculateCacheSize(context: Context, size: Float = 0f): Long {
        val activityManager = context.getActivityManager()
        val isLargeHeap = context.applicationInfo.flags and FLAG_LARGE_HEAP != 0
        val memoryClass = if (isLargeHeap) activityManager.largeMemoryClass else activityManager.memoryClass
        return (memoryClass * 1024L * 1024L * size).toLong()
    }
}