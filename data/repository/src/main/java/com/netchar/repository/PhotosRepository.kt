package com.netchar.repository

import android.content.Context
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.local.dao.PhotoDao
import com.netchar.models.Photo
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * Created by Netchar on 21.03.2019.
 * e.glushankov@gmail.com
 */

class PhotosRepository @Inject constructor(
    private val api: com.netchar.remote.api.PhotosApi,
    private val dao: PhotoDao,
    private val dispatchers: CoroutineDispatchers,
    private val context: Context
) : IPhotosRepository {

    override fun getPhotos(request: IPhotosRepository.PhotosApiRequest, scope: CoroutineScope): IBoundResource<List<Photo>> {
        return object : BoundResource<List<Photo>>(dispatchers, context) {
            override fun getStorageData(): List<Photo>? {
//                return dao.getAll()
                return emptyList()
            }

            override fun apiRequestAsync() = api.getPhotosAsync(request.page, request.perPage, request.orderBy)

            override fun saveRemoteDataInStorage(data: List<Photo>) {
//                dao.insert(data)
            }

            override fun isNeedRefresh(localData: List<Photo>) = localData.isNullOrEmpty() || request.forceFetching
        }.launchIn(scope)
    }
}



