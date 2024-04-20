package com.example.spellscanapp.util

import android.content.Context
import android.text.Spannable.SPAN_INCLUSIVE_INCLUSIVE
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.text.style.ImageSpan.ALIGN_BASELINE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.BufferType.SPANNABLE
import androidx.core.content.res.ResourcesCompat
import coil.imageLoader
import coil.request.ImageRequest
import com.example.spellscanapp.R
import com.example.spellscanapp.converter.languageToResource
import com.example.spellscanapp.converter.symbolToDrawable
import com.example.spellscanapp.db.entity.CardEntity
import com.example.spellscanapp.db.entity.CardFaceEntity
import com.example.spellscanapp.ui.viewmodel.CardServiceViewModel

suspend fun loadCard(
    cardId: String?,
    cardServiceViewModel: CardServiceViewModel,
    context: Context,
    cardCostContainer: ViewGroup,
    imageView: ImageView,
    cardNameTextView: TextView,
    cardTypeTextView: TextView,
    cardSetTextView: TextView,
    cardLangTextView: TextView,
    cardPrintedTextTextView: TextView
) {
    if (cardId == null) return

    val card = cardServiceViewModel.findById(cardId)

    renderCardDetails(
        card,
        context,
        imageView,
        cardCostContainer,
        cardNameTextView,
        cardTypeTextView,
        cardSetTextView,
        cardLangTextView,
        cardPrintedTextTextView
    )
}

fun renderFrontFace(
    card: CardEntity?,
    context: Context,
    cardCostContainer: ViewGroup,
    imageView: ImageView,
    cardNameTextView: TextView,
    cardTypeTextView: TextView,
    cardSetTextView: TextView,
    cardLangTextView: TextView,
    cardPrintedTextTextView: TextView
) {
    if (card == null || !card.hasCardFaces) return

    val face = card.cardFaces.first()

    val imageLoader = imageView.context.imageLoader
    val request = ImageRequest.Builder(imageView.context)
        .data(face.artImage)
        .target(imageView)
        .build()
    imageLoader.enqueue(request)

    loadManaCost(face.manaCost, context, cardCostContainer)

    renderPrintedText(face.printedText, context, cardPrintedTextTextView)

    cardNameTextView.text = face.name
    cardTypeTextView.text = face.typeLine
    cardSetTextView.text = card.set
    cardLangTextView.setText(languageToResource(card.lang))
}

fun renderBackFace(
    face: CardFaceEntity?,
    context: Context,
    cardNameTextView: TextView,
    cardTypeTextView: TextView,
    cardPrintedTextTextView: TextView
) {
    if (face == null) return

    renderPrintedText(face.printedText, context, cardPrintedTextTextView)

    cardNameTextView.text = face.name
    cardTypeTextView.text = face.typeLine
}

private fun renderCardDetails(
    card: CardEntity,
    context: Context,
    imageView: ImageView,
    cardCostContainer: ViewGroup,
    cardNameTextView: TextView,
    cardTypeTextView: TextView,
    cardSetTextView: TextView,
    cardLangTextView: TextView,
    cardPrintedTextTextView: TextView
) {
    val imageLoader = imageView.context.imageLoader
    val request = ImageRequest.Builder(imageView.context)
        .data(card.artImageUrl)
        .target(imageView)
        .build()
    imageLoader.enqueue(request)

    loadManaCost(card.cost, context, cardCostContainer)

    renderPrintedText(card.printedText, context, cardPrintedTextTextView)

    cardNameTextView.text = card.name
    cardTypeTextView.text = card.type
    cardSetTextView.text = card.set
    cardLangTextView.setText(languageToResource(card.lang))
}

private fun loadManaCost(
    manaCost: String,
    context: Context,
    cardCostContainer: ViewGroup
) {
    manaCost.split("}")
        .filter { it.isNotEmpty() }
        .map { it.removePrefix("{") }
        .map { renderManaCost(it, context, cardCostContainer) }
}

private fun renderManaCost(
    symbol: String,
    context: Context,
    cardCostContainer: ViewGroup
) {
    val imageView = ImageView(context)
    val width = context.resources.getDimension(R.dimen.mana_cost_width)
    imageView.layoutParams = ViewGroup.LayoutParams(width.toInt(), width.toInt())
    imageView.setImageResource(symbolToDrawable(symbol))
    cardCostContainer.addView(imageView)
}

private fun renderPrintedText(
    printedText: String,
    context: Context,
    cardDetailText: TextView
) {
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
            ResourcesCompat.getDrawable(
                context.resources,
                symbolToDrawable(action),
                context.theme
            )!!
        val width = context.resources.getDimension(R.dimen.text_mana_cost_width)
        cd.setBounds(0, 0, width.toInt(), width.toInt())
        modifiablePrintedText = modifiablePrintedText.replaceRange(start, end, "|".repeat(action.length + 2))
        ssb.setSpan(
            ImageSpan(cd, ALIGN_BASELINE), start, end,
            SPAN_INCLUSIVE_INCLUSIVE
        )
    }

    cardDetailText.setText(ssb, SPANNABLE)
}