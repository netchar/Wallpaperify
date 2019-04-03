package com.netchar.poweradapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

//interface IItemRenderer<T : IRecyclerItem> {
//
//    @LayoutRes
//    fun layoutRes(): Int
//
//    fun createView(parent: ViewGroup): View
//
//    fun render(itemView: View, item: T)
//}

abstract class ItemRenderer<T : IRecyclerItem> {
    @LayoutRes
    abstract fun layoutRes(): Int

    open fun createView(parent: ViewGroup): View {
        // todo: create extension after extracting new 'tools' module
        return LayoutInflater.from(parent.context).inflate(layoutRes(), parent, false)
    }

    abstract fun onBind(itemView: View, model: T)

    open fun onSetListeners(itemView: View) {

    }
}


