package com.example.spellscan.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscan.model.Card
import com.example.spellscan.repository.LocalCardRepository

class CardViewModel : ViewModel() {

    private val localCardRepository = LocalCardRepository.getInstance()

    val cardLiveData: MutableLiveData<Card> by lazy {
        MutableLiveData<Card>()
    }

    fun save() {
        cardLiveData.value?.let {
            localCardRepository.save(it)
        }
    }
}