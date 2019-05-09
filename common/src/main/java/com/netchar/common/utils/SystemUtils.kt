package com.netchar.common.utils

import android.content.res.Resources
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.util.*


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

fun formatDate(stingDate: String, pattern: String): String {
    val localDate = LocalDate.parse(stingDate, DateTimeFormatter.ISO_DATE_TIME)
    val formatter = DateTimeFormatter.ofPattern(pattern, Locale.US)
    return localDate.format(formatter)
}
