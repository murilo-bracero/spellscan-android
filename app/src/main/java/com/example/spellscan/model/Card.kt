package com.example.spellscan.model

import java.io.Serializable

data class Card(
    val name: String,
    val type: String,
    val set: String,
    val representation: String
) : Serializable

fun Card.toCardRow(): CardRow {
    return CardRow(name, type, set, isChecked = false)
}