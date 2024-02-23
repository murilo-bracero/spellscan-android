package com.example.spellscan.repository

import android.content.Context
import androidx.room.Room
import com.example.spellscan.db.CardCacheDatabase
import com.example.spellscan.db.entity.CardEntity
import com.example.spellscan.db.entity.CardFaceEntity
import com.spellscan.cardservice.CardFaceResponse
import com.spellscan.cardservice.CardResponse

class CardCacheRepository(context: Context) {

    private val db: CardCacheDatabase

    init {
        db = Room.databaseBuilder(
            context,
            CardCacheDatabase::class.java,
            "spellscan-database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    suspend fun save(card: CardResponse): CardEntity {
        val entity = CardEntity(
            card.id,
            card.name,
            card.manaCost,
            card.type,
            card.set,
            card.lang,
            card.imageUrl,
            card.artImageUrl,
            card.releasedAt,
            card.colorsList.joinToString(separator = ","),
            card.colorIdentityList.joinToString(separator = ","),
            card.printedText,
            card.keywordsList.joinToString(separator = ","),
            hasCardFaces = card.cardFacesList.isNotEmpty(),
        )

        db.cardDao().save(entity)

        card.cardFacesList.mapIndexed { index, cardFaceResponse ->
            CardFaceEntity(
                card.id.plus("_").plus(index),
                cardFaceResponse.name,
                cardFaceResponse.manaCost,
                cardFaceResponse.typeLine,
                cardFaceResponse.printedText,
                cardFaceResponse.flavorText,
                cardFaceResponse.cardImage,
                cardFaceResponse.artImage,
                cardFaceResponse.colorsList.joinToString(separator = ","),
                cardFaceResponse.colorIndicatorList.joinToString(separator = ","),
                card.id
            )
        }.map {
            db.cardFaceDao().save(it)
            entity.cardFaces.add(it)
        }

        return entity
    }

    suspend fun findById(id: String): CardEntity {
        val card = db.cardDao().findById(id)

        if (!card.hasCardFaces) {
            return card
        }

        (0..1).map {
            db.cardFaceDao().findById(card.id.plus("_").plus(it))
        }.map {
            card.cardFaces.add(it)
        }

        return card
    }

    suspend fun findAll(): List<CardEntity> {
        return db.cardDao().findAll()
            .map { it.key.cardFaces.addAll(it.value); it.key }
    }

    suspend fun findByNameAndTypeAndSet(name: String, type: String, set: String): CardEntity? {
        return db.cardDao().findByNameAndTypeAndSet(name, type, set)
    }

    suspend fun delete(card: CardEntity) {
        db.cardDao().delete(card)
    }

    companion object {

        @Volatile
        private var instance: CardCacheRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: CardCacheRepository(context).also { instance = it }
        }
    }
}