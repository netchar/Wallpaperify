package com.netchar.wallpaperify.ui.collections

import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.models.Collection

data class CollectionRecyclerItem(val data: Collection) : IRecyclerItem {
    override fun getId(): Long = data.hashCode().toLong()
    override fun getRenderKey(): String = CollectionRenderer::class.java.name
}