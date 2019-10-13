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

package com.netchar.wallpaperify.infrastructure

import android.content.Context
import android.content.pm.PackageInfo
import androidx.core.content.pm.PackageInfoCompat
import com.netchar.common.utils.IBuild
import com.netchar.wallpaperify.BuildConfig

class BuildPreferences(val context: Context) : IBuild {

    override fun getApiAccessKey() = if (BuildConfig.DEBUG) {
        BuildConfig.DEBUG_API_ACCESS_KEY
    } else {
        BuildConfig.RELEASE_API_ACCESS_KEY
    }

    override fun getApiSecretKey() = if (BuildConfig.DEBUG) {
        BuildConfig.DEBUG_API_SECRET_KEY
    } else {
        BuildConfig.RELEASE_API_SECRET_KEY
    }

    override fun getVersionCode(): Long {
        return try {
            val info = getPackageInfo(context) ?: return -1
            PackageInfoCompat.getLongVersionCode(info)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    override fun getVersionName(): String {
        return try {
            val info = getPackageInfo(context) ?: return ""
            info.versionName
        } catch (e: Exception) {
            ""
        }
    }

    override fun getApplicationId(): String {
        return BuildConfig.APPLICATION_ID
    }

    private fun getPackageInfo(context: Context): PackageInfo? {
        val manager = context.packageManager
        return manager.getPackageInfo(context.packageName, 0)
    }
}