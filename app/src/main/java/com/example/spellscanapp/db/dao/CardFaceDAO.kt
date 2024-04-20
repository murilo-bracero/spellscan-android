package com.example.spellscanapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.spellscanapp.db.entity.CardFaceEntity

@Dao
interface CardFaceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(card: CardFaceEntity)

    @Query("SELECT * FROM card_faces WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): CardFaceEntity

    @Delete
    suspend fun delete(card: CardFaceEntity)
}