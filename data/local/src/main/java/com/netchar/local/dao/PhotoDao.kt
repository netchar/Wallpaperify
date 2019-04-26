package com.netchar.local.dao

import androidx.room.*
import com.netchar.models.Photo

@Dao
interface PhotoDao : IBaseDao<Photo> {

    @Query("SELECT * FROM photo")
    fun getAll(): List<Photo>

    @Query("SELECT * FROM photo WHERE id= :id")
    fun getById(id: String): Photo

    @Query("DELETE FROM photo")
    fun deleteAll()
}