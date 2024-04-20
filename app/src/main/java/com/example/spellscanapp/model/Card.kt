package com.example.spellscanapp.model

import com.example.spellscanapp.db.entity.CardEntity
import com.example.spellscanapp.db.entity.CardFaceEntity

class Card(
    private val id: String,
    private val manaCost: String,
    private val lang: String,
    private val imageUrl: String,
    private val artImageUrl: String,
    private val releasedAt: String,
    private val printedText: String,
    private val cardFaces: List<CardFace> = listOf(),
    private val colors: List<String> = listOf(),
    private val colorIdentity: List<String> = listOf(),
    private val keywords: List<String> = listOf(),
    name: String,
    type: String,
    set: String
) : CardBase(name, type, set)

data class CardFace(
    private val name: String,
    private val manaCost: String,
    private val typeLine: String,
    private val printedText: String,
    private val flavorText: String,
    private val cardImage: String,
    private val artImage: String,
    private val colors: List<String> = listOf(),
    private val colorIndicator: List<String> = listOf()
)

fun buildCard(entity: CardEntity): Card {
    return Card(
        entity.id,
        entity.cost,
        entity.lang,
        entity.imageUrl,
        entity.artImageUrl,
        entity.releasedAt,
        entity.printedText,
        listOf(),
        parseStringArray(entity.colors),
        parseStringArray(entity.colorIdentity),
        parseStringArray(entity.keywords),
        entity.name,
        entity.type,
        entity.set
    )
}

fun buildCard(cardEntity: CardEntity, cardFaces: List<CardFaceEntity>): Card {
    return Card(
        cardEntity.id,
        cardEntity.cost,
        cardEntity.lang,
        cardEntity.imageUrl,
        cardEntity.artImageUrl,
        cardEntity.releasedAt,
        cardEntity.printedText,
        buildCardFaces(cardFaces).toList(),
        parseStringArray(cardEntity.colors),
        parseStringArray(cardEntity.colorIdentity),
        parseStringArray(cardEntity.keywords),
        cardEntity.name,
        cardEntity.type,
        cardEntity.set
    )
}

fun buildCardFaces(entity: List<CardFaceEntity>): List<CardFace> =
    entity.map { buildCardFace(it) }

fun buildCardFace(entity: CardFaceEntity): CardFace =
    CardFace(
        entity.name,
        entity.manaCost,
        entity.typeLine,
        entity.printedText,
        entity.flavorText,
        entity.cardImage,
        entity.artImage,
        parseStringArray(entity.colors),
        parseStringArray(entity.colorIndicators)
    )

private fun parseStringArray(raw: String?): List<String> =
    raw?.split(",") ?: listOf()
