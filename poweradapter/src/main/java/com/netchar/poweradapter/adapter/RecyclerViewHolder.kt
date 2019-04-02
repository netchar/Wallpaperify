package com.netchar.poweradapter.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netchar.poweradapter.item.IRecyclerItem
import com.netchar.poweradapter.item.ItemRenderer

class RecyclerViewHolder(
    parent: ViewGroup,
    private val renderer: ItemRenderer<IRecyclerItem>
) : RecyclerView.ViewHolder(renderer.createView(parent)) {

    internal fun bind(item: IRecyclerItem) {
        renderer.render(itemView, item)
    }
}
