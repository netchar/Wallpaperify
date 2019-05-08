package com.netchar.common.utils

import android.util.Log
import timber.log.Timber


/**
 * Created by Netchar on 08.05.2019.
 * e.glushankov@gmail.com
 */

class DebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "Class:${super.createStackElementTag(element)}: Line: ${element.lineNumber}, Method: ${element.methodName}"
    }
}

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR || priority == Log.WARN){
            //todo: Firebase crashlytics
        }
    }
}
