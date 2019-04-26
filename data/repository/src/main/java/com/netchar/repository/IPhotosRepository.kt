package com.netchar.repository

import androidx.annotation.StringDef
import com.netchar.models.Photo
import kotlinx.coroutines.CoroutineScope

interface IPhotosRepository {
    data class PhotosApiRequest(val page: Int = 0, val perPage: Int = 30, @OrderBy val orderBy: String = LATEST, val forceFetching: Boolean = false) {
        companion object {
            const val LATEST = "latest"
            const val OLDEST = "oldest"
            const val POPULAR = "popular"

            const val ITEMS_PER_PAGE = 30
        }

        @StringDef(LATEST, OLDEST, POPULAR)
        @Target(AnnotationTarget.VALUE_PARAMETER)
        @Retention(AnnotationRetention.SOURCE)
        annotation class OrderBy
    }

    fun getPhotos(request: PhotosApiRequest, scope: CoroutineScope): IBoundResource<List<Photo>>
}
