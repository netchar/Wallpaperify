package com.netchar.wallpaperify.data.models

/**
 * Created by Netchar on 17.03.2019.
 * e.glushankov@gmail.com
 */

sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}