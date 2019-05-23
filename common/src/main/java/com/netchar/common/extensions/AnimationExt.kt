package com.netchar.common.extensions

import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import com.netchar.common.base.BaseFragment


/**
 * Created by Netchar on 5/23/2019.
 * e.glushankov@gmail.com
 */

fun Transition.onTransitionEnd(action: () -> Unit) {
    this.addListener(object : TransitionListenerAdapter() {
        override fun onTransitionEnd(transition: Transition) {
            removeListener(this)
            action()
        }
    })
}

fun BaseFragment.inflateTransition(transitionResId: Int) = TransitionInflater.from(context).inflateTransition(transitionResId)