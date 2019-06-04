package com.netchar.wallpaperify.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceFragmentCompat
import com.netchar.common.base.IDrawerActivity
import com.netchar.common.extensions.setSupportActionBar
import com.netchar.wallpaperify.R

// todo: refactor this ASAP
class SettingsFragment : PreferenceFragmentCompat() {

    protected var fragmentToolbar: Toolbar? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    private fun applyWindowInsetsForToolbarOnly() {
        this.fragmentToolbar?.setOnApplyWindowInsetsListener { toolbar, windowInsets ->
            toolbar.updatePadding(top = windowInsets.systemWindowInsetTop, bottom = 0)
            // consuming insets will stop propagating them to other children
            windowInsets.consumeSystemWindowInsets()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentToolbar = view.findViewById<Toolbar>(com.netchar.common.R.id.toolbar)?.also { it.setupNavigation() }
        drawerActivity?.setupNavigationDrawer()
        applyWindowInsetsForToolbarOnly()
    }

    private val drawerActivity get() = activity as? IDrawerActivity

    private fun IDrawerActivity.setupNavigationDrawer() = appBarConfiguration.setupNavigationDrawer()

    private fun Toolbar?.setupNavigation() = this?.let {
        setSupportActionBar(it)
        setupToolbarNavigation(it)
    }

    private fun AppBarConfiguration.setupNavigationDrawer() = drawerLayout?.let { drawer ->
        val mode = getLockMode(this)
        drawer.setDrawerLockMode(mode)
    }

    private fun getLockMode(configuration: AppBarConfiguration): Int {
        val isTopLevelDestination = configuration.topLevelDestinations.contains(findNavController().currentDestination?.id)
        return if (isTopLevelDestination) DrawerLayout.LOCK_MODE_UNDEFINED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
    }

    private fun setupToolbarNavigation(toolbar: Toolbar) {
        val drawerAct = drawerActivity
        if (drawerAct == null) {
            toolbar.setupWithNavController(findNavController())
        } else {
            toolbar.setupWithNavController(findNavController(), drawerAct.appBarConfiguration)
        }
    }
}
