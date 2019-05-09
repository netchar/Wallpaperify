package com.netchar.common.utils

import android.content.res.Resources
import java.io.File


/**
 * Created by Netchar on 08.05.2019.
 * e.glushankov@gmail.com
 */

fun Any.getJsonFromResourceFile(fileName: String): String {
    val uri = this.javaClass.classLoader?.getResource(fileName)
    uri?.let {
        val file = File(uri.path)
        return String(file.readBytes())
    } ?: throw Resources.NotFoundException("File not found")
}

