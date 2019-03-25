package com.netchar.wallpaperify.data.repositories

import android.arch.lifecycle.LiveData
import com.netchar.wallpaperify.data.models.Resource
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

    override fun getPhotosAsync(page: Int, perPage: Int, orderBy: String, scope: CoroutineScope): LiveData<Resource<List<Photo>>> {
        return object : BoundResource<List<Photo>>(dispatchers) {

            override suspend fun apiRequestAsync() = api.getPhotosAsync(page, perPage, orderBy)

            override fun saveRemoteDataInStorage(data: List<Photo>?) {
                /*todo: saving*/
            }

            override fun shouldRefresh(localData: List<Photo>?) = localData.isNullOrEmpty()
        }.launchIn(scope)
    }
}



