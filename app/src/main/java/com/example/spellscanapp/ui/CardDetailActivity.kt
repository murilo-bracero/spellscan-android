package com.example.spellscanapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.spellscanapp.databinding.ActivityCardDetailBinding
import com.example.spellscanapp.ui.fragment.DoubleFaceCardDetailFragment
import com.example.spellscanapp.ui.fragment.SingleFaceCardDetailFragment

class CardDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardDetailBinding.inflate(layoutInflater)

        val cardId = intent.getStringExtra(CARD_ID_INTENT_KEY)
        val hasCardFaces = intent.getBooleanExtra(HAS_CARD_FACES_INTENT_KEY, false)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)

                val fragment: Fragment = if (hasCardFaces)
                    DoubleFaceCardDetailFragment.newInstance(cardId)
                else
                    SingleFaceCardDetailFragment.newInstance(cardId)

                replace(
                    binding.cardDetailFragmentContainer.id,
                    fragment
                )
            }
        }

        setContentView(binding.root)
    }

    companion object {
        const val CARD_ID_INTENT_KEY = "card_id"
        const val HAS_CARD_FACES_INTENT_KEY = "has_card_faces"
    }
}