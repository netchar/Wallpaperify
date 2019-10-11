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

import com.netchar.common.services.IExternalLibraryProvider.Library

internal class ExternalLibraryProvider : IExternalLibraryProvider {
    private val libraries = mutableListOf<Library>()

    init {
        libraries.add(Library("Timber", "This is a logger with a small, extensible API which provides utility on top of Android's normal Log class.", "https://github.com/JakeWharton/timber#license"))
        libraries.add(Library("Dagger2", "A fast dependency injector for Android and Java.", "https://github.com/google/dagger#license"))
        libraries.add(Library("Retrofit", "A type-safe HTTP client for Android and Java.", "https://square.github.io/retrofit/"))
        libraries.add(Library("ThreetenaBP", "An adaptation of the JSR-310 backport for Android.", "https://github.com/JakeWharton/ThreeTenABP#license"))
        libraries.add(Library("Moshi", "A modern JSON library for Kotlin and Java.", "https://github.com/square/moshi#license"))
        libraries.add(Library("Kotshi", "An annotations processor that generates Moshi adapters from immutable Kotlin data classes.", "https://github.com/ansman/kotshi#license"))
        libraries.add(Library("Shimmer", "An easy, flexible way to add a shimmering effect to any view in an Android app.", "https://github.com/facebook/shimmer-android#license"))
        libraries.add(Library("TransitionsEverywhere", "Set of extra Transitions on top of AndroidX Transitions Library.", "https://github.com/andkulikov/Transitions-Everywhere/blob/master/LICENSE"))
        libraries.add(Library("QuickPermissions", "The most easiest way to handle Android Runtime Permissions in Kotlin.", "https://github.com/QuickPermissions/QuickPermissions-Kotlin/blob/master/LICENSE"))
        libraries.add(Library("PhotoView", "Implementation of ImageView for Android that supports zooming, by various touch gestures.", "https://github.com/chrisbanes/PhotoView#license"))
    }

    override fun getLibraries(): List<Library> {
        return libraries
    }
}