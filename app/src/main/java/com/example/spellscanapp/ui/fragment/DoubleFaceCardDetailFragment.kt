package com.example.spellscanapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import coil.imageLoader
import coil.request.ImageRequest
import com.example.spellscanapp.databinding.FragmentDoubleFaceCardDetailBinding
import com.example.spellscanapp.model.CardFace
import com.example.spellscanapp.service.AuthService
import com.example.spellscanapp.ui.fragment.component.AddToInventoryFragment
import com.example.spellscanapp.ui.viewmodel.CardServiceViewModel
import com.example.spellscanapp.util.renderBackFace
import com.example.spellscanapp.util.renderFrontFace
import kotlinx.coroutines.launch

private const val ARG_CARD_ID = "cardId"

class DoubleFaceCardDetailFragment : Fragment() {
    private val cardServiceViewModel: CardServiceViewModel by activityViewModels()

    private var cardId: String? = null
    private var currentArt: String? = null

    private lateinit var binding: FragmentDoubleFaceCardDetailBinding

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
        binding = FragmentDoubleFaceCardDetailBinding.inflate(layoutInflater, container, false)


        if (savedInstanceState == null && authService.isAuthorized()) {
            childFragmentManager.commit {
                replace(binding.addToInventoryFragmentContainer.id, AddToInventoryFragment.newInstance(cardId))
            }
        }

        lifecycleScope.launch {
            renderDoubleFacedCard(cardId)
        }

        return binding.root
    }

    private suspend fun renderDoubleFacedCard(cardId: String?) {
        if (cardId == null) return

        val card = cardServiceViewModel.findById(cardId) ?: return

        val back = card.cardFaces.last()

        renderFrontFace(
            card,
            requireContext(),
            binding.cardCostContainer,
            binding.cardCover,
            binding.cardDetailName,
            binding.cardDetailType,
            binding.cardDetailSet,
            binding.cardDetailLang,
            binding.cardDetailText
        )

        renderBackFace(
            back,
            requireContext(),
            binding.backDetailName,
            binding.backDetailType,
            binding.backDetailText
        )

        val front = card.cardFaces.first()

        currentArt = front.artImage

        applyFABCallback(front, back)
    }

    private fun applyFABCallback(front: CardFace, back: CardFace) {
        binding.fabFlipArt.setOnClickListener {
            currentArt = if (currentArt == front.artImage) {
                flipArt(back.artImage)
                back.artImage
            } else {
                flipArt(front.artImage)
                front.artImage
            }
        }
    }

    private fun flipArt(artUrl: String) {
        val imageLoader = binding.cardCover.context.imageLoader
        val request = ImageRequest.Builder(binding.cardCover.context)
            .data(artUrl)
            .target(binding.cardCover)
            .build()
        imageLoader.enqueue(request)
    }

    companion object {
        @JvmStatic
        fun newInstance(cardId: String?) =
            DoubleFaceCardDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CARD_ID, cardId)
                }
            }
    }
}