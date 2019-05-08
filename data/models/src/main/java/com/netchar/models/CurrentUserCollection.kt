package com.netchar.models


import com.squareup.moshi.Json

data class CurrentUserCollection(
        val id: Int = 0,
        val title: String = "",
        @Json(name = "published_at")
        val publishedAt: String = "",
        @Json(name = "updated_at")
        val updatedAt: String = "",
        val curated: Boolean = false,
        @Json(name = "cover_photo")
        val coverPhoto: Any? = Any(),
        val user: Any? = Any()
)