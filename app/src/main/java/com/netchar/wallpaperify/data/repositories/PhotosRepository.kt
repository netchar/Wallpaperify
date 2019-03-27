package com.netchar.wallpaperify.data.repositories

import com.netchar.wallpaperify.data.remote.api.PhotosApi
import com.netchar.wallpaperify.data.remote.dto.Photo
import com.netchar.wallpaperify.infrastructure.CoroutineDispatchers
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Created by Netchar on 21.03.2019.
 * e.glushankov@gmail.com
 */

class PhotosRepository @Inject constructor(
        private val api: PhotosApi,
        private val dispatchers: CoroutineDispatchers
) : IPhotosRepository {

    override fun getPhotos(page: Int, perPage: Int, orderBy: String, scope: CoroutineScope): IBoundResource<List<Photo>> {
        return object : BoundResource<List<Photo>>(dispatchers) {
            override fun getStorageData(): List<Photo>? = emptyList()

            override suspend fun apiRequestAsync() = api.getPhotosAsync(page, perPage, orderBy)

            override fun saveRemoteDataInStorage(data: List<Photo>?) {
                /*todo: saving*/
            }

            override fun isNeedRefresh(localData: List<Photo>) = localData.isNullOrEmpty()
        }.launchIn(scope)
    }
}



