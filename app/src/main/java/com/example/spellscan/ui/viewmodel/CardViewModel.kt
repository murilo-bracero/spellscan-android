package com.example.spellscan.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscan.model.Card

class CardViewModel : ViewModel() {

    val cardLiveData: MutableLiveData<Card> by lazy {
        MutableLiveData<Card>()
    }
}