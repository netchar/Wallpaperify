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

package com.netchar.repository.utils

import com.netchar.remote.dto.*
import com.netchar.remote.dto.Collection
import com.netchar.repository.pojo.*

object Mapper {

    fun map(photo: Photo): PhotoPOJO {
        return PhotoPOJO(
                id = photo.id,
                width = photo.width,
                height = photo.height,
                color = photo.color,
                likes = photo.likes,
                downloads = photo.downloads,
                description = photo.description,
                user = map(photo.user),
                urls = map(photo.urls),
                links = map(photo.links)
        )
    }

    fun map(user: User): UserPOJO {
        return UserPOJO(
                id = user.id,
                username = user.username,
                name = user.name,
                profileImage = map(user.profileImage),
                links = map(user.links)
        )
    }

    fun map(profileImage: ProfileImage): ProfileImagePOJO {
        return ProfileImagePOJO(
                small = profileImage.small,
                medium = profileImage.medium,
                large = profileImage.large
        )
    }

    fun map(urls: Urls): UrlsPOJO {
        return UrlsPOJO(
                raw = urls.raw,
                full = urls.full,
                regular = urls.regular,
                small = urls.small,
                thumb = urls.thumb
        )
    }

    fun map(links: Links): LinksPOJO {
        return LinksPOJO(
                self = links.self,
                html = links.html,
                download = links.download,
                downloadLocation = links.downloadLocation,
                photos = "",
                related = ""
        )
    }

    fun map(links: UserLinks): LinksPOJO {
        return LinksPOJO(
                html = links.html
        )
    }

    fun map(links: CollectionLinks): LinksPOJO {
        return LinksPOJO(
                self = links.self,
                html = links.html,
                download = "",
                downloadLocation = "",
                photos = links.photos,
                related = links.related
        )
    }

    fun map(collection: Collection): CollectionPOJO {
        return CollectionPOJO(
                id = collection.id,
                title = collection.title,
                description = collection.description ?: "",
                publishedAt = collection.publishedAt,
                updatedAt = collection.updatedAt,
                curated = collection.curated,
                totalPhotos = collection.totalPhotos,
                isPrivate = collection.isPrivate,
                shareKey = collection.shareKey,
                coverPhoto = map(collection.coverPhoto),
                user = map(collection.user),
                links = map(collection.links)
        )
    }
}