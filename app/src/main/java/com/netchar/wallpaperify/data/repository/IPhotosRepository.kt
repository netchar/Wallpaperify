package com.netchar.wallpaperify.data.repository

import kotlinx.coroutines.CoroutineScope

interface IPhotosRepository {
    fun getPhotos(request: PhotosRepository.PhotosApiRequest, scope: CoroutineScope): IBoundResource<List<com.netchar.models.Photo>>
}
