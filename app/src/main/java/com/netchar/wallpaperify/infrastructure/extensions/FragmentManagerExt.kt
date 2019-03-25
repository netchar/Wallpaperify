package com.netchar.wallpaperify.infrastructure.extensions

import android.support.annotation.IdRes
import android.support.v4.app.FragmentManager
import com.netchar.wallpaperify.R
import com.netchar.wallpaperify.ui.base.BaseFragment


/**
 * Created by Netchar on 17.03.2019.
 * e.glushankov@gmail.com
 */

fun FragmentManager.canPopFragment() = this.backStackEntryCount > 0

fun FragmentManager.getCurrentFragment(@IdRes resId: Int = R.id.fragment_container) = this.findFragmentById(resId) as? BaseFragment