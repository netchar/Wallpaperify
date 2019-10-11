/*
 * Copyright © 2019 Eugene Glushankov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netchar.common.services

import android.content.Context
import android.content.res.Resources
import com.bumptech.glide.Glide
import com.netchar.common.extensions.asMb
import com.netchar.common.extensions.directorySize
import com.netchar.common.utils.CoroutineDispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

internal class PhotoCacheService @Inject constructor(
        private val context: Context,
        private val coroutineDispatchers: CoroutineDispatchers
) : IPhotoCacheService {

    override fun getCacheDirectory(): File? {
        return Glide.getPhotoCacheDir(context)
    }

    override suspend fun getCacheSizeMbAsync(): Long = withContext(coroutineDispatchers.disk) {
        var size: Long = 0

        try {
            size = getCacheDirectory()?.directorySize()?.asMb() ?: 0
        } catch (ex: Resources.NotFoundException) {
            Timber.e(ex)
        }
        size
    }

    override suspend fun clearDiskCacheAsync() = withContext(coroutineDispatchers.disk) {
        Glide.get(context).clearDiskCache()
    }

    override fun clearMemory() {
        Glide.get(context).clearMemory()
    }
}