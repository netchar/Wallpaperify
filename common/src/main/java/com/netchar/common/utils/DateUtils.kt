package com.netchar.common.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import timber.log.Timber
import java.util.*


/**
 * Created by Netchar on 09.05.2019.
 * e.glushankov@gmail.com
 */

fun formatDate(stingDate: String?, pattern: String): String {
    if (stingDate.isNullOrEmpty())
        return ""

    return try {
        val localDate = LocalDate.parse(stingDate, DateTimeFormatter.ISO_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.US)
        localDate.format(formatter)
    } catch (ex: DateTimeParseException) {
        Timber.e(ex)
        ""
    }
}
