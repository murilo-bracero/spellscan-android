package com.example.spellscanapp.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.spellscanapp.db.entity.CacheEntity
import com.example.spellscanapp.model.CacheCallEnum.GET_INVENTORIES
import com.example.spellscanapp.model.CacheCallEnum.GET_INVENTORY_BY_ID
import com.example.spellscanapp.model.Inventory
import com.example.spellscanapp.model.buildInventory
import com.example.spellscanapp.repository.CacheRepository
import com.example.spellscanapp.service.InventoryService
import com.example.spellscanapp.util.buildCacheHash
import com.google.gson.Gson

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val inventoryService by lazy {
        InventoryService.newInstance()
    }

    private val cacheRepository by lazy {
        CacheRepository.getInstance(application.baseContext)
    }

    suspend fun loadInventories(accessToken: String): List<Inventory> {
        val hashKey = buildCacheHash(GET_INVENTORIES)
        val cached = cacheRepository.findByHash(hashKey)

        Log.d("InventoryViewModel", "loadInventories: $cached")

        if (cached != null) {
            return Gson().fromJson(cached.responsePayload, Array<Inventory>::class.java).toList()
        }

        return inventoryService.findInventories(accessToken)
            .inventoriesList.map { buildInventory(it) }
            .also {
                cacheRepository.save(CacheEntity(hashKey, Gson().toJson(it)))
            }
    }

    suspend fun findInventoryById(accessToken: String, id: String): Inventory {
        val hashKey = buildCacheHash(GET_INVENTORY_BY_ID, id)
        val cached = cacheRepository.findByHash(hashKey)

        Log.d("InventoryViewModel", "findInventoryById: $cached")

        if (cached != null) {
            return Gson().fromJson(cached.responsePayload, Inventory::class.java)
        }

        val inventory = inventoryService.findInventoryById(accessToken, id)

        return buildInventory(inventory)
            .also {
                cacheRepository.save(
                    CacheEntity(hashKey, Gson().toJson(it))
                )
            }
    }
}
