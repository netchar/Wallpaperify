package com.netchar.wallpaperify.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.factories.ViewModelFactory
import com.netchar.wallpaperify.infrastructure.extensions.injectViewModel
import com.netchar.wallpaperify.ui.base.BaseActivity
import com.netchar.wallpaperify.ui.base.BaseFragment
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), IDrawerActivity {
    companion object {
        const val BACK_DOUBLE_TAP_TIMEOUT = 1000L
    }

    private val navigationController: NavController by lazy(LazyThreadSafetyMode.NONE) {
        findNavController(R.id.main_navigation_fragment)
    }


    @Inject
    lateinit var factory: ViewModelFactory

    private lateinit var viewModel: MainViewModel

    private lateinit var toggle: ActionBarDrawerToggle

    override val layoutResId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = injectViewModel(factory)
        setupNavigation(toolbar!!)
    }

    fun setupNavigation(toolbar: Toolbar) {
        drawer_navigation_view.setupWithNavController(navigationController)

//        val appBarConfiguration = AppBarConfiguration.Builder(setOf(R.id.homeFragment, R.id.settingsFragment))
//            .setDrawerLayout(drawer_layout)
//            .build()

        setupActionBarWithNavController(navigationController, appBarConfiguration)
        setupNavigationDrawer(toolbar, drawer_layout)
    }

    val appBarConfiguration by lazy {
        AppBarConfiguration.Builder(setOf(R.id.homeFragment, R.id.settingsFragment))
            .setDrawerLayout(drawer_layout)
            .build()
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return  NavigationUI.navigateUp(navigationController, drawer_layout)
//    }

    override fun onSupportNavigateUp(): Boolean {
//
//        val sadas = NavigationUI.navigateUp(navigationController, drawer_layout)
//
//        val drawerLayout = drawer_layout
//        val currentDestination = navigationController.getCurrentDestination()
//        val topLevelDestinations = setOf(R.id.homeFragment, R.id.settingsFragment)
//        val isIt = drawerLayout != null && currentDestination != null && matchDestinations(currentDestination!!, topLevelDestinations)
//        if (isIt) {
////            drawerLayout!!.openDrawer(1)
//            val rasas = 0
//        }

        return NavigationUI.navigateUp(navigationController, appBarConfiguration)
    }


    internal fun matchDestinations(destination: NavDestination, destinationIds: Set<Int>): Boolean {
        var currentDestination: Any? = destination

        while (!destinationIds.contains((currentDestination as NavDestination).id)) {
            currentDestination = currentDestination.parent
            if (currentDestination == null) {
                return false
            }
        }

        return true
    }

//    } NavigationUI.navigateUp(navigationController, drawer_layout)

    private fun setupNavigationDrawer(toolbar: Toolbar, drawerLayout: DrawerLayout) {
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (toggle.onOptionsItemSelected(item)) {
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            drawer_layout.removeDrawerListener(toggle)
        }
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
