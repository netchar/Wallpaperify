package com.netchar.models

import com.squareup.moshi.Json

data class UnsplashError(
    @field:Json(name = "errors")
    val errors: List<String>
)