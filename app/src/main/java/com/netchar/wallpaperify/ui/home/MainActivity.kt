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

package com.netchar.wallpaperify.ui.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.forEach
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.netchar.common.base.BaseActivity
import com.netchar.common.base.BaseFragment
import com.netchar.common.base.IDrawerActivity
import com.netchar.wallpaperify.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), IDrawerActivity {

    companion object {
        private const val BACK_DOUBLE_TAP_TIMEOUT = 1000L
        private val topLevelFragmentsIds = setOf(R.id.homeFragment, R.id.settingsFragment)
    }

    override val layoutResId = R.layout.activity_main

    private val navigationController: NavController by lazy(LazyThreadSafetyMode.NONE) {
        findNavController(R.id.main_navigation_fragment)
    }

    override val appBarConfiguration: AppBarConfiguration by lazy(LazyThreadSafetyMode.NONE) {
        AppBarConfiguration.Builder(topLevelFragmentsIds)
                .setDrawerLayout(drawer_layout)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_DayNight)
        super.onCreate(savedInstanceState)

        drawer_navigation_view.setupWithNavController(navigationController)
        enableDrawContentUnderStatusBars()

        drawer_layout.setOnApplyWindowInsetsListener { _, insets ->
            drawer_navigation_view.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }

        main_navigation_fragment.view?.setOnApplyWindowInsetsListener { view, insets ->
            propagateInsetsDownToAllChildren(view, insets)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navigationController, appBarConfiguration)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        }

        if (isBackPressedFromFragment()) {
            return
        }

        if (navigationController.navigateUp()) {
            return
        }

        runByDoubleBack {
            this.finishAffinity()
        }
    }

    private fun propagateInsetsDownToAllChildren(view: View?, insets: WindowInsets): WindowInsets? {
        var consumed = false
        (view as ViewGroup).forEach { child ->
            // Dispatch the insets to the child
            val childResult = child.dispatchApplyWindowInsets(insets)
            // If the child consumed the insets, record it
            if (childResult.isConsumed) {
                consumed = true
            }
        }

        // If any of the children consumed the insets, return
        // an appropriate value
        return if (consumed) insets.consumeSystemWindowInsets() else insets
    }

    private fun enableDrawContentUnderStatusBars() {
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }


    private fun isBackPressedFromFragment(): Boolean {
        val currentFragment = main_navigation_fragment.childFragmentManager.primaryNavigationFragment
        return currentFragment != null && currentFragment is BaseFragment && currentFragment.onBackPressed()
    }

    private inline fun runByDoubleBack(runAction: () -> Unit) {
        val backPressElapsed = System.currentTimeMillis() - lastPressedTime
        if (backPressElapsed in 0..BACK_DOUBLE_TAP_TIMEOUT) {
            runAction()
        } else {
            Toast.makeText(this, getString(R.string.main_toast_confirm_back), Toast.LENGTH_SHORT).show()
            lastPressedTime = System.currentTimeMillis()
        }
    }

    private var lastPressedTime: Long = 0
}
