package com.netchar.wallpaperify.data.repositories

import com.netchar.wallpaperify.data.models.dto.Photo

interface IPhotosRepository {
    suspend fun getPhotos(): HttpResult<List<Photo>>
}
