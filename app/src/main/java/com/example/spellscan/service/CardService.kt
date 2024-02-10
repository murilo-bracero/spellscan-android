package com.example.spellscan.service

import com.example.spellscan.config.GrpcConfig
import com.example.spellscan.model.Card
import com.spellscan.cardservice.CardRequest
import com.spellscan.cardservice.CardResponse
import com.spellscan.cardservice.CardServiceGrpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardService(
    private var stub: CardServiceGrpc.CardServiceFutureStub
) {

    suspend fun find(card: Card): CardResponse {
        val request = CardRequest.newBuilder()
            .setName(card.name)
            .setType(card.type)
            .setSet(card.set)
            .build()

        return withContext(Dispatchers.IO) {
            stub.find(request).get()
        }
    }

    suspend fun findAll(cards: List<Card>): List<CardResponse> {
        if (cards.isEmpty()) {
            return emptyList()
        }

        if (cards.size == 1) {
            return listOf(find(cards[0]))
        }

        // create stub for bulk finding

        return emptyList()
    }

    companion object {
        fun newInstance(): CardService {
            val grpcConfig = GrpcConfig.getInstance()
            val stub = grpcConfig.getCardServiceGrpcStub()
            return CardService(stub)
        }
    }

}