package com.example.spellscanapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.example.spellscanapp.databinding.FragmentSingleFaceCardDetailBinding
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.ui.fragment.component.AddToInventoryFragment
import com.example.spellscanapp.ui.viewmodel.CardServiceViewModel
import com.example.spellscanapp.util.renderCardDetails
import kotlinx.coroutines.launch

private const val ARG_CARD_ID = "cardId"

class SingleFaceCardDetailFragment : Fragment() {

    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()

    private var cardId: String? = null

    private lateinit var binding: FragmentSingleFaceCardDetailBinding

    private val authService by lazy {
        AuthService(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cardId = it.getString(ARG_CARD_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSingleFaceCardDetailBinding.inflate(layoutInflater, container, false)

        if (savedInstanceState == null && authService.isAuthorized()) {
            childFragmentManager.commit {
                replace(binding.addToInventoryFragmentContainer.id, AddToInventoryFragment.newInstance(cardId))
            }
        }

        if(cardId.isNullOrEmpty()) {
            return binding.root
        }

        lifecycleScope.launch {
            val card = cardServiceViewModel.findById(cardId!!) ?: return@launch

            renderCardDetails(
                card,
                requireContext(),
                binding.cardCover,
                binding.cardCostContainer,
                binding.cardDetailName,
                binding.cardDetailType,
                binding.cardDetailSet,
                binding.cardDetailLang,
                binding.cardDetailText
            )
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(cardId: String?): SingleFaceCardDetailFragment =
            SingleFaceCardDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("cardId", cardId)
                }
            }
    }
}