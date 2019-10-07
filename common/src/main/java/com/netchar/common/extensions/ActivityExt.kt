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
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity


fun Activity?.showToolbarTitle(enabled: Boolean) {
    val appCompatActivity = this as AppCompatActivity
    appCompatActivity.supportActionBar?.setDisplayShowTitleEnabled(enabled)
}

fun Activity?.setTransparentNavigationBar(transparent: Boolean) {
    this?.let {
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

fun Activity?.restoreStatusBarTheme() {
    this?.let {
        getThemeAttributeValue(android.R.attr.windowLightStatusBar) {
            setLightStatusBar(getBoolean(0, false))
        }
    }
}

private fun Int.isFlagSet(flag: Int) = (this and flag) == flag