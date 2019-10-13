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

package com.netchar.repository.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.netchar.common.di.AppPrefs
import com.netchar.repository.R
import javax.inject.Inject

class PreferenceRepository @Inject constructor(
        @AppPrefs private val defaultPreferences: SharedPreferences,
        private val context: Context
) : IPreferenceRepository {

    override val themeMode: Int
        get() {
            val key = context.getString(R.string.preference_option_key_theme)
            val preferenceValue = defaultPreferences.getString(key, "")

            return if (preferenceValue.isNullOrEmpty()) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                preferenceValue.toInt()
            }
        }

    override val downloadQuality: String
        get() {
            val key = context.getString(R.string.preference_option_key_download_quality)
            return defaultPreferences.getString(key,  context.getString(R.string.preference_download_quality_raw)).toString()
        }
}