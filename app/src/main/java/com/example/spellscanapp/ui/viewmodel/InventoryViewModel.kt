package com.example.spellscanapp.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.spellscanapp.repository.AuthStateRepository
import com.example.spellscanapp.service.InventoryService
import com.example.spellscanapp.ui.LoginAdapterActivity
import com.spellscan.inventoryservice.InventoryResponse
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationService

class InventoryViewModel(private val application: Application) : AndroidViewModel(application) {

    private val inventoryService by lazy {
        InventoryService.newInstance()
    }

    private val authStateRepository by lazy {
        AuthStateRepository()
    }

    private val authorizationService by lazy {
        AuthorizationService(application.baseContext)
    }

    private val _inventoryDataset: MutableLiveData<MutableList<InventoryResponse>> by lazy {
        MutableLiveData<MutableList<InventoryResponse>>()
    }

    val inventoryDataset: LiveData<List<InventoryResponse>> = _inventoryDataset.map { it.toList() }

    suspend fun loadInventories() {
        val authState = authStateRepository.find(application.baseContext)

        authState.performActionWithFreshTokens(authorizationService) { accessToken, _, ex ->
            if(ex != null || accessToken.isNullOrBlank()) {
                Log.e(TAG, "Failed to get fresh tokens on load inventories", ex)
                val intent = Intent(application.baseContext, LoginAdapterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                application.baseContext.startActivity(intent)
                return@performActionWithFreshTokens
            }

            viewModelScope.launch {
                val inventories = inventoryService.findInventories(accessToken)

                _inventoryDataset.value = inventories.inventoriesList
            }
        }
    }

    companion object {
        const val TAG = "InventoryViewModel"
    }
}
