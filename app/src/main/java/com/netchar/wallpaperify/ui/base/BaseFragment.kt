package com.netchar.wallpaperify.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CheckResult
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar
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

    private val baseActivity by lazy {
        activity as BaseActivity
    }

    override fun onAttach(context: Context?) {
        Injector.inject(this)
        super.onAttach(context)
    }

    override fun supportFragmentInjector(): AndroidInjector<androidx.fragment.app.Fragment> = childFragmentInjector

    protected var toolbar: Toolbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(layoutResId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupToolbar(view)
    }

    private fun setupToolbar(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        if (toolbar != null) {
            baseActivity.setSupportActionBar(toolbar)
            baseActivity.supportActionBar?.let {
                it.setHomeButtonEnabled(true)
                it.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    @CheckResult
    open fun onBackPressed(): Boolean {
        return false
    }
}