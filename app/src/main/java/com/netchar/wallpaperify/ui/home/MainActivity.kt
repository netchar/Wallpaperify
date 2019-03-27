package com.netchar.wallpaperify.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.Toast
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.base.BaseActivity
import com.netchar.wallpaperify.di.factories.ViewModelFactory
import com.netchar.wallpaperify.infrastructure.extensions.canPopFragment
import com.netchar.wallpaperify.infrastructure.extensions.getCurrentFragment
import com.netchar.wallpaperify.infrastructure.extensions.injectViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {
    companion object {
        const val BACK_DOUBLE_TAP_TIMEOUT = 1000L
    }

    @Inject
    lateinit var factory: ViewModelFactory

    lateinit var viewModel: MainViewModel

    private lateinit var toggle: ActionBarDrawerToggle

    override val layoutResId = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = injectViewModel(factory)

        setupNavigationDrawer()
        setupBottomNavigationView()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, MainFragment.newInstance())
                    .commit()
        }
    }

    private fun setupBottomNavigationView() {
        bottom_navigation_view.setOnNavigationItemSelectedListener(::onBottomNavigationItemSelected)
    }

    private fun onBottomNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.bottom_nav_home -> {

            }
            R.id.bottom_nav_featured -> {

            }
            R.id.bottom_nav_collections -> {

            }
        }

        return true
    }

    private fun setupNavigationDrawer() {
        toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        nav_view.setNavigationItemSelectedListener(::onNavigationItemSelected)
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        }

        if (isBackPressedFromFragment()) {
            return
        }

        if (supportFragmentManager.canPopFragment()) {
            super.onBackPressed()
        } else {
            runByDoubleBack { this.finishAffinity() }
        }
    }

    private fun isBackPressedFromFragment(): Boolean {
        val currentFragment = supportFragmentManager.getCurrentFragment()
        return currentFragment != null && currentFragment.onBackPressed()
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
