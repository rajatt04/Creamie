package com.rajatt7z.creamie.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rajatt7z.creamie.data.local.entity.CollectionEntity

@Dao
interface CollectionDao {





    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(collections: List<CollectionEntity>)

    @Query("DELETE FROM collections")
    suspend fun clearAll()
}
