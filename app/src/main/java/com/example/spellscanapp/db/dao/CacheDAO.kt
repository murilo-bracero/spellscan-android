package com.example.spellscanapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.spellscanapp.db.entity.CacheEntity

@Dao
interface CacheDAO {
    @Insert
    suspend fun save(cache: CacheEntity)

    @Query("SELECT * FROM backend_cache WHERE hash = :hash LIMIT 1")
    suspend fun findByHash(hash: String): CacheEntity?

    @Query("DELETE FROM backend_cache WHERE hash = :hash")
    suspend fun deleteByHash(hash: String)
}