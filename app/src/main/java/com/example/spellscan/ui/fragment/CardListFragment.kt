package com.example.spellscan.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.spellscan.databinding.FragmentCardListBinding
import com.example.spellscan.logger.TAG
import com.example.spellscan.model.newCard
import com.example.spellscan.model.toCardRow
import com.example.spellscan.repository.LocalCardRepository
import com.example.spellscan.service.CardService
import com.example.spellscan.ui.viewmodel.CardDatasetViewModel
import kotlinx.coroutines.launch

class CardListFragment : Fragment() {

    private val cardDatasetViewModel: CardDatasetViewModel by activityViewModels()

    private lateinit var localCardRepository: LocalCardRepository
    private lateinit var cardService: CardService
    private lateinit var binding: FragmentCardListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localCardRepository = LocalCardRepository.getInstance()
        cardService = CardService.newInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCardListBinding.inflate(layoutInflater, container, false)

        cardDatasetViewModel.setCardList(localCardRepository
            .findAll()
            .map { it.toCardRow() })

        binding.clearAllButton.setOnClickListener {
            clearAll()
        }

        binding.findAllButton.setOnClickListener {
            findAll()
        }

        cardDatasetViewModel.checkedLiveData.observe(viewLifecycleOwner) {
            if (it) {
                Log.i(TAG, "Has checked")
            }

            Log.i(TAG, "Observing")
        }
        return binding.root
    }

    private fun clearAll() {
        localCardRepository.reset()
        cardDatasetViewModel.reset()
    }

    private fun findAll() {
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