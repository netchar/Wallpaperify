package com.netchar.common.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.netchar.common.R
import com.netchar.common.utils.Injector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        Injector.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)
        setupToolbar()
    }

    @get:LayoutRes
    abstract val layoutResId: Int

    protected var toolbar: Toolbar? = null

    protected fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.let {
                it.setHomeButtonEnabled(true)
                it.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return fragmentInjector
    }
}