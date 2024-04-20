package com.example.spellscanapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spellscanapp.db.entity.CardEntity
import com.example.spellscanapp.db.entity.CardFaceEntity

@Dao
interface CardDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun save(card: CardEntity)

    @Query("SELECT * FROM cards WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): CardEntity

    @Query("SELECT * FROM cards " +
            "LEFT JOIN card_faces ON cards.id = card_faces.card_id")
    suspend fun findAll(): Map<CardEntity, List<CardFaceEntity>>

    @Query("SELECT * FROM cards WHERE name = :name AND type = :type AND card_set = :set LIMIT 1")
    suspend fun findByNameAndTypeAndSet(name: String, type: String, set: String): CardEntity?

    @Delete
    suspend fun delete(card: CardEntity)
}