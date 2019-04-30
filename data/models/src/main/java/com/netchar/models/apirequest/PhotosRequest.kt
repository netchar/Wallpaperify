package com.netchar.models.apirequest

data class PhotosRequest(val page: Int = 0, val perPage: Int = 30, val orderBy: String = LATEST) {
    companion object {
        const val LATEST = "latest"
        const val OLDEST = "oldest"
        const val POPULAR = "popular"

        const val ITEMS_PER_PAGE = 30
    }
}