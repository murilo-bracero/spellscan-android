package com.example.spellscanapp.service

import android.graphics.Point
import com.example.spellscanapp.model.CardReference
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.Text.TextBlock

class CardImageAnalyzerService {

    private val card_types = listOf("creature", "land", "artifact", "enchantment", "planeswalker", "battle", "instant", "sorcery", "tribal", "kindred", "interrupt", "mana source", "summon", "enchant")

    fun analyze(visionText: Text, onCardRecognized: (CardReference) -> Unit) {
        if (!hasEnoughBlocks(visionText.textBlocks)) {
            return
        }

        val lines = visionText.textBlocks
            .flatMap { it.lines }
            .filter { it.text.isNotBlank() }

        val conf = lines.filter { it.confidence > 0.5 }
            .filter { !hasSpecialChars(it.text) }
            .filter { isNotNumberString(it.text) }
            .sortedBy { comparePointsByNortheastToSouthwest(it.cornerPoints!!.toList()) }


        val cardName = findCardName(conf)

        val cardType = findCardType(conf)

        val cardSet = findCardSet(conf)

        val card = CardReference(cardName, cardType, cardSet)

        onCardRecognized(card)
    }

    private fun hasEnoughBlocks(textBlocks: List<TextBlock>): Boolean {
        return textBlocks.size >= 5
    }

    private fun comparePointsByNortheastToSouthwest(points: List<Point>): Int {
        val northwestPoint = points
            .minByOrNull { it.x }
            ?.let { point ->
                Point(point.x, points.minByOrNull { it.y }?.y ?: 0)
            } ?: Point(0, 0)

        return Comparator<Point> { p1, p2 ->
            compareValuesBy(p1, p2, { it.x }, { it.y })
        }.compare(northwestPoint, Point(0, 0))
    }

    private fun isNotNumberString(text: String): Boolean {
        return text.toIntOrNull() == null
    }

    private fun hasSpecialChars(text: String): Boolean {
        val regex = """[*/@()\[\]{}%$#]""".toRegex()

        return text.contains(regex)
    }

    private fun findCardName(lines: List<Text.Line>): String {
        return lines
            .take(lines.size / 2)
            .first { it.text.length in 2..40 }
            .text
            .replace("\n", " ")
    }

    private fun findCardType(lines: List<Text.Line>): String {
        return lines.drop(1).firstOrNull() {
            card_types.contains(it.text.lowercase())
        }?.text ?: ""
    }

    private fun findCardSet(lines: List<Text.Line>): String {
        val regex = "^[A-Z0-9]{3}".toRegex()

        val setTextBlock = lines.drop(lines.size / 2).filter { regex.containsMatchIn(it.text) }
            .maxByOrNull { it.text.length }

        return setTextBlock?.text?.take(3) ?: ""
    }
}