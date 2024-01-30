package com.example.spellscan.config

import com.example.spellscan.BuildConfig.BACKEND_HOST
import com.example.spellscan.BuildConfig.BACKEND_PORT
import com.spellscan.proto.CardServiceGrpc
import com.spellscan.proto.CardServiceGrpc.CardServiceFutureStub
import io.grpc.ManagedChannel
import io.grpc.okhttp.OkHttpChannelBuilder

class GrpcConfig {

    private var channel: ManagedChannel? = null

    fun getCardServiceGrpcStub(): CardServiceFutureStub {
        startChannel()
        return CardServiceGrpc.newFutureStub(channel)
    }

    private fun startChannel() {
        if(channel != null) {
            return
        }

        channel = OkHttpChannelBuilder
            .forAddress(BACKEND_HOST, BACKEND_PORT)
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