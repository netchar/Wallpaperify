package com.netchar.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.squareup.moshi.Json

data class User(
        @Json(name = "bio")
        val bio: String?,

        @Json(name = "id")
        @ColumnInfo(name = "user_id")
        val id: String,

        @Json(name = "instagram_username")
        val instagramUsername: String,

        @Json(name = "links")
        @Embedded
        val links: Links?,

        @Json(name = "location")
        val location: String?,

        @Json(name = "name")
        val name: String,

        @Json(name = "portfolio_url")
        val portfolioUrl: String?,

        @Json(name = "profile_image")
        @Embedded
        val profileImage: ProfileImage?,

        @Json(name = "total_collections")
        val totalCollections: Int,

        @Json(name = "total_likes")
        val totalLikes: Int,

        @Json(name = "total_photos")
        val totalPhotos: Int,

        @Json(name = "twitter_username")
        val twitterUsername: String?,

        @Json(name = "username")
        val username: String
) {
    constructor() : this("", "", "", null, "", "", "", null, 0, 0, 0, "", "")
}