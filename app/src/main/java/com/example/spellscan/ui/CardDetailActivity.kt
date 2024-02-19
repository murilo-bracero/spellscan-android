package com.example.spellscan.ui

import android.os.Bundle
import android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.text.style.ImageSpan.ALIGN_BASELINE
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.TextView.BufferType.SPANNABLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import coil.imageLoader
import coil.request.ImageRequest
import com.example.spellscan.R
import com.example.spellscan.converter.languageToResource
import com.example.spellscan.converter.symbolToDrawable
import com.example.spellscan.databinding.ActivityCardDetailBinding
import com.example.spellscan.ui.viewmodel.CardServiceViewModel
import com.example.spellscan.util.CardDetailUtils
import kotlinx.coroutines.launch

class CardDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardDetailBinding

    private val cardServiceViewModel: CardServiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardDetailBinding.inflate(layoutInflater)

        val cardId = intent.getStringExtra(CARD_ID_INTENT_KEY)

        lifecycleScope.launch {
            CardDetailUtils().loadCard(
                cardId,
                cardServiceViewModel,
                this@CardDetailActivity,
                binding.cardCostContainer,
                binding.cardCover,
                binding.cardDetailName,
                binding.cardDetailType,
                binding.cardDetailSet,
                binding.cardDetailLang,
                binding.cardDetailText
            )
        }

        setContentView(binding.root)
    }

    companion object {
        const val CARD_ID_INTENT_KEY = "card_id"
    }
}