package com.example.spellscan.repository

import com.example.spellscan.model.Card
import java.util.UUID

class LocalCardRepository private constructor() {
    private val db = HashMap<UUID, Card>()

    fun save(card: Card) {
        if (card.localId == null) {
            card.localId = UUID.randomUUID()
        }

        db[card.localId!!] = card
    }

    fun findById(id: UUID): Card? {
        return db[id]
    }

    fun findAll(): ArrayList<Card> {
        return db.values.toCollection(ArrayList())
    }

    fun deleteById(id: UUID) {
        db.remove(id)
    }

    companion object {

        @Volatile
        private var instance: LocalCardRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: LocalCardRepository().also { instance = it }
        }
    }
}
