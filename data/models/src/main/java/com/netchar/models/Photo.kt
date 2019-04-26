package com.netchar.models

import androidx.room.*
import com.squareup.moshi.Json

@Entity
data class Photo(
        @Json(name = "color")
        var color: String,

        @Json(name = "created_at")
        var createdAt: String,

        @Json(name = "current_user_collections")
        @Transient
        var currentUserCollections: List<CurrentUserCollection>?,

        @Json(name = "description")
        var description: String?,

        @Json(name = "height")
        var height: Int,

        @PrimaryKey(autoGenerate = false)
        @Json(name = "id")
        var id: String,

        @Json(name = "liked_by_user")
        var likedByUser: Boolean,

        @Json(name = "likes")
        var likes: Int,

        @Json(name = "links")
        @Embedded(prefix = "photo")
        var links: Links?,

        @Json(name = "updated_at")
        var updatedAt: String,

        @Json(name = "urls")
        @Embedded
        var urls: Urls?,

        @Json(name = "user")
        @Embedded
        var user: User?,

        @Json(name = "width")
        var width: Int
) {
    constructor() : this("", "", null, "", 0, "", false, 0, null, "", null, null, 0)

    init {
        currentUserCollections = listOf(CurrentUserCollection(null, true, 1, "", "Tite", "", ""), CurrentUserCollection(null, true, 1, "", "Tite2", "", ""))
    }
}