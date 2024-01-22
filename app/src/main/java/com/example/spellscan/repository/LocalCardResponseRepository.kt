package com.example.spellscan.repository

import com.spellscan.proto.CardResponse

//TODO: Need a better name
class LocalCardResponseRepository: LocalRepository<String, CardResponse>() {

    override fun save(card: CardResponse) {
        db[card.id] = card
    }

    companion object {

        @Volatile
        private var instance: LocalCardResponseRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: LocalCardResponseRepository().also { instance = it }
        }
    }

}