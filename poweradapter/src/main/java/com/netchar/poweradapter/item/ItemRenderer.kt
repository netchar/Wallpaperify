package com.netchar.poweradapter.item

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

interface ItemRenderer<T : IRecyclerItem> {

    @LayoutRes
    fun layoutRes(): Int

    fun createView(parent: ViewGroup): View

    fun render(itemView: View, item: T)
}