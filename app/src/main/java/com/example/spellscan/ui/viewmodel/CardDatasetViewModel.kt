package com.example.spellscan.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscan.model.CardRow

class CardDatasetViewModel : ViewModel() {

    val cardLiveData: MutableLiveData<MutableList<CardRow>> by lazy {
        MutableLiveData<MutableList<CardRow>>()
    }

    val checkedLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    fun setCardList(cardList: List<CardRow>) {
        cardLiveData.value = cardList.toMutableList()
    }

    fun removeByIndex(index: Int): CardRow? {
        if (cardLiveData.value == null) {
            return null
        }
        return cardLiveData.value!!.removeAt(index)
    }

    fun updateChecked(index: Int, isChecked: Boolean) {
        if (cardLiveData.value == null) {
            return
        }
        cardLiveData.value!![index].isChecked = isChecked

        when {
            checkedLiveData.value == null && isChecked -> checkedLiveData.value = true
            checkedLiveData.value == false && isChecked -> checkedLiveData.value = true
            checkedLiveData.value == true && getCheckedCards().isEmpty() -> checkedLiveData.value =
                false

            else -> checkedLiveData.value = null
        }

    }

    fun getCheckedCards(): List<CardRow> {
        if (cardLiveData.value == null) {
            return emptyList()
        }
        return cardLiveData.value!!.filter { it.isChecked }
    }

    fun reset() {
        cardLiveData.value = emptyList<CardRow>().toMutableList()
    }
}