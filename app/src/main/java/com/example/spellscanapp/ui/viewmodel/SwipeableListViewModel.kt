package com.example.spellscanapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscanapp.model.Card

class SwipeableListViewModel: ViewModel() {

    val dataset: MutableLiveData<List<Card>> by lazy {
        MutableLiveData<List<Card>>()
    }
}