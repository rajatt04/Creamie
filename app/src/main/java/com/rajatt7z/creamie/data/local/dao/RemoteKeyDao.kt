package com.rajatt7z.creamie.data.local.dao

import androidx.room.*
import com.rajatt7z.creamie.data.local.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Query("SELECT * FROM remote_keys WHERE queryOrCategory = :queryOrCategory")
    suspend fun getRemoteKey(queryOrCategory: String): RemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKeyEntity)

    @Query("DELETE FROM remote_keys WHERE queryOrCategory = :queryOrCategory")
    suspend fun deleteByQuery(queryOrCategory: String)

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}
