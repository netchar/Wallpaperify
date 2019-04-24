package com.netchar.wallpaperify.ui.latest

import com.netchar.poweradapter.item.IRecyclerItem


/**
 * Created by Netchar on 03.04.2019.
 * e.glushankov@gmail.com
 */

data class LatestPhotoRecyclerItem(val data: com.netchar.remote.dto.Photo) : IRecyclerItem {

    override fun getId(): Long = data.hashCode().toLong()

    override fun getRenderKey(): String = LatestPhotosRenderer::class.java.name
}