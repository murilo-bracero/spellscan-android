package com.example.spellscan.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscan.model.CardRow
import com.example.spellscan.model.toCardRow
import com.example.spellscan.repository.LocalCardRepository

class CardDatasetViewModel: ViewModel() {

    private val localCardRepository = LocalCardRepository.getInstance()

    private val _cardLiveData: MutableLiveData<MutableList<CardRow>> by lazy {
        MutableLiveData<MutableList<CardRow>>()
    }

    val cardLiveData: LiveData<MutableList<CardRow>> = _cardLiveData

    val checkedLiveData: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    fun fetchAll() {
        _cardLiveData.value = localCardRepository
            .findAll()
            .map { it.toCardRow() }
            .toMutableList()
    }

    fun findByIndex(index: Int): CardRow? {
        if (_cardLiveData.value == null) {
            return null
        }
        return _cardLiveData.value!![index]
    }

    fun removeByIndex(index: Int) {
        if (_cardLiveData.value == null) {
            return
        }

        _cardLiveData.value!!.removeAt(index)
            .let {
                localCardRepository.deleteById(it.id)
                updateCheckedLiveData()
            }
    }

    fun setIsChecked(index: Int, isChecked: Boolean) {
        if (_cardLiveData.value == null) {
            return
        }
        _cardLiveData.value!![index].isChecked = isChecked

        updateCheckedLiveData()
    }

    fun getCheckedCards(): List<CardRow> {
        if (_cardLiveData.value == null) {
            return emptyList()
        }
        return _cardLiveData.value!!.filter { it.isChecked }
    }

    fun clearSelected() {
        if (cardLiveData.value == null) {
            return
        }
        _cardLiveData.value!!.forEach {
            it.isChecked = false
        }
        updateCheckedLiveData()
    }

    fun removeChecked() {
        if (_cardLiveData.value == null) {
            return
        }
        _cardLiveData.value!!.removeIf {
            it.isChecked
        }
        updateCheckedLiveData()
    }

    fun reset() {
        localCardRepository.reset()
        _cardLiveData.value = emptyList<CardRow>().toMutableList()
        updateCheckedLiveData()
    }

    private fun updateCheckedLiveData(){
        checkedLiveData.value = getCheckedCards().size
    }
}