package com.netchar.models

import androidx.room.ColumnInfo
import com.squareup.moshi.Json

data class ProfileImage(
    @Json(name = "large")
    @ColumnInfo(name = "profile_image_large")
    val large: String,

    @Json(name = "medium")
    @ColumnInfo(name = "profile_image_medium")
    val medium: String,

    @Json(name = "small")
    @ColumnInfo(name = "profile_image_small")
    val small: String
)