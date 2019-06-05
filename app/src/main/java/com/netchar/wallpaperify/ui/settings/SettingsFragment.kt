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

package com.netchar.wallpaperify.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.bumptech.glide.Glide
import com.netchar.common.extensions.applyWindowInsets
import com.netchar.common.extensions.asMb
import com.netchar.common.extensions.directorySize
import com.netchar.common.extensions.toast
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.Injector
import com.netchar.common.utils.navigation.IToolbarNavigationBinder
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.modules.GlideApp
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SettingsFragment : PreferenceFragmentCompat(), CoroutineScope, HasSupportFragmentInjector {

    private val job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var navigationBinder: IToolbarNavigationBinder

    override fun supportFragmentInjector() = childFragmentInjector

    override fun onAttach(context: Context) {
        Injector.inject(this)
        super.onAttach(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        launch {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)

        navigationBinder.bind(this, toolbar)
        toolbar?.applyWindowInsets()
    }

    override fun onBindPreferences() {
        super.onBindPreferences()
        val cachepref = findPreference<Preference>(getString(R.string.preference_key_cache))
        cachepref?.summary = "Size: ${Glide.getPhotoCacheDir(context!!)?.directorySize()?.asMb()} MB"
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.preference_key_cache) -> {
                launch {
                    withContext(CoroutineDispatchers().disk) {
                        GlideApp.get(context!!).clearDiskCache()
                    }
                    preference.summary = "Size: ${Glide.getPhotoCacheDir(context!!)?.directorySize()?.asMb()} MB"
                    toast("Cache cleared")
                }
            }
        }

        return super.onPreferenceTreeClick(preference)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }
}
