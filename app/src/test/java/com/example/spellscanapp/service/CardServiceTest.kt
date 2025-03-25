package com.example.spellscanapp.service

import com.example.spellscanapp.buildCard
import com.example.spellscanapp.buildCardResponse
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
}