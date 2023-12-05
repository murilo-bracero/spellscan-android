package com.example.spellscan.repository

import com.example.spellscan.model.Card

class LocalCardRepository private constructor() {
    private val db = ArrayList<Card>()

    fun save(card: Card) {
        db.add(card)
    }

    fun get(id: Int): Card {
        return db[id]
    }

    fun getAll(): ArrayList<Card> {
        return db
    }

    fun removeByProps(name: String, type: String, set: String) {
        db.removeIf {
            it.name == name && it.type == type && it.set == set
        }
    }

    companion object {

        @Volatile
        private var instance: LocalCardRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: LocalCardRepository().also { instance = it }
        }
    }
}
