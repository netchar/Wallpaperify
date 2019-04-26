package com.netchar.wallpaperify.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.GravityCompat
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
        private val topLevelFragmentsIds = setOf(R.id.homeFragment)
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
        super.onCreate(savedInstanceState)
        drawer_navigation_view.setupWithNavController(navigationController)
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
