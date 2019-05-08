package com.netchar.models


import com.squareup.moshi.Json

data class Links(
        val self: String = "",
        val html: String = "",
        val download: String = "",
        @Json(name = "download_location")
        val downloadLocation: String = ""
)