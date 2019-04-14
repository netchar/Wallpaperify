package com.netchar.wallpaperify.ui.home

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.factories.ViewModelFactory
import com.netchar.wallpaperify.infrastructure.extensions.injectViewModel
import com.netchar.wallpaperify.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {
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
//
//        setupNavigationDrawer()
//        setupNavigation()

//        setupNavigationDrawer()
//        setupBottomNavigationView()
    }


//    private fun setupNavigation() {
//        drawer_navigation_view.setupWithNavController(navigationController)
//        setupActionBarWithNavController(navigationController, drawer_layout)
////        bottom_navigation_view.setupWithNavController(navigationController)
//    }

    fun setupNavigation() {
        drawer_navigation_view.setupWithNavController(navigationController)
        setupActionBarWithNavController(navigationController, drawer_layout)

        if (!::toggle.isInitialized) {
            setupNavigationDrawer(findViewById(R.id.toolbar), drawer_layout)
        }
    }

    //    override fun onSupportNavigateUp() = NavigationUI.navigateUp(navigationController, drawer_layout)
    override fun onSupportNavigateUp() = NavigationUI.navigateUp(navigationController, drawer_layout)


//    private fun setupBottomNavigationView() {
//        bottom_navigation_view.setOnNavigationItemSelectedListener(::onBottomNavigationItemSelected)
//    }

//    private fun onBottomNavigationItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.navigation_bottom_bar_latest -> {
//            }
//            R.id.navigation_bottom_bar_trending -> {
//            }
//            R.id.navigation_bottom_bar_collections -> {
//            }
//        }
//
//        return true
//    }

    private fun setupNavigationDrawer(toolbar: Toolbar, drawerLayout: DrawerLayout) {
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
//        nav_view.setNavigationItemSelectedListener(::onNavigationItemSelected)
    }

//    private fun onNavigationItemSelected(item: MenuItem): Boolean {
//        // Handle navigation view item clicks here.
//        when (item.itemId) {
//        }
//
//        drawer_layout.closeDrawer(GravityCompat.START)
//        return true
//    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
//        setupNavigation(findViewById(R.id.toolbar))
        setupNavigation()
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
////        NavigationUI.onNavDestinationSelected(item, navigationControler)
//        return super.onOptionsItemSelected(item)
//    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        }

        super.onBackPressed()

//        if (isBackPressedFromFragment()) {
//            return
//        }
//
//        if (supportFragmentManager.canPopFragment()) {
//            super.onBackPressed()
//        } else {
//            runByDoubleBack { this.finishAffinity() }
//        }
    }

//    private fun isBackPressedFromFragment(): Boolean {
//        val currentFragment = supportFragmentManager.getCurrentFragment()
//        return currentFragment != null && currentFragment.onBackPressed()
//    }

//    private inline fun runByDoubleBack(runAction: () -> Unit) {
//        val backPressElapsed = System.currentTimeMillis() - lastPressedTime
//        if (backPressElapsed in 0..BACK_DOUBLE_TAP_TIMEOUT) {
//            runAction()
//        } else {
//            Toast.makeText(this, getString(R.string.main_toast_confirm_back), Toast.LENGTH_SHORT).show()
//            lastPressedTime = System.currentTimeMillis()
//        }
//    }
//
//    private var lastPressedTime: Long = 0
}
