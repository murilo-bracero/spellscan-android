package com.example.spellscan.service

import android.graphics.Point
import com.example.spellscan.model.CardReference
import com.google.mlkit.vision.text.Text
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CardImageAnalyzerServiceTest {

    @Mock
    lateinit var mockedText: Text

    @Mock
    lateinit var mockedTextBlock1: Text.TextBlock

    @Mock
    lateinit var mockedTextBlock2: Text.TextBlock

    @Mock
    lateinit var mockedTextBlock3: Text.TextBlock

    @Mock
    lateinit var mockedTextBlock4: Text.TextBlock

    @Mock
    lateinit var mockedTextBlock5: Text.TextBlock

    private lateinit var cardImageAnalyzerService: CardImageAnalyzerService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        cardImageAnalyzerService = CardImageAnalyzerService()
    }

    @Test
    fun analyze_imageProcessedSuccessfully() {
        //given
        `when`(mockedText.textBlocks).thenReturn(
            listOf(
                mockedTextBlock1,
                mockedTextBlock2,
                mockedTextBlock3,
                mockedTextBlock4,
                mockedTextBlock5
            )
        )

        `when`(mockedTextBlock1.cornerPoints).thenReturn(arrayOf(Point(0, 0), Point(10, 0), Point(0, 10), Point(10, 10)))
        `when`(mockedTextBlock1.text).thenReturn("3")
        `when`(mockedTextBlock2.text).thenReturn("Arlinn Kord")
        `when`(mockedTextBlock3.text).thenReturn("Legendary Planeswalker - Arlinn")
        `when`(mockedTextBlock4.text).thenReturn("SOV")
        `when`(mockedTextBlock5.text).thenReturn("5")

        var capturedCard: CardReference? = null

        //when
        cardImageAnalyzerService.analyze(mockedText) {
            capturedCard = it
        }

        //then
        assertEquals("Arlinn Kord", capturedCard?.name)
        assertEquals("Legendary Planeswalker - Arlinn", capturedCard?.type)
        assertEquals("SOV", capturedCard?.set)
    }

    @Test
    fun analyze_imageWithoutMinimumBlocks() {
        //given
        `when`(mockedText.textBlocks).thenReturn(
            listOf(
                mockedTextBlock1,
                mockedTextBlock2,
                mockedTextBlock3
            )
        )

        val onCardRecognized: (CardReference) -> Unit = {
            fail("callback should not be called")
        }

        //when
        cardImageAnalyzerService.analyze(mockedText, onCardRecognized)
    }

    @Test
    fun analyze_shouldIgnoreInvalidCharacters() {
        //given
        `when`(mockedText.textBlocks).thenReturn(
            listOf(
                mockedTextBlock1,
                mockedTextBlock2,
                mockedTextBlock3,
                mockedTextBlock4,
                mockedTextBlock5
            )
        )

        `when`(mockedTextBlock1.text).thenReturn("23)")
        `when`(mockedTextBlock2.text).thenReturn("CardName321")
        `when`(mockedTextBlock3.text).thenReturn("CardType")
        `when`(mockedTextBlock4.text).thenReturn("8")
        `when`(mockedTextBlock5.text).thenReturn("SET")

        var capturedCard: CardReference? = null

        // when
        cardImageAnalyzerService.analyze(mockedText) {
            capturedCard = it
        }

        assertEquals("CardName321", capturedCard?.name)
        assertEquals("CardType", capturedCard?.type)
        assertEquals("SET", capturedCard?.set)
    }
}