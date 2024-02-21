package com.example.spellscan.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_faces")
data class CardFaceEntity(
    @PrimaryKey var id: String,
    var name: String,
    @ColumnInfo(name = "mana_cost") var manaCost: String,
    @ColumnInfo(name = "type_line") var typeLine: String,
    @ColumnInfo(name = "printed_text") var printedText: String,
    @ColumnInfo(name = "flavor_text") var flavorText: String,
    @ColumnInfo(name = "image_url") var cardImage: String,
    @ColumnInfo(name = "art_image_url") var artImage: String,
    var colors: String,
    @ColumnInfo(name = "color_indicators") var colorIndicators: String
)