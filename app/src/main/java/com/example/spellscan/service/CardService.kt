package com.example.spellscan.service

import com.example.spellscan.config.GrpcConfig
import com.example.spellscan.model.Card
import com.spellscan.proto.CardRequest
import com.spellscan.proto.CardResponse
import com.spellscan.proto.CardServiceGrpc.CardServiceFutureStub
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CardService(
    private var stub: CardServiceFutureStub
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
            val stub = GrpcConfig().getCardServiceGrpcStub()
            return CardService(stub)
        }
    }

}