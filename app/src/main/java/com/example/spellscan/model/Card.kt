package com.example.spellscan.model

import java.io.Serializable
import java.util.UUID

data class Card(
    var localId: UUID?,
    val name: String,
    val type: String,
    val set: String,
) : Serializable

fun newCard(name: String, type: String, set: String): Card{
   return Card(null, name, type, set)
}

fun Card.toCardRow(): CardRow {
    return CardRow(localId!!, name, type, set, false)
}