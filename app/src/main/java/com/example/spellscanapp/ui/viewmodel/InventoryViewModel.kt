package com.example.spellscanapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.spellscanapp.model.dto.FilterDirection
import com.example.spellscanapp.service.InventoryService
import com.spellscan.inventoryservice.InventoryResponse

class InventoryViewModel : ViewModel() {

    private val inventoryService by lazy {
        InventoryService.newInstance()
    }

    private val _inventoryDataset: MutableLiveData<MutableList<InventoryResponse>> by lazy {
        MutableLiveData<MutableList<InventoryResponse>>()
    }

    val inventoryDataset: LiveData<List<InventoryResponse>> = _inventoryDataset.map { it.toList() }

    suspend fun loadInventories(accessToken: String) {
        _inventoryDataset.value = inventoryService.findInventories(accessToken)
            .inventoriesList
    }

    suspend fun findInventoryById(accessToken: String, id: String): InventoryResponse {
        return inventoryService.findInventoryById(accessToken, id)
    }

    fun sortByName(direction: FilterDirection?) {
        if(direction == null) {
            _inventoryDataset.value = _inventoryDataset.value?.sortedBy { it.id }?.toMutableList()
            return
        }

        if (direction == FilterDirection.DESC) {
            _inventoryDataset.value = _inventoryDataset.value?.sortedByDescending { it.name }?.toMutableList()
            return
        }

        _inventoryDataset.value = _inventoryDataset.value?.sortedBy { it.name }?.toMutableList()
    }
}
