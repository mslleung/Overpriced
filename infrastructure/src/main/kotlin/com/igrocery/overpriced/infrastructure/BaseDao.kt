package com.igrocery.overpriced.infrastructure

import androidx.room.*

@Dao
internal interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(items: T): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg items: T): List<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(items: List<T>): List<Long>

    @Update
    suspend fun update(vararg items: T): Int

    @Update
    suspend fun update(items: List<T>): Int

    @Delete
    suspend fun delete(vararg items: T): Int

    @Delete
    suspend fun delete(items: List<T>): Int

}
