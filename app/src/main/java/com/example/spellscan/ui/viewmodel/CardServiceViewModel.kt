package com.example.spellscan.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscan.model.CardRow
import com.example.spellscan.repository.LocalCardResponseRepository
import com.example.spellscan.service.CardService
import com.spellscan.proto.CardResponse

class CardServiceViewModel: ViewModel() {

    private var cardService: CardService = CardService.newInstance()
    private var localCardResponseRepository = LocalCardResponseRepository.getInstance()

    suspend fun search(cardRow: CardRow): CardResponse {
        return cardService.find(cardRow.toCard())
    }

    fun save(card: CardResponse) {
        localCardResponseRepository.save(card)
    }

    fun findAll(): List<CardResponse> {
        return localCardResponseRepository.findAll()
    }

}