package com.example.spellscanapp.model.dto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

enum class FilterDirection {
    ASC, DESC
}

class InventoryListFilter {

    private val _nameDirection = MutableLiveData<FilterDirection>()

    val nameDirection: LiveData<FilterDirection> = _nameDirection

    fun setNameDirection(direction: FilterDirection) {
        _nameDirection.value = direction
    }
}