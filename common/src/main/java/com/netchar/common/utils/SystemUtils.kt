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

package com.netchar.common.utils

import android.app.Activity
import android.content.Context
import androidx.core.app.ShareCompat
import androidx.core.content.pm.PackageInfoCompat


/**
 * Created by Netchar on 08.05.2019.
 * e.glushankov@gmail.com
 */
//
//fun Any.getJsonFromResourceFile(fileName: String): String {
//    val uri = this.javaClass.classLoader?.getResource(fileName)
//    uri?.let {
//        val file = File(uri.path)
//        return String(file.readBytes())
//    } ?: throw Resources.NotFoundException("File not found")
//}

fun Activity.share(url: String, chooserTitle: String) {
    val shareIntent = ShareCompat.IntentBuilder
            .from(this)
            .setType("text/plain")
            .setChooserTitle(chooserTitle)
            .setText(url)
            .intent

    if (shareIntent.resolveActivity(packageManager) != null) {
        startActivity(shareIntent)
    } else {
        // unable to find resolve activity to share url
        // todo: firebase analytics
    }
}

fun Context.getVersionCode(): Long {
    return try {
        val manager = packageManager
        val info = manager.getPackageInfo(packageName, 0)
        PackageInfoCompat.getLongVersionCode(info)
    } catch (e: Exception) {
        e.printStackTrace()
        -1
    }
}

fun Context.getVersionName(): String? {
    return try {
        val manager = packageManager
        val info = manager.getPackageInfo(packageName, 0)
        info.versionName
    } catch (e: Exception) {
        null
    }
}