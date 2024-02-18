package com.example.spellscan.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE
import android.text.SpannableStringBuilder
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.text.style.ImageSpan.ALIGN_BASELINE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.BufferType.SPANNABLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import coil.imageLoader
import coil.request.ImageRequest
import com.example.spellscan.R
import com.example.spellscan.databinding.ActivityCardDetailBinding
import com.example.spellscan.ui.viewmodel.CardServiceViewModel
import kotlinx.coroutines.launch

class CardDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardDetailBinding

    private val cardServiceViewModel: CardServiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCardDetailBinding.inflate(layoutInflater)

        val cardId = intent.getStringExtra(CARD_ID_INTENT_KEY)

        if (cardId != null) {
            loadCard(cardId)
        }

        val gb = GradientDrawable().apply {
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            cornerRadius = 0f
            colors = intArrayOf(
                Color.argb(255, 60, 176, 67),
                Color.BLACK
            )
            gradientType = GradientDrawable.LINEAR_GRADIENT
            shape = GradientDrawable.RECTANGLE
        }

        binding.cardCoverContainer.background = gb

        setContentView(binding.root)
    }

    private fun loadCard(id: String) {
        lifecycleScope.launch {
            val card = cardServiceViewModel.findById(id)

            val imageLoader = binding.cardCover.context.imageLoader
            val request = ImageRequest.Builder(binding.cardCover.context)
                .data(card.artImageUrl)
                .target(binding.cardCover)
                .build()
            imageLoader.enqueue(request)

            loadManaCost(card.cost)

            renderPrintedText(card.printedText)

            binding.cardDetailName.text = card.name
            binding.cardDetailType.text = card.type
            binding.cardDetailSet.text = card.set
            binding.cardDetailLang.setText(getStringResource(card.lang))
        }
    }

    private fun loadManaCost(manaCost: String) {
        manaCost.split("}")
            .filter { it.isNotEmpty() }
            .map { it.removePrefix("{") }
            .map { renderManaCost(it) }
    }

    private fun renderManaCost(manaCost: String) {
        val imageView = ImageView(this)
        val width = resources.getDimension(R.dimen.mana_cost_width)
        imageView.layoutParams = LayoutParams(width.toInt(), width.toInt())
        imageView.setImageResource(costToDrawable(manaCost))
        binding.cardCostContainer.addView(imageView)
    }

    private fun renderPrintedText(printedText: String) {
        val ssb = SpannableStringBuilder(printedText)

        var modifiablePrintedText = printedText
        while (modifiablePrintedText.contains('{')) {
            var start = 0
            var action = ""
            var end = 0
            for (i in modifiablePrintedText.indices) {
                if (modifiablePrintedText[i] == '{') {
                    start = i
                }

                if (start != 0 && modifiablePrintedText[i] != '{' && modifiablePrintedText[i] != '}') {
                    action += modifiablePrintedText[i].toString()
                }

                if (modifiablePrintedText[i] == '}') {
                    end = i + 1
                    break
                }
            }
            val cd =
                ResourcesCompat.getDrawable(resources, costToDrawable(action), baseContext.theme)!!
            val width = resources.getDimension(R.dimen.text_mana_cost_width)
            cd.setBounds(0, 0, width.toInt(), width.toInt())
            modifiablePrintedText = modifiablePrintedText.removeRange(start, end)
            ssb.setSpan(ImageSpan(cd, ALIGN_BASELINE), start, end, SPAN_INCLUSIVE_INCLUSIVE)
        }

        binding.cardDetailText.setText(ssb, SPANNABLE)
    }

    private fun costToDrawable(manaCost: String): Int {
        return when (manaCost) {
            "W" -> R.drawable.white_mana_symbol
            "U" -> R.drawable.blue_mana_symbol
            "B" -> R.drawable.black_mana_symbol
            "R" -> R.drawable.red_mana_symbol
            "G" -> R.drawable.green_mana_symbol
            "W/B" -> R.drawable.white_black_mana_symbol
            "1" -> R.drawable.one_mana_symbol
            else -> R.drawable.two_mana_symbol
        }
    }

    private fun getStringResource(lang: String): Int {
        return when (lang) {
            "de" -> R.string.de
            "en" -> R.string.en
            "fr" -> R.string.fr
            "it" -> R.string.it
            "la" -> R.string.la
            "pt" -> R.string.pt
            else -> com.google.android.gms.base.R.string.common_google_play_services_unsupported_text
        }
    }

    companion object {
        const val CARD_ID_INTENT_KEY = "card_id"
    }
}