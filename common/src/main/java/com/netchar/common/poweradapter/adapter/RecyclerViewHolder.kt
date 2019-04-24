package com.netchar.common.poweradapter.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.common.poweradapter.item.ItemRenderer

class RecyclerViewHolder(
    parent: ViewGroup,
    private val renderer: ItemRenderer
) : RecyclerView.ViewHolder(renderer.createView(parent)) {

    init {
        renderer.onSetListeners(itemView)
    }

    internal fun bind(item: IRecyclerItem) {
        renderer.bind(itemView, item)
    }
}
