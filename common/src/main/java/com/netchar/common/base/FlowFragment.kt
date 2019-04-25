package com.netchar.common.base

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController


/**
 * Created by Netchar on 18.04.2019.
 * e.glushankov@gmail.com
 */

abstract class FlowFragment : BaseFragment() {

    override fun setupToolbar(view: View) {
        super.setupToolbar(view)
        drawerActivity?.setupDrawer()
    }

    private fun IDrawerActivity.setupDrawer() {
        appBarConfiguration.drawerLayout?.let {
            val isCurrentFragmentFromTopLevelDestinations = appBarConfiguration.topLevelDestinations.contains(findNavController().currentDestination?.id)
            it.setDrawerLockMode(if (isCurrentFragmentFromTopLevelDestinations) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }
}

