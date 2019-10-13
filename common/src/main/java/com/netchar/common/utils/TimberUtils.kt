package com.netchar.common.utils

import android.util.Log
import com.crashlytics.android.Crashlytics
import timber.log.Timber


/**
 * Created by Netchar on 08.05.2019.
 * e.glushankov@gmail.com
 */

class DebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "${super.createStackElementTag(element)}.${element.methodName}(); Line: ${element.lineNumber}"
    }
}

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {

        if (priority == Log.ERROR || priority == Log.WARN) {
            Crashlytics.log(priority, tag, message)

            if (throwable != null) {
                Crashlytics.logException(throwable)
            }
        }
    }
}
