package com.netchar.wallpaperify.infrastructure.utils

import android.content.Context
import com.netchar.wallpaperify.infrastructure.extensions.getActivityManager
import javax.inject.Inject

private const val OPTIMUM_CORE = 4
private const val OPTIMUM_MEMORY_MB = 124

class PerformanceChecker  @Inject constructor(context: Context) {

    val isHighPerformingDevice by lazy {
        isHighRamDevice(context) && isOptimumCoreProcessor() && isOptimumMemory(context)
    }

    private fun isOptimumMemory(context: Context) = context.getActivityManager().memoryClass >= OPTIMUM_MEMORY_MB

    private fun isOptimumCoreProcessor() = Runtime.getRuntime().availableProcessors() >= OPTIMUM_CORE

    private fun isHighRamDevice(context: Context) = !context.getActivityManager().isLowRamDevice

}