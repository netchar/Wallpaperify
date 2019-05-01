package com.netchar.models.apirequest

data class Paging(val startPage: Int = 1) {
    private var page: Int = startPage
    fun nextPage(): Int = ++page
    fun prevPage(): Int = if (page > 1) --page else page
    fun fromStart(): Int = startPage
}