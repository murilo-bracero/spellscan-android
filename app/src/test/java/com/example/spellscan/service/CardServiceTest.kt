package com.example.spellscan.service

import com.example.spellscan.buildCard
import com.example.spellscan.buildCardResponse
import com.google.common.util.concurrent.Futures
import com.spellscan.cardservice.CardServiceGrpc
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class CardServiceTest {

    @Mock
    lateinit var stub: CardServiceGrpc.CardServiceFutureStub

    lateinit var cardService: CardService

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        cardService = CardService(stub)
    }

    @Test
    fun find_happyPath() = runTest {
        //given
        val card = buildCard()

        //and
        `when`(stub.find(any())).thenReturn(Futures.immediateFuture(buildCardResponse()))

        //when
        val result = cardService.find(card)

        //then
        assertNotNull(result)
    }

    @Test
    fun findAll_uniqueHappyPath() = runTest {
        //given
        val cards = listOf(buildCard())

        //and
        `when`(stub.find(any())).thenReturn(Futures.immediateFuture(buildCardResponse()))

        //when
        val result = cardService.findAll(cards)

        //then
        assertEquals(cards.size, result.size)
    }

    @Test
    fun findAll_manyHappyPath() = runTest {
        //given
        val cards = listOf(buildCard(), buildCard(name = "Craterhoof Behemoth"))

        //and
        `when`(stub.find(any())).thenReturn(Futures.immediateFuture(buildCardResponse()))

        //when
        val result = cardService.findAll(cards)

        //then
        // TODO: UPDATE THIS METHOD WHEN THE CARD SERVICE WILL BE IMPLEMENTED
        assertEquals(0, result.size)

        //and
        // TODO: UPDATE THIS METHOD WHEN THE CARD SERVICE WILL BE IMPLEMENTED
        verify(stub, times(0)).find(any())
    }
}