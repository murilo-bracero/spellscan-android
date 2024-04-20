package com.example.spellscanapp.ui.fragment.component

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
import com.example.spellscanapp.ui.viewmodel.CardServiceViewModel
import com.example.spellscanapp.ui.viewmodel.CardViewModel
import com.google.android.material.snackbar.Snackbar
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

        val handler = CoroutineExceptionHandler { _, throwable ->
            Log.e(TAG, throwable.toString())

            Snackbar.make(
                binding.cardThumbnailRoot,
                "Could not find card due to a network error. Check your internet connection and try again.",
                Snackbar.LENGTH_LONG
            ).show()
        }

        lifecycleScope.launch(handler) {
            cardServiceViewModel.search(card)
        }
    }
}