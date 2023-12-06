package com.example.spellscan.service

import com.example.spellscan.model.Card
import io.grpc.ManagedChannel
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class CardServiceTest {

    @Mock
    lateinit var channelMock: ManagedChannel

    lateinit var cardService: CardService

    @Before
    fun setUp() {
        cardService = CardService(channelMock)
    }

    @Test
    fun find_happyPath() {
        //given
        val card = Card("name", "type", "set", "[name, type, set]")

        //when
        val result = cardService.find(card)

        //then
        assertEquals(true, result)
    }

    @Test
    fun findAll_happyPath() {
        //given
        val cards = listOf(Card("name", "type", "set", "[name, type, set]"))

        //when
        val result = cardService.findAll(cards)

        //then
        assertEquals(cards.size, result.size)
    }
}