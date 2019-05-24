/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.remote.dto


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