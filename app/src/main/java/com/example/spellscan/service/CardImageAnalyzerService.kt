package com.example.spellscan.service

import android.graphics.Point
import com.example.spellscan.model.Card
import com.example.spellscan.model.newCard
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.TextBlock

class CardImageAnalyzerService {

    fun analyze(visionText: Text, onCardRecognized: (Card) -> Unit) {
        if (!hasEnoughBlocks(visionText.textBlocks)) {
            return
        }

        // remove invalid characters for type, name and set
        var textBlocks = removeOnlyNumbersBlocks(visionText.textBlocks)

        textBlocks = removeSpecialCharBlocks(textBlocks)

        sortByCord(textBlocks)

        val cardName = findCardName(textBlocks)

        val cardType = findCardType(textBlocks)

        val cardSet = findCardSet(textBlocks)

        val card = newCard(cardName, cardType, cardSet)

        onCardRecognized(card)
    }

    private fun hasEnoughBlocks(textBlocks: List<TextBlock>): Boolean {
        return textBlocks.size >= 5
    }

    private fun sortByCord(textBlocks: List<TextBlock>): List<TextBlock> {
        return textBlocks.sortedBy { tb ->
            val northwestPoint = tb.cornerPoints
                ?.minByOrNull { it.x }
                ?.let { point ->
                    Point(point.x, tb.cornerPoints!!.minByOrNull { it.y }?.y ?: 0)
                } ?: Point(0, 0)

            Comparator<Point> { p1, p2 ->
                compareValuesBy(p1, p2, { it.x }, { it.y })
            }.compare(northwestPoint, Point(0, 0))
        }
    }

    private fun removeOnlyNumbersBlocks(textBlocks: List<TextBlock>): List<TextBlock> {
        return textBlocks
            .filter { it.text.toIntOrNull() == null }
    }

    private fun removeSpecialCharBlocks(textBlocks: List<TextBlock>): List<TextBlock> {
        val regex = """[*/@()\[\]{}%$#]""".toRegex()

        return textBlocks
            .filter { !it.text.contains(regex) }
    }

    private fun findCardName(textBlocks: List<TextBlock>): String {
        val copy = textBlocks
            .toTypedArray()
            .copyOf(textBlocks.size)
            .filterNotNull()

        return copy
            .first { it.text.length in 2..40 }
            .text
    }

    private fun findCardType(textBlocks: List<TextBlock>): String {
        return textBlocks[1].text
    }

    private fun findCardSet(textBlocks: List<TextBlock>): String {
        val regex = "^[A-Z]{3}".toRegex()
        val setTextBlock = textBlocks.filter { regex.containsMatchIn(it.text) }
            .maxByOrNull { it.text.length }

        return setTextBlock?.text?.take(3) ?: ""
    }
}