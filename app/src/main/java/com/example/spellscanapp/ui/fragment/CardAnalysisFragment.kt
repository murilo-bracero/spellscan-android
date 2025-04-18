package com.example.spellscanapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.spellscanapp.R
import com.example.spellscanapp.databinding.FragmentCardAnalysisBinding
import com.example.spellscanapp.ui.fragment.CardDetailFragment.Companion.ARG_CARD_ID
import com.example.spellscanapp.ui.fragment.CardDetailFragment.Companion.ARG_HAS_CARD_FACES
import com.example.spellscanapp.ui.viewmodel.CardServiceViewModel
import com.example.spellscanapp.ui.viewmodel.CardViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class CardAnalysisFragment : Fragment() {

    private val cardViewModel: CardViewModel by activityViewModels()
    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()

    private lateinit var binding: FragmentCardAnalysisBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCardAnalysisBinding.inflate(layoutInflater, container, false)

        binding.scanFab.setOnClickListener { scanCard() }

        return binding.root
    }

    private fun scanCard() {
        setLoading(true)

        val card = cardViewModel.cardLiveData.value ?: return

        val handler = CoroutineExceptionHandler { _, throwable ->
            setLoading(false)
            Log.e(TAG, "Error adding card", throwable)

            Snackbar.make(
                binding.root,
                "Could not find card due to a network error. Check your internet connection and try again.",
                LENGTH_LONG
            ).show()
        }

        lifecycleScope.launch(handler) {
            val found = cardServiceViewModel.search(card).also { setLoading(false) }

            val navController = findNavController()
            navController.navigate(R.id.cardDetailFragment, Bundle().apply {
                putString(ARG_CARD_ID, found.id)
                putBoolean(ARG_HAS_CARD_FACES, found.cardFaces.isNotEmpty())
            })

            return@launch
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.loadingProgressIndicator.isVisible = loading
    }

    companion object {
        const val TAG = "CardAnalysisFragment"
    }
}