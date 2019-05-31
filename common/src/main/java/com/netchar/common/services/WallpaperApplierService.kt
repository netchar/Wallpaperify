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

package com.netchar.common.services

import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.netchar.common.R
import timber.log.Timber

class WallpaperApplierService(val context: Context) {

    fun setWallpaper(uri: Uri) {
        try {
            Timber.d("Set wallpaper via WallpaperManager. Uri: $uri")
            val wallpaperManager = WallpaperManager.getInstance(context)
            wallpaperManager.getCropAndSetWallpaperIntent(uri)
                    .apply {
                        setDataAndType(uri, "image/*")
                        putExtra("mimeType", "image/*")
                    }.also {
                        //                        startActivityForResult(it, 13451)
                        ContextCompat.startActivity(context, it, null)
                    }
        } catch (ex: Exception) {
            ex.printStackTrace()
            Timber.d("Set wallpaper via Chooser. Uri: $uri")

            val intent = Intent(Intent.ACTION_ATTACH_DATA).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                setDataAndType(uri, "image/*")
                putExtra("mimeType", "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            val chooserIntent = Intent.createChooser(intent, context.getString(R.string.title_chooser_set_wallpaper_as))
//            startActivity(context, chooserIntent, null)
            ContextCompat.startActivity(context, chooserIntent, null)
        }
    }
}