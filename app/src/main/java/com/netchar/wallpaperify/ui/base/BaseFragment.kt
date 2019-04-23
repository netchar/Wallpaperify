package com.netchar.wallpaperify.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.di.Injector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

abstract class BaseFragment : androidx.fragment.app.Fragment(), HasSupportFragmentInjector {

    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>

    @get:LayoutRes
    abstract val layoutResId: Int

    protected val drawerActivity: IDrawerActivity? get() = activity as? IDrawerActivity

    protected var toolbar: Toolbar? = null

    override fun onAttach(context: Context) {
        Injector.inject(this)
        super.onAttach(context)
    }

    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> = childFragmentInjector

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(layoutResId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(view)
    }

    @CallSuper
    protected open fun setupToolbar(view: View) {
        val toolbar: Toolbar? = view.findViewById(R.id.toolbar)

        if (toolbar != null) {
            val drawerAct = drawerActivity

            if (drawerAct != null) {
                toolbar.setupWithNavController(findNavController(), drawerAct.appBarConfiguration)
            } else {
                toolbar.setupWithNavController(findNavController())
            }

            val currentActivity = activity as AppCompatActivity
            currentActivity.setSupportActionBar(toolbar)
        }
        this.toolbar = toolbar
    }

    @CheckResult
    open fun onBackPressed(): Boolean {
        return false
    }
}