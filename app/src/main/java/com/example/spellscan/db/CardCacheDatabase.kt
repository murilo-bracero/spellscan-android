package com.example.spellscan.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spellscan.db.dao.CardDAO
import com.example.spellscan.db.dao.CardFaceDAO
import com.example.spellscan.db.entity.CardEntity
import com.example.spellscan.db.entity.CardFaceEntity

@Database(entities = [CardEntity::class, CardFaceEntity::class], version = 1)
abstract class CardCacheDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDAO
    abstract fun cardFaceDao(): CardFaceDAO
}

