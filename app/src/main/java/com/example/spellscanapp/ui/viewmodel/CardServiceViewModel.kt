package com.example.spellscanapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.spellscanapp.db.entity.CacheEntity
import com.example.spellscanapp.model.CacheCallEnum.GET_CARD_BY_ID
import com.example.spellscanapp.model.CacheCallEnum.GET_CARD_FIND
import com.example.spellscanapp.model.Card
import com.example.spellscanapp.model.CardReference
import com.example.spellscanapp.model.buildCard
import com.example.spellscanapp.repository.CacheRepository
import com.example.spellscanapp.service.CardService
import com.example.spellscanapp.util.buildCacheHash
import com.google.gson.Gson

class CardServiceViewModel(application: Application) : AndroidViewModel(application) {

    private var cardService: CardService = CardService.newInstance()
    private val cacheRepository = CacheRepository.getInstance(application.applicationContext)

    suspend fun search(card: CardReference): Card {
        val hashId = buildCacheHash(GET_CARD_FIND, card.name, card.type, card.set)
        val found = cacheRepository.findByHash(hashId)

        if (found != null) {
            return Gson().fromJson(found.responsePayload, Card::class.java)
        }

        return buildCard(cardService.find(card))
            .also { cacheRepository.save(CacheEntity(hashId, Gson().toJson(it))) }
    }

    suspend fun findById(id: String): Card? {
        val hashId = buildCacheHash(GET_CARD_BY_ID, id)
        val found = cacheRepository.findByHash(hashId)
        if (found != null) {
            return Gson().fromJson(found.responsePayload, Card::class.java)
        }

        return buildCard(cardService.findById(id))
            .also {
                cacheRepository.save(CacheEntity(hashId, Gson().toJson(it)))
            }
    }

    suspend fun delete(card: Card) {
        Log.d("CardServiceViewModel", "delete - Needs new implementation")
    }
}