package com.example.spellscanapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.spellscanapp.databinding.FragmentCardDetailBinding

class CardDetailFragment : Fragment() {

    private lateinit var binding: FragmentCardDetailBinding
    private var cardId: String? = null
    private var hasCardFaces: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCardDetailBinding.inflate(inflater, container, false)

        cardId = arguments?.getString(ARG_CARD_ID)
        hasCardFaces = arguments?.getBoolean(ARG_HAS_CARD_FACES) ?: false

        parentFragmentManager.commit {
            setReorderingAllowed(true)

            val fragment: Fragment = if (hasCardFaces as Boolean)
                DoubleFaceCardDetailFragment.newInstance(cardId)
            else
                SingleFaceCardDetailFragment.newInstance(cardId)

            replace(
                binding.cardDetailFragmentContainer.id,
                fragment
            )
        }

        return binding.root
    }

    companion object {
        const val ARG_CARD_ID = "cardId"
        const val ARG_HAS_CARD_FACES = "hasCardFaces"
    }
}
