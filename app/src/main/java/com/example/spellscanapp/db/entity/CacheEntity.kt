package com.example.spellscanapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "backend_cache")
data class CacheEntity(
    @PrimaryKey val hash: String,
    @ColumnInfo(name = "response_payload") val responsePayload: String,
    @ColumnInfo(name = "valid_until") var validUntil: Long = 0
)