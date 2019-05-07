package com.netchar.models.apirequest

import androidx.annotation.StringDef

/**
 * Created by Netchar on 07.05.2019.
 * e.glushankov@gmail.com
 */
const val LATEST = "latest"
const val OLDEST = "oldest"
const val POPULAR = "popular"


@StringDef(LATEST, OLDEST, POPULAR)
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class OrderBy

inline class Ordering(@OrderBy val order: String)