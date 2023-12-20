package com.example.spellscan.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscan.model.CardRow

class CardDatasetViewModel : ViewModel() {

    val cardLiveData: MutableLiveData<MutableList<CardRow>> by lazy {
        MutableLiveData<MutableList<CardRow>>()
    }

    fun setCardList(cardList: List<CardRow>) {
        cardLiveData.value = cardList.toMutableList()
    }

    fun removeByIndex(index: Int): CardRow? {
        if(cardLiveData.value == null) {
            return null
        }
        return cardLiveData.value!!.removeAt(index)
    }

    fun removeCheckedCards() {
        if(cardLiveData.value == null) {
            return
        }
        cardLiveData.value = cardLiveData.value!!
            .filter { !it.isChecked }
            .toMutableList()
    }

    fun getCheckedCards(): List<CardRow> {
        if(cardLiveData.value == null) {
            return emptyList()
        }
        return cardLiveData.value!!.filter { it.isChecked }
    }
}