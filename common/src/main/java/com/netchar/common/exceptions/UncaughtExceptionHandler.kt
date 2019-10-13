/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.common.exceptions

import android.content.Context
import android.os.Build
import androidx.core.os.ConfigurationCompat
import com.crashlytics.android.Crashlytics
import com.netchar.common.utils.IBuildConfig
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class UncaughtExceptionHandler private constructor(
        context: Context,
        private val buildConfig: IBuildConfig
) : Thread.UncaughtExceptionHandler {
    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", ConfigurationCompat.getLocales(context.resources.configuration)[0])
    private val previousHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()
    private val runtime by lazy { Runtime.getRuntime() }

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val dumpDate = Date(System.currentTimeMillis())

        buildString {
            getFullStack(exception, this)

            appendln()
            appendln("************ Timestamp  ${formatter.format(dumpDate)} ************")
            appendln()
            appendln("************ DEVICE INFORMATION ***********")
            appendln("Brand: " + Build.BRAND)
            appendln("Device: " + Build.DEVICE)
            appendln("Model: " + Build.MODEL)
            appendln("Id: " + Build.ID)
            appendln("Product: " + Build.PRODUCT)
            appendln()
            appendln("************ BUILD INFO ************")
            appendln("SDK: " + Build.VERSION.SDK_INT)
            appendln("Release: " + Build.VERSION.RELEASE)
            appendln("Incremental: " + Build.VERSION.INCREMENTAL)
            appendln("Version Name: " + buildConfig.getVersionName())
            appendln("Version Code: " + buildConfig.getVersionCode())

            // Calculate the memory heap
            val maxMemory = runtime.maxMemory()
            val freeMemory = runtime.freeMemory()
            val usedMemory = runtime.totalMemory() - freeMemory
            val availableMemory = maxMemory - usedMemory

            //Set values to Crashlytics
            Crashlytics.setLong("used_memory", usedMemory)
            Crashlytics.setLong("available_memory", availableMemory)

            Timber.e(this.toString())
        }

        previousHandler?.uncaughtException(thread, exception)
    }

    private fun getFullStack(exception: Throwable?, builder: StringBuilder) {

        if (exception == null)
            return

        builder.apply {
            appendln()
            appendln("Exception: ${exception.javaClass.name}")
            appendln("Message: ${exception.message}")
            appendln("Stacktrace:")
            for (element in exception.stackTrace) {
                append("\t\t").append(element.toString()).append("\n")
            }
        }

        getFullStack(exception.cause, builder)
    }

    companion object {
        fun inContext(context: Context, buildConfig: IBuildConfig): UncaughtExceptionHandler {
            return UncaughtExceptionHandler(context, buildConfig)
        }
    }
}