package com.netchar.wallpaperify.data.repository

import com.netchar.wallpaperify.data.remote.dto.Photo
import kotlinx.coroutines.CoroutineScope

interface IPhotosRepository {
    fun getPhotos(page: Int, perPage: Int, orderBy: String, scope: CoroutineScope): IBoundResource<List<Photo>>
}
