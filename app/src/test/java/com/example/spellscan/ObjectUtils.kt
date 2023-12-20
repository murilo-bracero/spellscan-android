package com.example.spellscan

import com.example.spellscan.model.Card
import com.spellscan.proto.CardResponse

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
    .setCost(cost)
    .setImageUrl(imageUrl)
    .setLang(lang)
    .build()

fun buildCard(
    name: String = "Mestre da Caça de Tovolar",
    type: String = "Criatura - Humano Lobisomem",
    set: String = "MID",
): Card = Card(
    name,
    type,
    set,
    "[$name, $type, $set]"
)