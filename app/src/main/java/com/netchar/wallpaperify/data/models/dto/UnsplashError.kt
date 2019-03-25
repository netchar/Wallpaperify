package com.netchar.wallpaperify.data.models.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class UnsplashError(
    @Json(name = "errors")
    val errors: List<String>
)