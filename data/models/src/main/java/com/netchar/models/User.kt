package com.netchar.models


import com.squareup.moshi.Json

data class User(
        val id: String = "",
        val username: String = "",
        val name: String = "",
        @Json(name = "portfolio_url")
        val portfolioUrl: String = "",
        val bio: String = "",
        val location: String = "",
        @Json(name = "total_likes")
        val totalLikes: Int = 0,
        @Json(name = "total_photos")
        val totalPhotos: Int = 0,
        @Json(name = "total_collections")
        val totalCollections: Int = 0,
        @Json(name = "instagram_username")
        val instagramUsername: String = "",
        @Json(name = "twitter_username")
        val twitterUsername: String = "",
        @Json(name = "profile_image")
        val profileImage: ProfileImage = ProfileImage(),
        val links: UserLinks = UserLinks()
)