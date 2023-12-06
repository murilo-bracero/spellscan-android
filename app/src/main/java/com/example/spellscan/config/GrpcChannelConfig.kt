package com.example.spellscan.config

import android.content.Context
import com.example.spellscan.BuildConfig.BACKEND_HOST
import com.example.spellscan.BuildConfig.BACKEND_PORT
import io.grpc.ManagedChannel
import io.grpc.cronet.CronetChannelBuilder
import org.chromium.net.ExperimentalCronetEngine

class GrpcChannelConfig(private val context: Context) {

    fun getChannel(): ManagedChannel? {
        val engine = ExperimentalCronetEngine.Builder(context).build()
        return CronetChannelBuilder.forAddress(BACKEND_HOST, BACKEND_PORT, engine).build()
    }
}