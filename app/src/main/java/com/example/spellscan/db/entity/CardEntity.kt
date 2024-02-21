package com.example.spellscan.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey var id: String,
    var name: String,
    var cost: String,
    var type: String,
    @ColumnInfo(name = "card_set") var set: String,
    var lang: String,
    @ColumnInfo(name = "image_url") var imageUrl: String,
    @ColumnInfo(name = "art_image_url") var artImageUrl: String,
    @ColumnInfo(name = "released_at") var releasedAt: String,
    @ColumnInfo(name = "colors") var colors: String,
    @ColumnInfo(name = "color_identity") var colorIdentity: String,
    @ColumnInfo(name = "printed_text") var printedText: String,
    @ColumnInfo(name = "keywords") var keywords: String,
    @ColumnInfo(name = "has_card_faces") var hasCardFaces: Boolean,
) {
    @Ignore var cardFaces: MutableList<CardFaceEntity> = mutableListOf()
}