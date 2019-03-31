package com.netchar.wallpaperify.data.repository

import android.arch.lifecycle.LiveData
import com.netchar.wallpaperify.data.models.Resource

interface IBoundResource<TResult : Any> {
    fun cancelJob()
    fun getLiveData(): LiveData<Resource<TResult>>
}