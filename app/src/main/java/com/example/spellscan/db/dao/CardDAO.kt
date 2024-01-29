package com.example.spellscan.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.spellscan.db.entity.CardEntity

@Dao
interface CardDAO {
    @Insert
    suspend fun save(card: CardEntity)

    @Query("SELECT * FROM cards")
    suspend fun findAll(): List<CardEntity>

    @Query("SELECT * FROM cards WHERE name = :name AND type = :type AND card_set = :set LIMIT 1")
    suspend fun findByNameAndTypeAndSet(name: String, type: String, set: String): CardEntity?
}