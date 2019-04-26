package com.netchar.local.dao

import androidx.room.*

interface IBaseDao<TItem> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg item: TItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<TItem>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(item: TItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(items: List<TItem>)

    @Delete
    fun delete(item: TItem)
}