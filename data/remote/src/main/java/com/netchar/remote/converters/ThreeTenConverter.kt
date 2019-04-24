package com.netchar.remote.converters

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter.ISO_INSTANT


/**
 * Created by Netchar on 15.03.2019.
 * e.glushankov@gmail.com
 */
class ThreeTenConverter {
    @ToJson
    fun toJson(time: Instant): String = ISO_INSTANT.format(time)

    @FromJson
    fun fromJson(time: String): Instant = ISO_INSTANT.parse(time, Instant.FROM).atZone(ZoneId.systemDefault()).toInstant()
}