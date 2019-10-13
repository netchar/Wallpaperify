package com.netchar.common.utils

import android.net.Uri
import java.io.File

interface IBuildConfig {
    fun getApiAccessKey(): String

    fun getApiSecretKey(): String

    fun getVersionCode(): Long

    fun getVersionName(): String

    fun getApplicationId(): String

    fun getFileProviderUri(file: File): Uri
}