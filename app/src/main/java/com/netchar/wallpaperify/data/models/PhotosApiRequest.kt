package com.netchar.wallpaperify.data.models

import com.netchar.wallpaperify.data.remote.api.PhotosApi
import com.netchar.wallpaperify.data.remote.api.PhotosApi.Companion.LATEST

/**
 * Created by Netchar on 25.03.2019.
 * e.glushankov@gmail.com
 */

data class PhotosApiRequest(val page: Int = 0, val perPage: Int = 30, @PhotosApi.Companion.OrderBy val orderBy: String = LATEST)