package com.netchar.remote.dto

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class UnsplashError(
    @Json(name = "errors")
    val errors: List<String>
)