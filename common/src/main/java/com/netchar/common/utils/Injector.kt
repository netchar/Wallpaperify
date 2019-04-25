package com.netchar.common.utils

import com.netchar.common.base.BaseActivity
import com.netchar.common.base.BaseFragment
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection


/**
 * Created by Netchar on 31.03.2019.
 * e.glushankov@gmail.com
 */
open class Injector {
    companion object {
        fun inject(fragment: BaseFragment) {
            AndroidSupportInjection.inject(fragment)
        }

        fun inject(activity: BaseActivity) {
            AndroidInjection.inject(activity)
        }
    }
}