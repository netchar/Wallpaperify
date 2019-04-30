package com.netchar.repository

import com.netchar.models.Photo
import com.netchar.models.apirequest.PhotosRequest
import kotlinx.coroutines.CoroutineScope

interface IPhotosRepository {

    fun getPhotos(request: PhotosRequest, scope: CoroutineScope): IBoundResource<List<Photo>>
}
