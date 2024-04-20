package com.example.spellscanapp.config

import com.example.spellscanapp.BuildConfig.BACKEND_HOST
import com.example.spellscanapp.BuildConfig.BACKEND_PORT
import com.spellscan.cardservice.CardServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.okhttp.OkHttpChannelBuilder

class GrpcConfig {

    private var channel: ManagedChannel? = null

    fun getCardServiceGrpcStub(): CardServiceGrpc.CardServiceFutureStub {
        startChannel()
        return CardServiceGrpc.newFutureStub(channel)
    }

    private fun startChannel() {
        if(channel != null) {
            return
        }

        channel = OkHttpChannelBuilder
            .forAddress(BACKEND_HOST, BACKEND_PORT)
            .useTransportSecurity()
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