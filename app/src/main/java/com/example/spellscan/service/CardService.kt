package com.example.spellscan.service

import android.content.Context
import com.example.spellscan.config.GrpcChannelConfig
import com.example.spellscan.model.Card
import io.grpc.ManagedChannel

class CardService(
    private var channel: ManagedChannel?
) {

    fun find(card: Card): Boolean {
        // create stub for single finding

        return false
    }

    fun findAll(cards: List<Card>): List<Boolean> {
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
        fun newInstance(context: Context): CardService {
            val channel = GrpcChannelConfig(context).getChannel()
            return CardService(channel)
        }
    }

}