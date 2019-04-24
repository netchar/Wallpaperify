package com.netchar.wallpaperify.ui.latest

import com.netchar.poweradapter.item.IRecyclerItem
import com.netchar.wallpaperify.data.remote.dto.Photo


/**
 * Created by Netchar on 03.04.2019.
 * e.glushankov@gmail.com
 */

data class LatestPhotoRecyclerItem(val data: Photo) : IRecyclerItem {

    override fun getId(): Long = data.hashCode().toLong()

    override fun getRenderKey(): String = LatestPhotosRenderer::class.java.name
}