package com.example.spellscan.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellscan.logger.TAG
import com.example.spellscan.model.Card
import com.example.spellscan.repository.LocalCardRepository

class CardViewModel : ViewModel() {

    private val localCardRepository = LocalCardRepository.getInstance()

    val cardLiveData: MutableLiveData<Card> by lazy {
        MutableLiveData<Card>()
    }

    fun save() {
        cardLiveData.value?.let {
            // ACHTUNG!
            // This might generate a bug for duplicate cards in the future.
            // if it happens, just remove the copy line and save the card
            val card = it.copy(localId = null)
            localCardRepository.save(card)
            Log.i(TAG, "Card saved: $card")
        }
    }
}