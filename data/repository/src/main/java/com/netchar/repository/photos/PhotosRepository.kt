package com.netchar.repository.photos

import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.models.Photo
import com.netchar.models.apirequest.PhotosRequest
import com.netchar.remote.api.PhotosApi
import com.netchar.repository.IBoundResource
import com.netchar.repository.NetworkBoundResource
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * Created by Netchar on 21.03.2019.
 * e.glushankov@gmail.com
 */

class PhotosRepository @Inject constructor(
    private val api: PhotosApi,
    private val dispatchers: CoroutineDispatchers
) : IPhotosRepository {

    override fun getPhotos(request: PhotosRequest, scope: CoroutineScope): IBoundResource<List<Photo>> {
        return NetworkBoundResource(dispatchers, getApiCall = {
            api.getPhotosAsync(request.page, request.perPage, request.orderBy)
        }).launchIn(scope)
    }
}



