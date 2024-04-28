package com.example.spellscanapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscanapp.db.entity.CardEntity

@Deprecated("Use InventoryViewModel and/or CardServiceViewModel instead")
class CardInventoryViewModel: ViewModel() {

    private val _cardDataset: MutableLiveData<List<CardEntity>> by lazy {
        MutableLiveData<List<CardEntity>>()
    }

    val cardDataset: LiveData<List<CardEntity>> = _cardDataset

    fun setCardDataset(dataset: List<CardEntity>) {
        _cardDataset.value = dataset
    }

    fun removeByIndex(index: Int) {
        _cardDataset.value?.let {
            _cardDataset.value = it.toMutableList().apply {
                removeAt(index)
            }.toList()
        }
    }
}