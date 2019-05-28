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

package com.netchar.common.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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

    protected var fragmentToolbar: Toolbar? = null

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
        fragmentToolbar = view.findViewById<Toolbar>(R.id.toolbar)?.also { it.setupNavigation() }
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

    /*
        Disable touch events during fragment page transition animation. Since it
        cause it's allows to add a view in parent with not yet detached view and cause IllegalStateException
     */
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animation: Animation?
        if (nextAnim == 0) {
            animation = super.onCreateAnimation(transit, enter, nextAnim)
        } else {
            animation = AnimationUtils.loadAnimation(activity, nextAnim)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    activity?.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

                override fun onAnimationRepeat(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }
            })
        }
        return animation
    }
}

