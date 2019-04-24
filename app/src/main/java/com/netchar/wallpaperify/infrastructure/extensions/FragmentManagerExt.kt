package com.netchar.wallpaperify.infrastructure.extensions


/**
 * Created by Netchar on 17.03.2019.
 * e.glushankov@gmail.com
 */

fun androidx.fragment.app.FragmentManager.canPopFragment() = this.backStackEntryCount > 0

//fun androidx.fragment.app.FragmentManager.getCurrentFragment(@IdRes resId: Int = R.id.fragment_container) = this.findFragmentById(resId) as? BaseFragment