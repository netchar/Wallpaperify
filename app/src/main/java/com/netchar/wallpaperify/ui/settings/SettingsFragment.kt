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
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.netchar.common.extensions.applyWindowInsets
import com.netchar.common.extensions.getStringSafe
import com.netchar.common.extensions.injectViewModel
import com.netchar.common.extensions.toast
import com.netchar.common.utils.Injector
import com.netchar.common.utils.getVersionName
import com.netchar.common.utils.navigation.IToolbarNavigationBinder
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.ViewModelFactory
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SettingsFragment : PreferenceFragmentCompat(), CoroutineScope, HasSupportFragmentInjector, SharedPreferences.OnSharedPreferenceChangeListener {

    private val job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var navigationBinder: IToolbarNavigationBinder

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun supportFragmentInjector() = childFragmentInjector

    private lateinit var activityContext: Context

    private lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        Injector.inject(this)
        super.onAttach(context)
        activityContext = context
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        launch {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar).also { it.applyWindowInsets() }

        viewModel = injectViewModel(viewModelFactory)

        launch {
            navigationBinder.bind(this@SettingsFragment, toolbar)
        }

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
        val themePreference = findPreference<Preference>(getString(R.string.preference_option_key_theme))
        val buildPreference = findPreference<Preference>(getString(R.string.preference_option_key_build))

        buildPreference?.summary = activityContext.getVersionName()
        themePreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
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
                AppCompatDelegate.setDefaultNightMode(themeMode.toInt())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }
}
