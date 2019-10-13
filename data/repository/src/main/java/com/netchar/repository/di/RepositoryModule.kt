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

package com.netchar.repository.di

import android.content.Context
import android.content.SharedPreferences
import com.netchar.common.di.AppPrefs
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.IBuild
import com.netchar.remote.api.CollectionsApi
import com.netchar.remote.api.PhotosApi
import com.netchar.repository.collection.CollectionRepository
import com.netchar.repository.collection.ICollectionRepository
import com.netchar.repository.photos.IPhotosRepository
import com.netchar.repository.photos.PhotosRepository
import com.netchar.repository.preferences.IPreferenceRepository
import com.netchar.repository.preferences.PreferenceRepository
import com.netchar.repository.services.DownloadService
import com.netchar.repository.services.IDownloadService
import com.netchar.repository.usecase.IPhotoUseCase
import com.netchar.repository.usecase.PhotoUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepositoryModule {

    @JvmStatic
    @Provides
    @Singleton
    fun photosRepo(photosApi: PhotosApi, dispatchers: CoroutineDispatchers, downloadService: IDownloadService): IPhotosRepository = PhotosRepository(dispatchers, photosApi, downloadService)

    @JvmStatic
    @Provides
    @Singleton
    fun collectionsRepo(collectionsApi: CollectionsApi, dispatchers: CoroutineDispatchers): ICollectionRepository = CollectionRepository(collectionsApi, dispatchers)

    @JvmStatic
    @Provides
    @Singleton
    fun provideDownloadService(context: Context, build: IBuild): IDownloadService = DownloadService(context, build)

    @JvmStatic
    @Provides
    @Singleton
    fun providePreferenceRepo(@AppPrefs prefs: SharedPreferences, context: Context): IPreferenceRepository = PreferenceRepository(prefs, context)

    @JvmStatic
    @Provides
    @Singleton
    fun providePhotoUseCase(repo: IPhotosRepository): IPhotoUseCase = PhotoUseCase(repo)
}