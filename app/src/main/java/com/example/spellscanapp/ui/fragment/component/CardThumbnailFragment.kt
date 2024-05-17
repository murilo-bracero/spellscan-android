package com.example.spellscanapp.ui.fragment.component

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.spellscanapp.databinding.FragmentCardThumbnailBinding
import com.example.spellscanapp.logger.TAG
import com.example.spellscanapp.ui.CardDetailActivity
import com.example.spellscanapp.ui.CardDetailActivity.Companion.CARD_ID_INTENT_KEY
import com.example.spellscanapp.ui.CardDetailActivity.Companion.HAS_CARD_FACES_INTENT_KEY
import com.example.spellscanapp.ui.viewmodel.CardServiceViewModel
import com.example.spellscanapp.ui.viewmodel.CardViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class CardThumbnailFragment : Fragment() {

    private val cardViewModel: CardViewModel by activityViewModels()
    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()

    private lateinit var binding: FragmentCardThumbnailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCardThumbnailBinding.inflate(inflater, container, false)

        binding.scanCardButton.setOnClickListener { scanCard() }

        cardViewModel.cardLiveData.observe(viewLifecycleOwner) {
            binding.cardNameText.text = it.name
            binding.cardTypeText.text = it.type
            binding.cardSetText.text = it.set
        }

        return binding.root
    }

    private fun scanCard() {
        val card = cardViewModel.cardLiveData.value ?: return

        val handler = CoroutineExceptionHandler { _, throwable ->
            Log.e(TAG, "Error adding card", throwable)

            Snackbar.make(
                binding.cardThumbnailRoot,
                "Could not find card due to a network error. Check your internet connection and try again.",
                LENGTH_LONG
            ).show()
        }

        lifecycleScope.launch(handler) {
            val found = cardServiceViewModel.search(card)

            val intent = Intent(requireContext(), CardDetailActivity::class.java)
            intent.putExtra(CARD_ID_INTENT_KEY, found.id)
            intent.putExtra(HAS_CARD_FACES_INTENT_KEY, found.cardFaces.isNotEmpty())
            requireContext().startActivity(intent)
            return@launch
        }
    }
}