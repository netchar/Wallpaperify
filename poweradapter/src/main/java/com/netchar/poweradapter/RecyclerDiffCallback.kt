package com.netchar.poweradapter

import androidx.recyclerview.widget.DiffUtil
import com.netchar.poweradapter.item.IRecyclerItem

class RecyclerDiffCallback internal constructor(
    private val oldList: List<IRecyclerItem>,
    private val newList: List<IRecyclerItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].itemId == newList[newItemPosition].itemId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
