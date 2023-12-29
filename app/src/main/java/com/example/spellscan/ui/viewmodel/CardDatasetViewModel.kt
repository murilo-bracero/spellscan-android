package com.example.spellscan.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscan.model.CardRow
import com.example.spellscan.model.toCardRow
import com.example.spellscan.repository.LocalCardRepository

class CardDatasetViewModel: ViewModel() {

    private val localCardRepository = LocalCardRepository.getInstance()

    val cardLiveData: MutableLiveData<MutableList<CardRow>> by lazy {
        MutableLiveData<MutableList<CardRow>>()
    }

    val checkedLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    fun fetchAll() {
        cardLiveData.value = localCardRepository
            .findAll()
            .map { it.toCardRow() }
            .toMutableList()
    }

    fun removeByIndex(index: Int) {
        if (cardLiveData.value == null) {
            return
        }

        cardLiveData.value!!.removeAt(index)
            .let {
                localCardRepository.deleteById(it.id)
                updateCheckedLiveData()
            }
    }

    fun setIsChecked(index: Int, isChecked: Boolean) {
        if (cardLiveData.value == null) {
            return
        }
        cardLiveData.value!![index].isChecked = isChecked

        updateCheckedLiveData()
    }

    fun getCheckedCards(): List<CardRow> {
        if (cardLiveData.value == null) {
            return emptyList()
        }
        return cardLiveData.value!!.filter { it.isChecked }
    }

    fun clearSelected() {
        if (cardLiveData.value == null) {
            return
        }
        cardLiveData.value!!.forEach {
            it.isChecked = false
        }
        updateCheckedLiveData()
    }

    fun removeChecked() {
        if (cardLiveData.value == null) {
            return
        }
        cardLiveData.value!!.removeIf {
            it.isChecked
        }
        updateCheckedLiveData()
    }

    fun reset() {
        localCardRepository.reset()
        cardLiveData.value = emptyList<CardRow>().toMutableList()
        updateCheckedLiveData()
    }

    private fun updateCheckedLiveData(){
        checkedLiveData.value = getCheckedCards().size
    }
}