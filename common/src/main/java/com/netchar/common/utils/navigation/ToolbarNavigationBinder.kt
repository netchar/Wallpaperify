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

package com.netchar.common.utils.navigation

import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.netchar.common.base.IDrawerActivity
import com.netchar.common.extensions.setSupportActionBar
import timber.log.Timber

class ToolbarNavigationBinder : IToolbarNavigationBinder {

    override fun bind(fragment: Fragment, toolbar: Toolbar?) {
        if (toolbar != null) {
            fragment.setSupportActionBar(toolbar)
            setupToolbarNavigation(fragment, toolbar)
        } else {
            Timber.w("Toolbar is null. Unable to setup navigation for ${fragment::class.java.name}")
        }
    }

    private fun setupToolbarNavigation(fragment: Fragment, toolbar: Toolbar) {
        val activity = fragment.activity

        if (activity is IDrawerActivity) {
            val navigationController = fragment.findNavController()
            val currentDestinationId = navigationController.currentDestination?.id ?: throw IllegalArgumentException("Wrong currentDestinationId")

            toolbar.setupWithNavController(navigationController, activity.appBarConfiguration)
            activity.setupNavigationDrawer(currentDestinationId)
        } else {
            toolbar.setupWithNavController(fragment.findNavController())
        }
    }

    private fun IDrawerActivity.setupNavigationDrawer(currentDestinationId: Int) = with(appBarConfiguration) {
        val lockMode = getLockMode(this, currentDestinationId)
        drawerLayout?.setDrawerLockMode(lockMode)
    }

    private fun getLockMode(configuration: AppBarConfiguration, currentDestinationId: Int): Int {
        val isTopLevelDestination = configuration.topLevelDestinations.contains(currentDestinationId)
        return if (isTopLevelDestination) DrawerLayout.LOCK_MODE_UNDEFINED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
    }
}