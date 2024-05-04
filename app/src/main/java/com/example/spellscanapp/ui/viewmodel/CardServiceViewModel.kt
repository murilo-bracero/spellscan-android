package com.example.spellscanapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.spellscanapp.db.entity.CardEntity
import com.example.spellscanapp.model.CardReference
import com.example.spellscanapp.repository.AuthStateRepository
import com.example.spellscanapp.repository.CardCacheRepository
import com.example.spellscanapp.service.CardService

class CardServiceViewModel(application: Application) : AndroidViewModel(application) {

    private var cardService: CardService = CardService.newInstance()
    private var cardCacheRepository =
        CardCacheRepository.getInstance(application.applicationContext)

    suspend fun search(card: CardReference): CardEntity {
        val found =
            cardCacheRepository.findByNameAndTypeAndSet(card.name, card.type, card.set)

        if (found != null) {
            return found
        }

        return cardService.find(card).let {
            cardCacheRepository.save(it)
        }
    }

    suspend fun findById(id: String): CardEntity? {
        return cardCacheRepository.findById(id)
    }

    suspend fun delete(card: CardEntity) {
        cardCacheRepository.delete(card)
    }
}