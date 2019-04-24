package com.netchar.wallpaperify.ui.latest


/**
 * Created by Netchar on 03.04.2019.
 * e.glushankov@gmail.com
 */

data class LatestPhotoRecyclerItem(val data: com.netchar.models.Photo) : com.netchar.common.poweradapter.item.IRecyclerItem {

    override fun getId(): Long = data.hashCode().toLong()

    override fun getRenderKey(): String = LatestPhotosRenderer::class.java.name
}