package com.netchar.common.utils

interface IBuild {
    fun getApiAccessKey(): String

    fun getApiSecretKey(): String

    fun getVersionCode(): Long

    fun getVersionName(): String
    fun getApplicationId(): String
}