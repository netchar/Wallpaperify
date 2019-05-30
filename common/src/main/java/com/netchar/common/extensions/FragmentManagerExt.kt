package com.netchar.common.extensions

import androidx.fragment.app.FragmentManager


/**
 * Created by Netchar on 17.03.2019.
 * e.glushankov@gmail.com
 */

fun FragmentManager.canPopFragment() = this.backStackEntryCount > 0