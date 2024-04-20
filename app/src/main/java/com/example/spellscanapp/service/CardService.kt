package com.example.spellscanapp.service

import com.example.spellscanapp.config.GrpcConfig
import com.example.spellscanapp.model.CardReference
import com.spellscan.cardservice.CardRequest
import com.spellscan.cardservice.CardResponse
import com.spellscan.cardservice.CardServiceGrpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit.SECONDS

class CardService(
    private var stub: CardServiceGrpc.CardServiceFutureStub
) {

    private val deadlineDuration = 2L

//    private val metadataInterceptor: (String) -> ClientInterceptor = { accessToken ->
//        MetadataUtils.newAttachHeadersInterceptor(Metadata().also {
//            val key = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)
//            it.put(key, "Bearer $accessToken")
//        })
//    }

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

    companion object {
        fun newInstance(): CardService {
            val grpcConfig = GrpcConfig.getInstance()
            val stub = grpcConfig.getCardServiceGrpcStub()
            return CardService(stub)
        }
    }

}