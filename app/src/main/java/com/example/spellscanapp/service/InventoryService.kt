package com.example.spellscanapp.service

import com.example.spellscanapp.config.GrpcConfig
import com.spellscan.inventoryservice.AddToInventoryRequest
import com.spellscan.inventoryservice.Empty
import com.spellscan.inventoryservice.FindInventoriesByOwnerIdResponse
import com.spellscan.inventoryservice.FindInventoryByIdRequest
import com.spellscan.inventoryservice.InventoryResponse
import com.spellscan.inventoryservice.InventoryServiceGrpc.InventoryServiceBlockingStub
import io.grpc.ClientInterceptor
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import io.grpc.Metadata.Key
import io.grpc.stub.MetadataUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryService(
    private val stub: InventoryServiceBlockingStub
) {

    private val metadataInterceptor: (String) -> ClientInterceptor = { accessToken ->
        MetadataUtils.newAttachHeadersInterceptor(io.grpc.Metadata().also {
            val key = Key.of("Authorization", ASCII_STRING_MARSHALLER)
            it.put(key, "Bearer $accessToken")
        })
    }

    suspend fun findInventories(accessToken: String): FindInventoriesByOwnerIdResponse =
        withContext(Dispatchers.IO) {
            stub.withInterceptors(metadataInterceptor(accessToken))
                .findInventoriesByOwnerId(Empty.newBuilder().build())
        }

    suspend fun findInventoryById(accessToken: String, id: String): InventoryResponse =
        withContext(Dispatchers.IO) {
            stub.withInterceptors(metadataInterceptor(accessToken))
                .findInventoryById(FindInventoryByIdRequest.newBuilder().setId(id).build())
        }

    suspend fun addToInventory(accessToken: String, cardId: String): Empty =
        withContext(Dispatchers.IO) {
            stub.withInterceptors(metadataInterceptor(accessToken))
                .addToInventory(AddToInventoryRequest.newBuilder().setCardId(cardId).build())
        }

    companion object {
        fun newInstance(): InventoryService {
            val grpcConfig = GrpcConfig.getInstance()
            val stub = grpcConfig.getInventoryServiceGrpcStub()
            return InventoryService(stub)
        }
    }
}
