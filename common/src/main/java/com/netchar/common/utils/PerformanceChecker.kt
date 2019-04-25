package com.netchar.common.utils

import android.content.Context
import com.netchar.common.extensions.getActivityManager
import javax.inject.Inject

private const val OPTIMUM_CORE = 4
private const val OPTIMUM_MEMORY_MB = 124

object PerformanceChecker {

    fun isHighPerformingDevice(context: Context): Boolean {
        return isHighRamDevice(context) && isOptimumCoreProcessor() && isOptimumMemory(context)
    }

    private fun isOptimumMemory(context: Context) = context.getActivityManager().memoryClass >= OPTIMUM_MEMORY_MB

    private fun isOptimumCoreProcessor() = Runtime.getRuntime().availableProcessors() >= OPTIMUM_CORE

    private fun isHighRamDevice(context: Context) = !context.getActivityManager().isLowRamDevice

}