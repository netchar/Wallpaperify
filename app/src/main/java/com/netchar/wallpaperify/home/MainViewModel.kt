package com.netchar.wallpaperify.home

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.IntDef
import android.support.annotation.StringDef
import com.netchar.wallpaperify.data.api.PhotosApi
import com.netchar.wallpaperify.data.models.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class MainViewModel @Inject constructor(private val photosApi: PhotosApi) : ViewModel() {

    private val photos = MutableLiveData<List<Photo>>()

    fun getPhotos(): LiveData<List<Photo>> {
        if (photos.value == null) {
            refreshPhotos(PhotosApi.LATEST)
        }
        return photos
    }

    private fun refreshPhotos(order: String) {
        photosApi.getPhotos(1, 30, order).enqueue(object : Callback<List<Photo>> {
            override fun onFailure(p0: Call<List<Photo>>, p1: Throwable) {

            }

            override fun onResponse(p0: Call<List<Photo>>, p1: Response<List<Photo>>) {
                photos.value = p1.body()
            }
        })
    }
}
