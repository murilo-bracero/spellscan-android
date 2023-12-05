package com.example.spellscan.model

data class CardRow(
    val name: String,
    val type: String,
    val set: String,
    var isChecked: Boolean
)