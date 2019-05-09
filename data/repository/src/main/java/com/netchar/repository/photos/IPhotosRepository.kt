package com.netchar.repository.photos

import com.netchar.models.Photo
import com.netchar.models.apirequest.ApiRequest
import com.netchar.repository.IBoundResource
import kotlinx.coroutines.CoroutineScope

interface IPhotosRepository {
    fun getPhotos(request: ApiRequest.Photos, scope: CoroutineScope): IBoundResource<List<Photo>>
}
