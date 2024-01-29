package com.example.spellscan.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "cards")
data class CardEntity(
    @PrimaryKey val id: String,
    val name: String,
    val cost: String,
    val type: String,
    @ColumnInfo(name = "card_set") val set: String,
    val lang: String,
    @ColumnInfo(name = "image_url") val imageUrl: String
)