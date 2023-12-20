package com.example.spellscan.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spellscan.R
import com.example.spellscan.databinding.ActivityCardListBinding
import com.example.spellscan.logger.TAG
import com.example.spellscan.model.Card
import com.example.spellscan.model.newCard
import com.example.spellscan.model.toCardRow
import com.example.spellscan.repository.LocalCardRepository
import com.example.spellscan.service.CardService
import com.example.spellscan.ui.adapter.CardListAdapter
import com.example.spellscan.ui.fragment.SwipableListFragment
import com.example.spellscan.ui.viewmodel.CardDatasetViewModel
import com.example.spellscan.ui.viewmodel.CardViewModel
import kotlinx.coroutines.launch

class CardListActivity : AppCompatActivity() {
    private val cardDatasetViewModel: CardDatasetViewModel by viewModels()

    private val localCardRepository = LocalCardRepository.getInstance()
    private lateinit var cardService: CardService

    private lateinit var binding: ActivityCardListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cardDatasetViewModel.setCardList(localCardRepository
            .findAll()
            .map { it.toCardRow() })

        binding.deleteRowButton.setOnClickListener {
            onDeleteButtonClicked()
        }

        binding.findAllButton.setOnClickListener {
            onFindAllButtonClicked()
        }

        cardService = CardService.newInstance()
    }

    private fun onDeleteButtonClicked() {
        cardDatasetViewModel.getCheckedCards()
            .map {
                localCardRepository.deleteById(it.id)
            }

        cardDatasetViewModel.removeCheckedCards()
    }

    private fun onFindAllButtonClicked() {
        cardDatasetViewModel.getCheckedCards()
            .map {
                lifecycleScope.launch {
                    val card = cardService.find(newCard(it.name, it.type, it.set))
                    //TODO: implement after find flow
                    Log.i(TAG, "${card.id} - ${card.name} - ${card.type} - ${card.set}")
                }
            }
    }
}