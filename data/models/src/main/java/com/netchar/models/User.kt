package com.netchar.models


import com.squareup.moshi.Json

data class User(
        val id: String = "",
        val username: String = "",
        val name: String = "",
        @field:Json(name = "portfolio_url")
        val portfolioUrl: String = "",
        val bio: String = "",
        val location: String = "",
        @field:Json(name = "total_likes")
        val totalLikes: Int = 0,
        @field:Json(name = "total_photos")
        val totalPhotos: Int = 0,
        @field:Json(name = "total_collections")
        val totalCollections: Int = 0,
        @field:Json(name = "instagram_username")
        val instagramUsername: String = "",
        @field:Json(name = "twitter_username")
        val twitterUsername: String = "",
        @field:Json(name = "profile_image")
        val profileImage: ProfileImage = ProfileImage(),
        val links: UserLinks = UserLinks()
)