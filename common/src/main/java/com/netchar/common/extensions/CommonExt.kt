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

import android.text.Spannable
import android.text.style.ClickableSpan
import android.view.View

/**
 * Helps to set clickable part in text.
 *
 * Don't forget to set android:textColorLink="@color/link" (click selector) and
 * android:textColorHighlight="@color/window_background" (background color while clicks)
 * in the TextView where you will use this.
 */
fun Spannable.withClickableSpan(clickablePart: String, onClickListener: () -> Unit): Spannable {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View?) = onClickListener.invoke()
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