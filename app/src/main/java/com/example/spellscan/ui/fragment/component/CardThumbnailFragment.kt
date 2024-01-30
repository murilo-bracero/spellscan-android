package com.example.spellscan.ui.fragment.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.spellscan.databinding.FragmentCardThumbnailBinding
import com.example.spellscan.model.CardRow
import com.example.spellscan.ui.viewmodel.CardServiceViewModel
import com.example.spellscan.ui.viewmodel.CardViewModel
import kotlinx.coroutines.launch
import java.util.UUID

class CardThumbnailFragment : Fragment() {

    private val cardViewModel: CardViewModel by activityViewModels()
    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()

    private lateinit var binding: FragmentCardThumbnailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCardThumbnailBinding.inflate(inflater, container, false)

        binding.addCardButton.setOnClickListener { addCard() }

        cardViewModel.cardLiveData.observe(viewLifecycleOwner) {
            binding.cardNameText.text = it.name
            binding.cardTypeText.text = it.type
            binding.cardSetText.text = it.set
        }

        return binding.root
    }

    private fun addCard() {
        val card = cardViewModel.cardLiveData.value!!

        val cardRow = CardRow(UUID.randomUUID(), card.name, card.type, card.set, false)

        lifecycleScope.launch {
            cardServiceViewModel.search(cardRow)
        }
    }
}