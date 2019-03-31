package com.netchar.wallpaperify.data.repository


/**
 * Created by Netchar on 31.03.2019.
 * e.glushankov@gmail.com
 */
interface IOAuthRepository {
    fun isAuthorized(): Boolean
    fun getUserApiAccessTokenKey(): String
}