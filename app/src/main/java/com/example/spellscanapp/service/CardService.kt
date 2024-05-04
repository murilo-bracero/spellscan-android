package com.example.spellscanapp.service

import com.example.spellscanapp.config.GrpcConfig
import com.example.spellscanapp.model.CardReference
import com.spellscan.cardservice.CardRequest
import com.spellscan.cardservice.CardResponse
import com.spellscan.cardservice.CardServiceGrpc
import com.spellscan.cardservice.FindByIdRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit.SECONDS

class CardService(
    private var stub: CardServiceGrpc.CardServiceFutureStub
) {

    private val deadlineDuration = 2L

    suspend fun find(card: CardReference): CardResponse {
        val request = CardRequest.newBuilder()
            .setName(card.name)
            .setType(card.type)
            .setSet(card.set)
            .build()

        return withContext(Dispatchers.IO) {
            stub.withDeadlineAfter(deadlineDuration, SECONDS)
                .find(request)
                .get()
        }
    }

    suspend fun findById(id: String): CardResponse {
        val request = FindByIdRequest.newBuilder()
            .setId(id)
            .build()

        return withContext(Dispatchers.IO) {
            stub.withDeadlineAfter(deadlineDuration, SECONDS)
                .findById(request)
                .get()
        }
    }

    companion object {
        fun newInstance(): CardService {
            val grpcConfig = GrpcConfig.getInstance()
            val stub = grpcConfig.getCardServiceGrpcStub()
            return CardService(stub)
        }
    }
}
