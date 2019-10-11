package com.netchar.common.utils

interface IBuildPreferences {
    fun getApiAccessKey(): String

    fun getApiSecretKey(): String

    fun getVersionCode(): Long

    fun getVersionName(): String
}