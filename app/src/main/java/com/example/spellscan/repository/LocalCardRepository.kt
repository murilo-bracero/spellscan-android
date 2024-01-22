package com.example.spellscan.repository

import com.example.spellscan.model.Card
import java.util.UUID

class LocalCardRepository : LocalRepository<UUID, Card>() {

    override fun save(card: Card) {
        if (card.localId == null) {
            card.localId = UUID.randomUUID()
        }

        db[card.localId!!] = card
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
