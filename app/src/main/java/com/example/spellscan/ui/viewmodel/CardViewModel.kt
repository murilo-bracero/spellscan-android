package com.example.spellscan.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscan.model.CardReference

class CardViewModel : ViewModel() {

    val cardLiveData: MutableLiveData<CardReference> by lazy {
        MutableLiveData<CardReference>()
    }
}