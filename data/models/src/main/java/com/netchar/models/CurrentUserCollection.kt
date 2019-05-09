package com.netchar.models


import com.squareup.moshi.Json

data class CurrentUserCollection(
        val id: Int = 0,
        val title: String = "",
        @field:Json(name = "published_at")
        val publishedAt: String = "",
        @field:Json(name = "updated_at")
        val updatedAt: String = "",
        val curated: Boolean = false,
        @field:Json(name = "cover_photo")
        val coverPhoto: Any? = Any(),
        val user: Any? = Any()
)