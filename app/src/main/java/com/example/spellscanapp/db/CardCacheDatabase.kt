package com.example.spellscanapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.spellscanapp.db.dao.CardDAO
import com.example.spellscanapp.db.dao.CardFaceDAO
import com.example.spellscanapp.db.entity.CardEntity
import com.example.spellscanapp.db.entity.CardFaceEntity

@Database(entities = [CardEntity::class, CardFaceEntity::class], version = 1)
abstract class CardCacheDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDAO
    abstract fun cardFaceDao(): CardFaceDAO
}

