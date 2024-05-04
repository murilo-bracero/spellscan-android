package com.example.spellscanapp.repository

import android.content.Context
import androidx.room.Room
import com.example.spellscanapp.db.CacheDatabase
import com.example.spellscanapp.db.entity.CacheEntity

class CacheRepository private constructor(context: Context) {

    private val db: CacheDatabase

    init {
        db = Room.databaseBuilder(
            context,
            CacheDatabase::class.java,
            "spellscan-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    suspend fun save(entity: CacheEntity) {
        entity.validUntil = System.currentTimeMillis() + 1000 * 60 * 60
        db.cacheDao().save(entity)
    }

    suspend fun findByHash(hash: String): CacheEntity? {
        val cached = db.cacheDao().findByHash(hash)

        if(cached != null && cached.validUntil < System.currentTimeMillis()) {
            db.cacheDao().deleteByHash(hash)
            return null
        }

        return cached
    }

    suspend fun deleteByHash(hash: String) {
        db.cacheDao().deleteByHash(hash)
    }

    companion object {

        @Volatile
        private var instance: CacheRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: CacheRepository(context).also { instance = it }
        }
    }
}