package com.netchar.remote.dto

import com.squareup.moshi.Json

data class ProfileImage(
    @Json(name = "large")
    val large: String,
    @Json(name = "medium")
    val medium: String,
    @Json(name = "small")
    val small: String
)