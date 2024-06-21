package com.example.spellscanapp.model

import com.spellscan.cardservice.CardFaceResponse
import com.spellscan.cardservice.CardResponse

// TODO: implement fields releasedAt, colors and colorIdentity
class Card(
    val id: String,
    val manaCost: String,
    val lang: String,
    val imageUrl: String,
    val artImageUrl: String,
    val printedText: String,
    val cardFaces: List<CardFace> = listOf(),
    name: String,
    type: String,
    set: String
) : CardBase(name, type, set)

data class CardFace(
    val name: String,
    val manaCost: String,
    val typeLine: String,
    val printedText: String,
    val flavorText: String,
    val cardImage: String,
    val artImage: String,
    val colors: List<String> = listOf(),
    val colorIndicator: List<String> = listOf()
)

fun buildCard(response: CardResponse): Card =
    Card(
        response.id,
        response.manaCost,
        response.lang,
        response.imageUrl,
        response.artImageUrl,
        response.printedText,
        buildCardFaces(response.cardFacesList),
        response.name,
        response.type,
        response.set
    )

fun buildCardFaces(entity: List<CardFaceResponse>): List<CardFace> =
    entity.map { buildCardFace(it) }

fun buildCardFace(response: CardFaceResponse): CardFace =
    CardFace(
        response.name,
        response.manaCost,
        response.typeLine,
        response.printedText,
        response.flavorText,
        response.cardImage,
        response.artImage,
        response.colorsList,
        response.colorIndicatorList
    )
