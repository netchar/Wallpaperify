package com.netchar.wallpaperify.data.repositories

import android.arch.lifecycle.LiveData
import com.netchar.wallpaperify.data.models.dto.Photo

interface IPhotosRepository {
    suspend fun getPhotos(): LiveData<Resource<List<Photo>>>
}
