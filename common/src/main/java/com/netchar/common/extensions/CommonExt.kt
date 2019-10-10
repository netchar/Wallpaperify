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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.style.ClickableSpan
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.drawerlayout.widget.DrawerLayout
import com.netchar.common.DEVELOPER_GMAIL
import com.netchar.common.R

/**
 * Helps to set clickable part in text.
 *
 * Don't forget to set android:textColorLink="@color/link" (click selector) and
 * android:textColorHighlight="@color/window_background" (background color while clicks)
 * in the TextView where you will use this.
 */
fun Spannable.withClickableSpan(clickablePart: String, onClickListener: () -> Unit): Spannable {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) = onClickListener.invoke()
    }
    val clickablePartStart = indexOf(clickablePart)
    setSpan(
            clickableSpan,
            clickablePartStart,
            clickablePartStart + clickablePart.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return this
}

fun String.toWebUri(): Uri {
    return (if (startsWith("http://") || startsWith("https://")) this else "https://$this").toUri()
}

fun Context.openWebPage(url: String): Boolean {
    // Format the URI properly.
    val uri = url.toWebUri()

    // Try using Chrome Custom Tabs.
    try {
        val intent = CustomTabsIntent.Builder()
                .setToolbarColor(getColorCompat(R.color.color_surface))
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

fun createEmailIntent(subject: String, message: String) : Intent {
    return Intent(Intent.ACTION_SEND).apply {
        // The intent does not have a URI, so declare the "text/plain" MIME type
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(DEVELOPER_GMAIL)) // recipients
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, message)
        putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"))
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

inline fun <T> Sequence<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

//inline fun <reified C : Collection<T>, reified T : Any> Moshi.collectionAdapter(): JsonAdapter<C> {
//    val parametrizedType = Types.newParameterizedType(C::class.java, T::class.java)
//    return this.adapter<C>(parametrizedType)
//}