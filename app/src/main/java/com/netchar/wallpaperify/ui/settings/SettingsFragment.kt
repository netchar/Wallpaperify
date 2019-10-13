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
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.netchar.common.extensions.applyWindowInsets
import com.netchar.common.extensions.getStringSafe
import com.netchar.common.extensions.injectViewModel
import com.netchar.common.extensions.toast
import com.netchar.common.utils.IBuild
import com.netchar.common.utils.Injector
import com.netchar.common.utils.ThemeUtils
import com.netchar.common.utils.navigation.IToolbarNavigationBinder
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), HasAndroidInjector, SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var navigationBinder: IToolbarNavigationBinder

    @Inject
    lateinit var build: IBuild

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun androidInjector(): AndroidInjector<Any> {
        return childFragmentInjector
    }

    private lateinit var activityContext: Context

    private lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        Injector.inject(this)
        super.onAttach(context)
        activityContext = context
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar).also { it.applyWindowInsets() }
        viewModel = injectViewModel(viewModelFactory)
        navigationBinder.bind(this@SettingsFragment, toolbar)
        observe()
    }

    private fun observe() {
        viewModel.cacheSize.observe(viewLifecycleOwner, Observer { cacheSizeMb ->
            val cachePreference = findPreference<Preference>(getString(R.string.preference_option_key_cache))
            cachePreference?.summary = "Size: $cacheSizeMb MB"
        })

        viewModel.toast.observe(viewLifecycleOwner, Observer { message ->
            val toastMessage = getStringSafe(message.messageRes)
            toast(toastMessage)
        })
    }

    override fun onBindPreferences() {
        super.onBindPreferences()
        setPreferencesSummary()
    }

    private fun setPreferencesSummary() {
        val buildPreference = findPreference<Preference>(getString(R.string.preference_option_key_build))
        buildPreference?.summary = "${build.getVersionName()}"
    }

    override fun onStart() {
        super.onStart()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.preference_option_key_cache) -> viewModel.clearCacheAsync()
        }

        return super.onPreferenceTreeClick(preference)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        when (key) {
            getString(R.string.preference_option_key_theme) -> {
                val themeMode = sharedPreferences.getString(key, "")
                if (themeMode.isNullOrEmpty()) {
                    Timber.e("Unexpected state. Preferences does not contains: $key")
                    return
                }
                ThemeUtils.applyDayNightMode(themeMode.toInt())
            }
        }
    }
}
