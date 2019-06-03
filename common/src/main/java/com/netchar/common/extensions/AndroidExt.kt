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

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.netchar.common.R
import com.netchar.common.base.BaseFragment
import com.netchar.common.utils.getThemeAttrColor
import java.io.File


//todo: fix this mess
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

@ColorInt
fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

fun Context.getDrawableCompat(@DrawableRes drawableRes: Int): Drawable {
    return AppCompatResources.getDrawable(this, drawableRes)!!
}

@CheckResult
fun Drawable.tint(@ColorInt color: Int): Drawable {
    val tintedDrawable = DrawableCompat.wrap(this).mutate()
    DrawableCompat.setTint(tintedDrawable, color)
    return tintedDrawable
}

@CheckResult
fun Drawable.tint(context: Context, @ColorRes color: Int): Drawable {
    return tint(context.getColorCompat(color))
}

//todo: implement custom tabs
fun Context.openWebPage(url: String): Boolean {
    // Format the URI properly.
    val uri = url.toWebUri()

    // Try using Chrome Custom Tabs.
    try {
        val intent = CustomTabsIntent.Builder()
            .setToolbarColor(getColorCompat(R.color.colorPrimary))
            .setShowTitle(true)
            .build()
        intent.launchUrl(this, uri)
        return true
    } catch (ignored: Exception) {
    }

    // Fall back to launching a default web browser intent.
    try {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            return true
        }
    } catch (ignored: Exception) {
    }

    // We were unable to show the web page.
    return false
}

fun String.toWebUri(): Uri {
    return (if (startsWith("http://") || startsWith("https://")) this else "https://$this").toUri()
}

inline fun <T : Fragment> T.withArgs(argsBuilder: Bundle.() -> Unit): T = this.apply {
    arguments = Bundle().apply(argsBuilder)
}

fun Activity?.setTransparentStatusBars(transparent: Boolean) {
    this?.let {
        it.window.statusBarColor = if (transparent) Color.TRANSPARENT else it.getThemeAttrColor(android.R.attr.statusBarColor)
        it.window.navigationBarColor = if (transparent) Color.TRANSPARENT else it.getThemeAttrColor(android.R.attr.navigationBarColor)
    }
}

fun Activity?.setLightStatusBar(enable: Boolean) {
    this?.let {
        val systemUiVisibility = it.window.decorView.systemUiVisibility
        if (enable) {
            if (!systemUiVisibility.isFlagSet(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)) {
                it.window.decorView.systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        } else {
            if (systemUiVisibility.isFlagSet(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)) {
                it.window.decorView.systemUiVisibility = systemUiVisibility xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}

private fun Int.isFlagSet(flag: Int) = (this and flag) == flag