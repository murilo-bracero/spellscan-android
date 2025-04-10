package com.example.spellscanapp

import com.example.spellscanapp.model.CardReference
import com.spellscan.cardservice.CardResponse

fun buildCardResponse(
    id: String = "bcd65dec-ec09-4fc2-833a-383c67454c44",
    name: String = "Mestre da Caça de Tovolar",
    type: String = "Criatura - Humano Lobisomem",
    set: String = "MID",
    cost: String = "{4}{G}{G}",
    imageUrl: String = "imageurl",
    lang: String = "pt"
): CardResponse = CardResponse.newBuilder()
    .setId(id)
    .setName(name)
    .setType(type)
    .setSet(set)
    .setManaCost(cost)
    .setImageUrl(imageUrl)
    .setLang(lang)
    .build()

fun buildCard(
    name: String = "Mestre da Caça de Tovolar",
    type: String = "Criatura - Humano Lobisomem",
    set: String = "MID",
): CardReference = CardReference(
    name,
    type,
    set
)