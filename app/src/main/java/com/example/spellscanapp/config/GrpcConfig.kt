package com.example.spellscanapp.config

import com.example.spellscanapp.BuildConfig.BACKEND_HOST
import com.spellscan.cardservice.CardServiceGrpc
import com.spellscan.inventoryservice.InventoryServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.okhttp.OkHttpChannelBuilder

class GrpcConfig {

    private var channel: ManagedChannel? = null

    fun getCardServiceGrpcStub(): CardServiceGrpc.CardServiceFutureStub {
        startChannel()
        return CardServiceGrpc.newFutureStub(channel)
    }

    fun getInventoryServiceGrpcStub(): InventoryServiceGrpc.InventoryServiceBlockingStub {
        startChannel()
        return InventoryServiceGrpc.newBlockingStub(channel)
    }

    private fun startChannel() {
        if(channel != null) {
            return
        }

        channel = OkHttpChannelBuilder
            .forAddress("10.0.2.2", 9000)
            //.useTransportSecurity()
            .usePlaintext()
            .build()
    }

    companion object {
        @Volatile
        private var instance: GrpcConfig? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: GrpcConfig().also { instance = it }
        }
    }
}