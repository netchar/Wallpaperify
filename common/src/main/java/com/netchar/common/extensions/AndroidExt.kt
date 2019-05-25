package com.netchar.common.extensions

import android.app.DownloadManager
import android.content.res.Resources
import android.database.Cursor
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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