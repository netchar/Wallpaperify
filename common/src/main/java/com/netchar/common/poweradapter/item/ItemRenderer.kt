package com.netchar.common.poweradapter.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

abstract class ItemRenderer {

    abstract val renderKey: String

    @LayoutRes
    abstract fun layoutRes(): Int

    open fun createView(parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(layoutRes(), parent, false)
    }

    abstract fun bind(itemView: View, item: IRecyclerItem)

    open fun onSetListeners(itemView: View) {}
}


