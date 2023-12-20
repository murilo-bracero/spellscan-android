package com.example.spellscan.model

import java.util.UUID

data class CardRow(
    val id: UUID,
    val name: String,
    val type: String,
    val set: String,
    var isChecked: Boolean
)
