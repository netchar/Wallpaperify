package com.netchar.models.converters

import androidx.room.TypeConverter
import com.netchar.models.CurrentUserCollection
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class CollectionTypeConverter {
    private val moshi: JsonAdapter<List<CurrentUserCollection>> by lazy {
        Moshi.Builder().build().adapter<List<CurrentUserCollection>>(Types.newParameterizedType(List::class.java, CurrentUserCollection::class.java))
    }

    @TypeConverter
    fun collectionToString(collection: List<CurrentUserCollection>?): String? {
        return moshi.toJson(collection)
    }

    @TypeConverter
    fun fromStringToCollection(collection: String?): List<CurrentUserCollection>? {
        return if (collection != null) {
            moshi.fromJson(collection)
        } else {
            null
        }
    }
}