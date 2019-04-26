package com.netchar.local.dao

import androidx.room.*
import com.netchar.models.Photo

@Dao
public interface PhotoDao {

    @Query("SELECT * FROM photo")
    fun getAll(): List<Photo>

    @Query("SELECT * FROM photo WHERE id= :id")
    fun getById(id: String): Photo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(photo: Photo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(photo: List<Photo>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(photo: Photo)

    @Delete
    fun delete(photo: Photo)

    @Query("DELETE FROM photo")
    fun deleteAll()
}