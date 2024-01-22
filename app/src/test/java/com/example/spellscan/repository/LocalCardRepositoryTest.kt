package com.example.spellscan.repository

import com.example.spellscan.buildCard
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.UUID

class LocalCardRepositoryTest {

    private lateinit var localCardRepository: LocalCardRepository

    @Before
    fun setup() {
        localCardRepository = LocalCardRepository.getInstance()
        localCardRepository.reset()
    }

    @Test
    fun `save - happy path`() {
        //given
        val card = buildCard()

        //when
        localCardRepository.save(card)

        //then
        assertEquals(1, localCardRepository.findAll().size)
    }

    @Test
    fun `deleteById - happy path`() {
        //given
        val card = buildCard()
        localCardRepository.save(card)

        //when
        localCardRepository.deleteById(card.localId!!)

        //then
        assertEquals(0, localCardRepository.findAll().size)
    }

    @Test
    fun `deleteById - not found`() {
        //given
        val cardRow = buildCard()
        localCardRepository.save(cardRow)

        //when
        localCardRepository.deleteById(UUID.randomUUID())

        //then
        assertEquals(1, localCardRepository.findAll().size)
    }

    @Test
    fun `findAll - happy path`() {
        //given
        val card = buildCard()
        localCardRepository.save(card)

        //when
        val result = localCardRepository.findAll()

        //then
        assertEquals(1, result.size)
    }
}