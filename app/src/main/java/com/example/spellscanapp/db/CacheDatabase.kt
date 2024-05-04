package com.example.spellscanapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spellscanapp.db.dao.CacheDAO
import com.example.spellscanapp.db.entity.CacheEntity

@Database(entities = [CacheEntity::class], version = 1)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDAO
}