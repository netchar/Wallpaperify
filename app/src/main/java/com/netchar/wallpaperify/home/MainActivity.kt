package com.netchar.wallpaperify.home

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.base.BaseActivity
import com.netchar.wallpaperify.base.BaseFragment
import com.netchar.wallpaperify.di.factories.ViewModelFactory
import com.netchar.wallpaperify.infrastructure.extensions.injectViewModel
import javax.inject.Inject


class MainActivity : BaseActivity() {

    companion object {
        const val DOUBLE_TAP_TIMEOUT = 1000L
    }

    @Inject
    lateinit var factory: ViewModelFactory

    lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = injectViewModel(factory)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, MainFragment.newInstance())
                .commit()
        }
    }

    override fun onBackPressed() {
        if (isBackPerformedFromFragment()) {
            return
        }

        val canGoBack = supportFragmentManager.backStackEntryCount > 0
        if (canGoBack) {
            super.onBackPressed()
        } else {
            runByConfirm { this.finishAffinity() }
        }
    }

    private fun isBackPerformedFromFragment(): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as? BaseFragment
        return currentFragment != null && currentFragment.onBackPressed()
    }

    private inline fun runByConfirm(runAction: () -> Unit) {
        val backPressElapsed = System.currentTimeMillis() - lastPressedTime
        if (backPressElapsed in 0..DOUBLE_TAP_TIMEOUT) {
            runAction()
        } else {
            Toast.makeText(this, "Press 'back' again to ", Toast.LENGTH_SHORT).show()
            lastPressedTime = System.currentTimeMillis()
        }
    }

    private var lastPressedTime: Long = 0

}
