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

package com.netchar.common.extensions

import android.app.DownloadManager
import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.netchar.common.base.BaseFragment
import java.io.File


fun File.notExist() = !this.exists()

inline fun <reified TViewModel : ViewModel> AppCompatActivity.injectViewModel(factory: ViewModelProvider.Factory): TViewModel {
    return ViewModelProviders.of(this, factory)[TViewModel::class.java]
}

inline fun <reified TViewModel : ViewModel> Fragment.injectViewModel(factory: ViewModelProvider.Factory): TViewModel {
    return ViewModelProviders.of(this, factory)[TViewModel::class.java]
}

inline fun <reified TViewModel : ViewModel> injectViewModelOf(context: AppCompatActivity, factory: ViewModelProvider.Factory): TViewModel {
    return ViewModelProviders.of(context, factory)[TViewModel::class.java]
}

inline fun <reified TViewModel : ViewModel> injectViewModelOf(context: Fragment, factory: ViewModelProvider.Factory): TViewModel {
    return ViewModelProviders.of(context, factory)[TViewModel::class.java]
}

fun BaseFragment.setSupportActionBar(toolbar: Toolbar) = (activity as AppCompatActivity).setSupportActionBar(toolbar)

val Int.dp get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Fragment.getStringSafe(@StringRes res: Int?): String = res?.let { getString(it) } ?: ""

fun <T> Cursor.using(body: Cursor.() -> T): T {
    return use { this.body() }
}

fun Cursor.getInt(columnIndex: String): Int {
    return getInt(getColumnIndex(columnIndex))
}

fun DownloadManager.getCursor(downloadId: Long): Cursor? {
    val cursor = query(DownloadManager.Query().setFilterById(downloadId))

    return when {
        cursor == null -> null
        cursor.count > 0 && cursor.moveToFirst() -> cursor
        else -> {
            cursor.close()
            null
        }
    }
}

inline fun consume(f: () -> Unit): Boolean {
    f()
    return true
}

inline fun DrawerLayout.consume(f: () -> Unit): Boolean {
    f()
    closeDrawers()
    return true
}

fun File.toFileProviderUri(context: Context) = FileProvider.getUriForFile(context, "com.netchar.wallpaperify.fileprovider", this)