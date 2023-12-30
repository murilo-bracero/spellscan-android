package com.example.spellscan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spellscan.model.CardRow
import com.example.spellscan.service.CardService
import com.spellscan.proto.CardResponse
import kotlinx.coroutines.launch

class CardSearchViewModel: ViewModel() {

    private var cardService: CardService = CardService.newInstance()

    suspend fun search(cardRow: CardRow): CardResponse {
        return cardService.find(cardRow.toCard())
    }

}