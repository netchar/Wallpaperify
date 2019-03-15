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
        private val backPressConfirmationHandler = Handler()
    }

    @Inject
    lateinit var factory: ViewModelFactory

    lateinit var viewModel: MainViewModel

    private var isDoubleTapConfirmed = false

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
        if (isDoubleTapConfirmed) {
            runAction()
        } else {
            Toast.makeText(this, "Are you sure you want to exit?", Toast.LENGTH_SHORT).show()
            isDoubleTapConfirmed = true
            backPressConfirmationHandler.postDelayed({ isDoubleTapConfirmed = false }, DOUBLE_TAP_TIMEOUT)
        }
    }
}
