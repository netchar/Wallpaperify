package com.netchar.wallpaperify.ui.about

import com.netchar.common.base.BaseViewModel
import com.netchar.common.utils.CoroutineDispatchers
import com.netchar.common.utils.IBuildPreferences
import java.net.URL
import javax.inject.Inject

class ExternalLibraryProvider {
    data class Library(val name: String, val description: String, val link: URL)

    private val libraries = mutableListOf<Library>()

    init {
        libraries.add(Library("Timber", "This is a logger with a small, extensible API which provides utility on top of Android's normal Log class.", URL("https://github.com/JakeWharton/timber#license")))
        libraries.add(Library("Dagger2", "A fast dependency injector for Android and Java.", URL("https://github.com/google/dagger#license")))
        libraries.add(Library("Retrofit", "A type-safe HTTP client for Android and Java.", URL("https://square.github.io/retrofit/")))
        libraries.add(Library("ThreetenaBP", "An adaptation of the JSR-310 backport for Android.", URL("https://github.com/JakeWharton/ThreeTenABP#license")))
        libraries.add(Library("Moshi", "A modern JSON library for Kotlin and Java.", URL("https://github.com/square/moshi#license")))
        libraries.add(Library("Kotshi", "An annotations processor that generates Moshi adapters from immutable Kotlin data classes.", URL("https://github.com/ansman/kotshi#license")))
        libraries.add(Library("Shimmer", "An easy, flexible way to add a shimmering effect to any view in an Android app.", URL("https://github.com/facebook/shimmer-android#license")))
        libraries.add(Library("TransitionsEverywhere", "Set of extra Transitions on top of AndroidX Transitions Library.", URL("https://github.com/andkulikov/Transitions-Everywhere/blob/master/LICENSE")))
        libraries.add(Library("QuickPermissions", "The most easiest way to handle Android Runtime Permissions in Kotlin.", URL("https://github.com/QuickPermissions/QuickPermissions-Kotlin/blob/master/LICENSE")))
        libraries.add(Library("PhotoView", "Implementation of ImageView for Android that supports zooming, by various touch gestures.", URL("https://github.com/chrisbanes/PhotoView#license")))
    }

    fun getLibraries(): List<Library> {
        return libraries
    }
}

class AboutViewModel @Inject constructor(
        coroutineDispatchers: CoroutineDispatchers,
        private val buildPreferences: IBuildPreferences
) : BaseViewModel(coroutineDispatchers) {

    companion object {
        const val APP_INFO = "Wallpaperify is powered by unsplash.com. \n\nBrowse over 1 million free high-resolution images brought to you by the most generous community of photographers."
    }

    fun getVersionName() = buildPreferences.getVersionName()

    fun getAppInfoText() = APP_INFO

}