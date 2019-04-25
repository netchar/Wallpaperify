package com.netchar.models

import com.squareup.moshi.Json

data class Links(
    @Json(name = "html")
    val html: String,
    @Json(name = "likes")
    val likes: String,
    @Json(name = "photos")
    val photos: String,
    @Json(name = "portfolio")
    val portfolio: String,
    @Json(name = "self")
    val self: String
)