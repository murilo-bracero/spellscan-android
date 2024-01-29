package com.example.spellscan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.spellscan.db.entity.CardEntity
import com.example.spellscan.model.CardRow
import com.example.spellscan.repository.CardCacheRepository
import com.example.spellscan.service.CardService
import com.spellscan.proto.CardResponse

class CardServiceViewModel(application: Application) : AndroidViewModel(application) {

    private var cardService: CardService = CardService.newInstance()
    private var cardCacheRepository =
        CardCacheRepository.getInstance(application.applicationContext)

    suspend fun search(cardRow: CardRow): CardEntity {
        val found =
            cardCacheRepository.findByNameAndTypeAndSet(cardRow.name, cardRow.type, cardRow.set)

        if (found != null) {
            return found
        }

        return cardService.find(cardRow.toCard()).also {
            cardCacheRepository.save(it)
        }.let {
            CardEntity(it.id, it.name, it.cost, it.type, it.set, it.lang, it.imageUrl)
        }
    }

    suspend fun findAll(): List<CardEntity> {
        return cardCacheRepository.findAll()
    }
}