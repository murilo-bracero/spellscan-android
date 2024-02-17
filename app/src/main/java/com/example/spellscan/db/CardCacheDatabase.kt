package com.example.spellscan.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spellscan.db.dao.CardDAO
import com.example.spellscan.db.entity.CardEntity

@Database(entities = [CardEntity::class], version = 3)
abstract class CardCacheDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDAO
}

