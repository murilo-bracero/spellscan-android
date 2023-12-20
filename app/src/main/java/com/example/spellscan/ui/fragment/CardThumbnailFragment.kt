package com.example.spellscan.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.spellscan.R
import com.example.spellscan.databinding.FragmentCardThumbnailBinding
import com.example.spellscan.logger.TAG
import com.example.spellscan.model.Card
import com.example.spellscan.repository.LocalCardRepository
import com.example.spellscan.ui.CardListActivity
import com.example.spellscan.ui.viewmodel.CardViewModel

class CardThumbnailFragment : Fragment() {

    private val cardViewModel: CardViewModel by activityViewModels()
    private val localCardRepository = LocalCardRepository.getInstance()

    private var binding: FragmentCardThumbnailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCardThumbnailBinding.inflate(inflater, container, false)

        binding?.addCardButton?.setOnClickListener { addCard() }
        binding?.openListButton?.setOnClickListener { openListActivity() }

        val view = binding?.root

        val cardObserver = Observer<Card> {
            binding?.cardNameText?.text = it.name
            binding?.cardTypeText?.text = it.type
            binding?.cardSetText?.text = it.set
        }

        cardViewModel.cardLiveData.observe(viewLifecycleOwner, cardObserver)

        return view
    }

    private fun addCard() {
        cardViewModel.cardLiveData.value?.let {
            localCardRepository.save(it.copy())
            Log.i(TAG, "Added card: $it")
        }
    }

    private fun openListActivity() {
        val intent = Intent(activity, CardListActivity::class.java)

        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}