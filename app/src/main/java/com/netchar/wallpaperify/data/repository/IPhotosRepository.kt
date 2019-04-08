package com.netchar.wallpaperify.data.repository

import com.netchar.wallpaperify.data.remote.dto.Photo
import kotlinx.coroutines.CoroutineScope

interface IPhotosRepository {
    fun getPhotos(request: PhotosRepository.PhotosApiRequest, scope: CoroutineScope): IBoundResource<List<Photo>>
}
