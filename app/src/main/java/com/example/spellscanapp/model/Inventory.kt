package com.example.spellscanapp.model

import com.spellscan.inventoryservice.InventoryResponse

data class Inventory(
    val id: String,
    val name: String,
    val cardIds: List<String>,
    val cardIdsCount: Int
)

fun buildInventory(model: InventoryResponse): Inventory =
    Inventory(model.id, model.name, model.cardIdsList, model.cardIdsCount)
