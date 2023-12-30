package com.example.spellscan.ui.fragment.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.spellscan.databinding.FragmentCardThumbnailBinding
import com.example.spellscan.model.Card
import com.example.spellscan.ui.viewmodel.CardViewModel

class CardThumbnailFragment : Fragment() {

    private val cardViewModel: CardViewModel by activityViewModels()

    private var binding: FragmentCardThumbnailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCardThumbnailBinding.inflate(inflater, container, false)

        binding?.addCardButton?.setOnClickListener { addCard() }

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
        cardViewModel.save()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}