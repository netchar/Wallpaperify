package com.netchar.wallpaperify.ui.photos

import com.netchar.common.poweradapter.item.IRecyclerItem
import com.netchar.models.Photo

/**
 * Created by Netchar on 03.04.2019.
 * e.glushankov@gmail.com
 */

data class PhotoRecyclerItem(val data: Photo) : IRecyclerItem {

    override fun getId(): Long = data.hashCode().toLong()

    override fun getRenderKey(): String = PhotosRenderer::class.java.name
}
