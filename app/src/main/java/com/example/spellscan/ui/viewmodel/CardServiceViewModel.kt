package com.example.spellscan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.spellscan.db.entity.CardEntity
import com.example.spellscan.model.Card
import com.example.spellscan.repository.CardCacheRepository
import com.example.spellscan.service.CardService

class CardServiceViewModel(application: Application) : AndroidViewModel(application) {

    private var cardService: CardService = CardService.newInstance()
    private var cardCacheRepository =
        CardCacheRepository.getInstance(application.applicationContext)

    suspend fun search(card: Card): CardEntity {
        val found =
            cardCacheRepository.findByNameAndTypeAndSet(card.name, card.type, card.set)

        if (found != null) {
            return found
        }

        return cardService.find(card).also {
            cardCacheRepository.save(it)
        }.let {
            CardEntity(it.id, it.name, it.manaCost, it.type, it.set, it.lang, it.imageUrl, it.artImageUrl, it.printedText)
        }
    }

    suspend fun findById(id: String): CardEntity {
        return cardCacheRepository.findById(id)
    }

    suspend fun findAll(): List<CardEntity> {
        return cardCacheRepository.findAll()
    }

    suspend fun delete(card: CardEntity) {
        cardCacheRepository.delete(card)
    }
}