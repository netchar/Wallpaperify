package com.netchar.repository

import com.netchar.models.Photo
import kotlinx.coroutines.CoroutineScope

interface IPhotosRepository {
    fun getPhotos(request: PhotosRepository.PhotosApiRequest, scope: CoroutineScope): IBoundResource<List<Photo>>
}
