package com.netchar.models

import com.squareup.moshi.Json

data class LinksX(
    @Json(name = "download")
    val download: String,
    @Json(name = "download_location")
    val downloadLocation: String,
    @Json(name = "html")
    val html: String,
    @Json(name = "self")
    val self: String
)