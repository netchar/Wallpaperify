package com.netchar.models

import com.squareup.moshi.Json

data class Urls(
        @Json(name = "full")
        var full: String,
        @Json(name = "raw")
        var raw: String,
        @Json(name = "regular")
        var regular: String,
        @Json(name = "small")
        var small: String,
        @Json(name = "thumb")
        var thumb: String
)