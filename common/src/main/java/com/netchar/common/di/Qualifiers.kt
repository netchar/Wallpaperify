package com.netchar.common.di

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthPrefs

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AppPrefs

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class OAuthInterceptor