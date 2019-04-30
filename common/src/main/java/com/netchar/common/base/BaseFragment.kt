package com.netchar.common.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.netchar.common.R
import com.netchar.common.extensions.setSupportActionBar
import com.netchar.common.utils.Injector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseFragment : Fragment(), HasSupportFragmentInjector {

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Fragment>

    @get:LayoutRes
    abstract val layoutResId: Int

    private val drawerActivity get() = activity as? IDrawerActivity

    protected var toolbar: Toolbar? = null

    override fun onAttach(context: Context) {
        Injector.inject(this)
        super.onAttach(context)
    }

    override fun supportFragmentInjector() = childFragmentInjector

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById<Toolbar>(R.id.toolbar)?.also { it.setupNavigation() }
        drawerActivity?.setupNavigationDrawer()
    }

    @CheckResult
    open fun onBackPressed(): Boolean {
        return false
    }

    private fun Toolbar?.setupNavigation() = this?.let {
        setSupportActionBar(it)
        setupToolbarNavigation(it)
    }

    private fun IDrawerActivity.setupNavigationDrawer() = appBarConfiguration.setupNavigationDrawer()

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

