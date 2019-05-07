package com.netchar.models.apirequest




data class PhotosRequest(val page: Int = 1, val orderBy: String = LATEST, val perPage: Int = 30) {
    companion object {


        const val ITEMS_PER_PAGE = 30
    }

    fun isStartPage() = page == 1
}