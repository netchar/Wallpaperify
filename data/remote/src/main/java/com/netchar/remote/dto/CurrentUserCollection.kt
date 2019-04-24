package com.netchar.remote.dto

import com.squareup.moshi.Json

data class CurrentUserCollection(
    @Json(name = "cover_photo")
    val coverPhoto: Any?,
    @Json(name = "curated")
    val curated: Boolean,
    @Json(name = "id")
    val id: Int,
    @Json(name = "published_at")
    val publishedAt: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "updated_at")
    val updatedAt: String,
    @Json(name = "user")
    val user: Any?
)