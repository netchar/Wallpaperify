package com.netchar.wallpaperify.data.repository

import android.content.Context
import androidx.annotation.StringDef
import com.netchar.common.utils.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * Created by Netchar on 21.03.2019.
 * e.glushankov@gmail.com
 */

class PhotosRepository @Inject constructor(
        private val api: com.netchar.remote.api.PhotosApi,
        private val dispatchers: CoroutineDispatchers,
        private val context: Context
) : IPhotosRepository {

    data class PhotosApiRequest(val page: Int = 0, val perPage: Int = 30, @OrderBy val orderBy: String = LATEST) {
        companion object {
            const val LATEST = "latest"
            const val OLDEST = "oldest"
            const val POPULAR = "popular"
        }

        @StringDef(LATEST, OLDEST, POPULAR)
        @Target(AnnotationTarget.VALUE_PARAMETER)
        @Retention(AnnotationRetention.SOURCE)
        annotation class OrderBy
    }

    override fun getPhotos(request: PhotosApiRequest, scope: CoroutineScope): IBoundResource<List<com.netchar.models.Photo>> {
        return object : BoundResource<List<com.netchar.models.Photo>>(dispatchers, context) {
            override fun getStorageData(): List<com.netchar.models.Photo>? = emptyList()

            override fun apiRequestAsync() = api.getPhotosAsync(request.page, request.perPage, request.orderBy)

            override fun saveRemoteDataInStorage(data: List<com.netchar.models.Photo>) {
                /*todo: saving*/
            }

            override fun isNeedRefresh(localData: List<com.netchar.models.Photo>) = localData.isNullOrEmpty()
        }.launchIn(scope)
    }
}



