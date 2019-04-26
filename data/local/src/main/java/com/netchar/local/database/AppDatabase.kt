package com.netchar.local.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.netchar.local.dao.PhotoDao
import com.netchar.models.Photo
import com.netchar.models.converters.CollectionTypeConverter

@Database(entities = [Photo::class], version = 1, exportSchema = false)
@TypeConverters(CollectionTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}