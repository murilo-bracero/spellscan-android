package com.example.spellscan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.spellscan.R
import com.example.spellscan.databinding.ActivityCardDetailBinding
import com.example.spellscan.ui.viewmodel.CardServiceViewModel
import kotlinx.coroutines.launch

class CardDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardDetailBinding

    private val cardServiceViewModel: CardServiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardDetailBinding.inflate(layoutInflater)

        val cardId = intent.getStringExtra(CARD_ID_INTENT_KEY)

        if (cardId != null) {
            loadCard(cardId)
        }

        setContentView(binding.root)
    }

    private fun loadCard(id: String) {
        lifecycleScope.launch {
            val card = cardServiceViewModel.findById(id)

            binding.cardDetailName.text = card.name
            binding.cardDetailType.text = card.type
            binding.cardDetailSet.text = card.set
            binding.cardDetailCost.text = card.cost
            binding.cardDetailText.text = card.printedText
        }
    }

    companion object {
        const val CARD_ID_INTENT_KEY = "card_id"
    }
}