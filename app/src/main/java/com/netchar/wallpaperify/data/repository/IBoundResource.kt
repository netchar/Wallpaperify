package com.netchar.wallpaperify.data.repository

import androidx.lifecycle.LiveData
import com.netchar.remote.Resource

interface IBoundResource<TResult : Any> {
    fun cancelJob()
    fun getLiveData(): LiveData<Resource<TResult>>
}