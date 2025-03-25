package com.example.spellscanapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscanapp.model.CardReference

class CardViewModel : ViewModel() {

    val cardLiveData: MutableLiveData<CardReference> by lazy {
        MutableLiveData<CardReference>()
    }
}