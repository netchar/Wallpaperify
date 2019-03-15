package com.netchar.wallpaperify.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthPrefs

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AppPrefs