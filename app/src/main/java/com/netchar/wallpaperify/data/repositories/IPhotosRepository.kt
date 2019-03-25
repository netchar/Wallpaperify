package com.netchar.wallpaperify.data.repositories

import android.arch.lifecycle.LiveData
import com.netchar.wallpaperify.data.models.Resource
import com.netchar.wallpaperify.data.remote.dto.Photo
import kotlinx.coroutines.CoroutineScope

interface IPhotosRepository {
    fun getPhotosAsync(page: Int, perPage: Int, orderBy: String, scope: CoroutineScope): LiveData<Resource<List<Photo>>>
}
